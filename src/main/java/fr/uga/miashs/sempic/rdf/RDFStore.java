/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.rdf;

import fr.uga.miashs.sempic.model.rdf.SempicOnto;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.modify.request.QuadAcc;
import org.apache.jena.sparql.modify.request.UpdateDeleteWhere;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.update.Update;
import org.apache.jena.vocabulary.RDFS;

/**
 *
 * @author jerome.david@univ-grenoble-alpes.fr
 */
@Stateless
public class RDFStore {

    // Endpoints base sempic
    public final static String ENDPOINT_QUERY = "http://localhost:3030/sempic/sparql"; // SPARQL endpoint
    public final static String ENDPOINT_UPDATE = "http://localhost:3030/sempic/update"; // SPARQL UPDATE endpoint
    public final static String ENDPOINT_GSP = "http://localhost:3030/sempic/data"; // Graph Store Protocol
    
    // Endpoints base sempic-dbpedia
    public final static String ENDPOINT_DBPEDIA_QUERY = "http://localhost:3030/sempic-dbpedia/sparql"; // SPARQL endpoint
    public final static String ENDPOINT_DBPEDIA_UPDATE = "http://localhost:3030/sempic-dbpedia/update"; // SPARQL UPDATE endpoint
    public final static String ENDPOINT_DBPEDIA_GSP = "http://localhost:3030/sempic-dbpedia/data"; // Graph Store Protocol
    
     // Endpoint fr.dbpedia
    public final static String ENDPOINT_DBPEDIA = "http://fr.dbpedia.org/sparql";
    
    protected final RDFConnection cnx; // base sempic
    protected final RDFConnection cnxDbpedia; // base sempic-dbpedia
        
    private List<Resource> places = new ArrayList<Resource>();

    public RDFStore() {
        cnx = RDFConnectionFactory.connect(ENDPOINT_QUERY, ENDPOINT_UPDATE, ENDPOINT_GSP);
        cnxDbpedia = RDFConnectionFactory.connect(ENDPOINT_DBPEDIA_QUERY, ENDPOINT_DBPEDIA_UPDATE, ENDPOINT_DBPEDIA_GSP);
    }

    /**
     * Supprime toutes les ressources
     */
    public void cleanAllResource() {
        cnx.begin(ReadWrite.WRITE);
        cnx.update("DELETE WHERE { ?s ?p ?o }");
        cnx.commit();
    }
    
    /**
     * Supprime toutes les ressources par type
     * TODO : ne marche pas !! Doit-on supp les instances des sous-types avant ?
     * 
     * @param typeUri
     */
    public void cleanAllResource(String typeUri) {
        cnx.begin(ReadWrite.WRITE);
        cnx.update("DELETE WHERE { ?s a <" + typeUri + "> }");
        
        
        debug("cleanAllResource", "DELETE WHERE { ?s a <" + typeUri + "> }");
        
        cnx.commit();
    }
    
    /**
     * Supprime toutes les ressources (base sempic-dbpedia)
     */
    public void cleanAllResourceDbpedia() {
        cnxDbpedia.begin(ReadWrite.WRITE);
        cnxDbpedia.update("DELETE WHERE { ?s ?p ?o }");
        cnxDbpedia.commit();
    }
    
    /**
     * Save the given model into the triple store.
     * @param m THe Jena model to be persisted
     */
    public void saveModel(Model m) {
        cnx.begin(ReadWrite.WRITE);
        cnx.load(m);
        cnx.commit();
    }
    
    /**
     * Save the given model into the triple store.
     * @param m THe Jena model to be persisted
     */
    public void saveModelDbpedia(Model m) {
        cnxDbpedia.begin(ReadWrite.WRITE);
        cnxDbpedia.load(m);
        cnxDbpedia.commit();
    }

    /**
     * Delete the given model from the triple store. Be carreful: Blank nodes
     * are replaced by variables
     * @param m the model to be deleted
     */
    public void deleteModel(Model m) {
        HashMap<Resource, Var> map = new HashMap<>();
        QuadAcc acc = new QuadAcc();
        m.listStatements().forEachRemaining(st -> {
            if (st.getSubject().isAnon() || st.getObject().isAnon()) {
                Node s = blankNodeAsVariable(st.getSubject(), map);
                Node p = st.getPredicate().asNode();
                Node o = blankNodeAsVariable(st.getObject(), map);

                acc.addTriple(new Triple(s, p, o));
            } else {

                acc.addTriple(st.asTriple());
            }
        });

        Update u = new UpdateDeleteWhere(acc);
        //System.out.println(u);
        cnx.begin(ReadWrite.WRITE);
        cnx.update(u);
        cnx.commit();
    }

    private Node blankNodeAsVariable(Resource r, Map<Resource, Var> bnMap) {
        if (r.isAnon()) {
            Var v = bnMap.get(r);
            if (v == null) {
                bnMap.put(r, v = Var.alloc("A" + bnMap.size()));
            }
            return v;
        }
        return r.asNode();
    }

    private Node blankNodeAsVariable(RDFNode r, Map<Resource, Var> bnMap) {
        if (r.isAnon()) {
            return blankNodeAsVariable(r.asResource(), bnMap);
        }
        return r.asNode();
    }
    
    private void debug(String s, Object o) {
        System.out.println("\n\n" + s + " :\n" + o);
    }

    /**
     * Delete all the statements where the resource appears as subject or object
     * @param r The named resource to be deleted (the resource cannot be annonymous)
     */
    public void deleteResource(Resource r) {
        if (r.isURIResource()) {
            cnx.begin(ReadWrite.WRITE);
            cnx.update("DELETE WHERE { <" + r.getURI() + "> ?p ?o }");
            cnx.update("DELETE WHERE { ?s ?p <" + r.getURI() + "> }");
            cnx.commit();
        }
    }
    
   /**
    * Delete all the statements where the resource appears as subject and property uri appears as property
    * @param r The named resource to be deleted (the resource cannot be annonymous)
    */
   public void deleteResource(Resource r, String pURI) {
       if (r.isURIResource()) {
           cnx.begin(ReadWrite.WRITE);
           cnx.update("DELETE WHERE { <" + r.getURI() + "> <" + pURI + "> ?o }");
           cnx.commit();
       }
   }
   
   /**
    * Supprime toutes les annotations "depicts"
    */
   public void cleanAllDepicts() {
        cnx.begin(ReadWrite.WRITE);
        cnx.update("DELETE WHERE { ?s <" + SempicOnto.depicts + "> ?o }");
        cnx.commit();
   }
   
   /* 
    * Delete all the statements where the resource appears as subject and object uri appears as object
    * @param r The named resource to be deleted (the resource cannot be annonymous)
    */

    public void deleteAnnotationByObject(Resource r, String pURI) {
        if (r.isURIResource()) {
            cnx.begin(ReadWrite.WRITE);
            cnx.update("DELETE WHERE { <" + r.getURI() + "> <" + SempicOnto.depicts + "> <" + pURI + "> }");
            cnx.commit();
        }
    }
   
    /**
     * Retieves all the resources that are subclasses of resource c. To be
     * selected classes must have the property rdfs:label instanciated
     *
     * @param c A named class (the resource cannot be annonymous)
     * @return
     */
    public List<Resource> listSubClassesOf(Resource c) {
        
        String queryStr = "CONSTRUCT { "
                + "?s <" + RDFS.label + "> ?o "
                + "} WHERE {"
                + "?s <" + RDFS.subClassOf + "> <" + c.getURI() + "> ."
                + "?s <" + RDFS.label + "> ?o ."
                + "}";
        Query query = QueryFactory.create(queryStr);
        Model m = cnx.queryConstruct(query);

        return m.listSubjects().toList();
    }
    
    /**
     * Retourne tous les types Depiction
     *
     * @return
     */
    public List<Resource> listDepictionClasses() {
        String queryStr = "CONSTRUCT { "
                + "?s <" + RDFS.label + "> ?o "
                + "} WHERE {"
                + "?s <" + RDFS.subClassOf + "> <" + SempicOnto.Depiction.getURI() + "> ."
                + "?s <" + RDFS.label + "> ?o ."
                + " FILTER (?s != <" + SempicOnto.Depiction.getURI() + "> )"
                + "}";
        Query query = QueryFactory.create(queryStr);
        query.addOrderBy("?o", Query.ORDER_ASCENDING);
        debug("Query listDepictionClasses", queryStr);
        Model m = cnx.queryConstruct(query);

        return m.listSubjects().toList();
    }
    
    /**
     * Retourne tous les types Who
     *
     * @return
     */
    public List<Resource> listWhoDepictionClasses() {
        List<Resource> classes = this.listSubClassesOf(SempicOnto.Depiction);
        
        return classes;
    }
    
    /**
     * Retourne tous les types What
     *
     * @return
     */
    public List<Resource> listWhatDepictionClasses() {
        List<Resource> classes;
        classes = this.listSubClassesOf(SempicOnto.Depiction);
      
        return classes;
    }
    
    /**
     * Retourne les instances d'un type donné
     *
     * @param typeUri
     * @return
     */
    public List<Resource> listInstancesByType(String typeUri) {
        String queryStr = "CONSTRUCT { "
                + "?s <" + RDFS.label + "> ?name "
                + "} WHERE {"
                + "?s a <" + typeUri + "> ;"
                +      "<" + RDFS.label + "> ?name"
                + "}";
        Query query = QueryFactory.create(queryStr);
        
        debug("Query listInstancesByType", queryStr);

        Model m = cnx.queryConstruct(query);

        return m.listSubjects().toList();
    }
    
    /**
     * Retourne la liste des auteurs de photos
     *
     * @return
     */
    public List<Resource> listAuthors() {
        String queryStr = "CONSTRUCT { "
                + "    ?s <" + RDFS.label + "> ?name "
                + "} WHERE {"
                + "    ?s a <" + SempicOnto.Person + "> ;"
                +          "<" + RDFS.label + "> ?name."
                + "    ?s2 <" + SempicOnto.takenBy + "> ?s"
                + "}";
        Query query = QueryFactory.create(queryStr);
        
        Model m = cnx.queryConstruct(query);

        return m.listSubjects().toList();
    }
    
    /**
     * Retourne la liste des selfies (une photo qui décrit la personne qui est auteur de cette photo)
     *
     * @return
     */
    public List<Resource> getSelfies() {
        String queryStr = "CONSTRUCT { "
                + "    ?photo <" + SempicOnto.depicts + "> ?person "
                + "} WHERE {"
                + "    ?photo <" + SempicOnto.depicts + "> ?person ;"
                + "           <" + SempicOnto.takenBy + "> ?person."
                + "}";
        Query query = QueryFactory.create(queryStr);
        
        Model m = cnx.queryConstruct(query);

        return m.listSubjects().toList();
    }
    
    /**
     * Enregistre dans une base locale toutes les villes françaises > 50000 habitants (dbpedia)
     */
    public void createPopulatedPlaces()  {
        String ns = "PREFIX dbo: <" + Namespaces.dbo + ">"
            + "PREFIX dbr: <" + Namespaces.dbr + ">"
            + "PREFIX foaf: <" + Namespaces.foaf + ">";

        String query = "CONSTRUCT {"
            + "        ?place foaf:name ?place_name"
            + "   }"
            + "   WHERE {"
            + "           SERVICE <" + ENDPOINT_DBPEDIA + ">"
            + "           {"
            + "                   ?place dbo:country dbr:France ."
            + "                   ?place foaf:name ?place_name ."
            + "                   ?place a dbo:PopulatedPlace. "
            + "                   ?place dbo:populationTotal ?population ."
            + "           }"
            + "           FILTER (?population > 50000)"
            + "   }"
            + "   order by ?place_name";
        
        Query q = QueryFactory.create(ns + query);
        
        Model m = cnxDbpedia.queryConstruct(q); 
        
        this.saveModelDbpedia(m);
    }
    
    /**
     * Retourne toutes les villes françaises > 50000 habitants
     *
     * @return
     */
    public List<Resource> listPopulatedPlacesConstruct()  {
        String query = "CONSTRUCT {"
            + "       ?place ?prop ?name"
            + "   }"
            + "   WHERE {"
            + "       ?place ?prop ?name"
            + "   }";
        
        Query q = QueryFactory.create(query);
        
        Model m = cnxDbpedia.queryConstruct(q); 
        
        List<Resource> placesTmp = m.listSubjects().toList();

        return placesTmp;
    }
    
    /**
     * Retourne toutes les villes françaises > 50000 habitants triées par nom
     *
     * @return
     */
    public List<Resource> listPopulatedPlaces()  {
        String query = "SELECT ?place ?prop ?name"
            + "   WHERE {"
            + "       ?place ?prop ?name"  
            + "   }"
            + "   order by ?name";
        
        Query q = QueryFactory.create(query);

        Model m = ModelFactory.createDefaultModel();
 
        cnxDbpedia.querySelect(q, (qs) -> {
            Resource subject = qs.getResource("place") ;
            subject.addProperty(FOAF.name, qs.getLiteral("name"));
            places.add(subject);
        }) ;

        return places;
        
       /*
        TODO Permettrait de se passer de l'attribut places
        
        ResultSet rs = qExec.execSelect() ;
        while(rs.hasNext()) {
            QuerySolution qs = rs.next() ;
            Resource subject = qs.getResource("s") ;
            System.out.println("Subject: "+subject) ;
        }
        */
    }

    /**
     * Create a list of anonymous instances for each of the classes
     * given as parameter. The created instances have a label "a "+ label of the class.
     * @param classes
     * @return 
     */
    public List<Resource> createAnonInstances(List<Resource> classes) {
        Model m = ModelFactory.createDefaultModel();
        List<Resource> res = new ArrayList<>();
        for (Resource c : classes) {
            Resource instance = m.createResource(c);
            instance.addLiteral(RDFS.label, "a " + c.getProperty(RDFS.label).getLiteral());
            res.add(instance);
        }
        
        return res;
    }
    
    /**
     * Crée une annotation avec une propriété objet
     * 
     * TODO gérer exeption si uri inconnue ?
     *
     * @param photoId
     * @param pUri
     * @param oUri
     * @return
     */
    public Resource createAnnotationObject(long photoId, String pUri, String oUri) {
        Model m = ModelFactory.createDefaultModel();

        String photoUri = Namespaces.getPhotoUri(photoId);
        Resource photo = m.getResource(photoUri);
        Property prop = m.getProperty(pUri);
        Resource object = m.getResource(oUri);
        
        m.add(photo, prop, object);
        this.saveModel(m);
        
        //debug("photo", photo);debug("Property", prop);debug("object", object);
        
        //m.write(System.out, "turtle");
        
        return photo;
    }
    
    
    /**
     * Crée une annotation avec une propriété data
     *
     * TODO gérer exeption si uri inconnue ?
     * 
     * @param photoId
     * @param pUri
     * @param l
     * @return
     */
    public Resource createAnnotationData(long photoId, String pUri, String l) {
        Model m = ModelFactory.createDefaultModel();
        
        String photoUri = Namespaces.getPhotoUri(photoId);
        Resource photo = m.getResource(photoUri);
        Property prop = m.getProperty(pUri);
        
        photo.addLiteral(prop, l);
        this.saveModel(m);
        
        return photo;
    }
    
    /**
     * Crée une annotation avec une propriété data
     *
     * TODO gérer exeption si uri inconnue ?
     * 
     * @param photoId
     * @param pUri
     * @param l
     * @return
     */
    public Resource createAnnotationDataUsingDate(long photoId, String pUri, Calendar d) {
        Model m = ModelFactory.createDefaultModel();
        
        String photoUri = Namespaces.getPhotoUri(photoId);
        Resource photo = m.getResource(photoUri);
        Property prop = m.getProperty(pUri);
        
        photo.addLiteral(prop, d);
        this.saveModel(m);
        
        return photo;
    }

    /**
     * Crée une photo
     * 
     * @param photoId
     * @param albumId
     * @param ownerId
     * @return 
     */
    public Resource createPhoto(long photoId, long albumId, long ownerId) {
        // create an empty RDF graph
        Model m = ModelFactory.createDefaultModel();
        // create an instance of Photo in Model m
        Resource pRes = m.createResource(Namespaces.getPhotoUri(photoId), SempicOnto.Photo);

        pRes.addLiteral(SempicOnto.albumId, albumId);
        pRes.addLiteral(SempicOnto.ownerId, ownerId);

        saveModel(m);

        return pRes;
    }

    /**
     * Liste des annotations "depicts" d'une photo
     *
     * @param id
     * @return
     */
    public List<Resource> getPhotoDepictions(long id) {
        String pUri = Namespaces.getPhotoUri(id);

        String s = "CONSTRUCT {"
                + "    ?o <" + RDFS.label + "> ?name "
                + "} "
                + "WHERE { "
                + "    <" + pUri + "> <" + SempicOnto.depicts + "> ?o . "
                + "    ?o <" + RDFS.label + "> ?name"
                + "}";
        Model m = cnx.queryConstruct(s);
 
        return m.listSubjects().toList();
        
    }
    
    /**
     * A virer ?
     *
     * @param id
     * @return
     */
    public Resource getPhotoObjectByProperty(long id, String pUri) {
        String photoUri = Namespaces.getPhotoUri(id);

        String s = "CONSTRUCT {"
                + "<" + photoUri + "> <" + pUri + "> ?o . "
                + "} "
                + "WHERE { "
                + "<" + photoUri + "> <" + pUri + "> ?o . "
                + "}";
        Model m = cnx.queryConstruct(s);
        
        return m.getResource(pUri);
    }
    
    /**
     * Query a Photo and retrieve all the direct properties of the photo and if
     * the property are depic, takenIn or takenBy, it also retrieve the labels
     * of the object of these properties
     * 
     *
     * @param id
     * @return
     */
    public Resource readPhoto(long id) {
        String pUri = Namespaces.getPhotoUri(id);

        String s = "CONSTRUCT {"
                + "<" + pUri + "> ?p ?o . "
                + "<" + pUri + "> ?p1 ?o1 . "
                + "?o1 <" + RDFS.label + "> ?o2 . "
                + "} "
                + "WHERE { "
                + "<" + pUri + "> ?p ?o . "
                + "OPTIONAL {"
                + "<" + pUri + "> ?p1 ?o1 ."
                + "?o1 <" + RDFS.label + "> ?o2 ."
                + "FILTER (?p1 IN (<" + SempicOnto.depicts + ">,<" + SempicOnto.takenIn + ">,<" + SempicOnto.takenBy + ">)) "
                + "}"
                + "}";
        Model m = cnx.queryConstruct(s);
        
        return m.getResource(pUri);
    }

    
}
