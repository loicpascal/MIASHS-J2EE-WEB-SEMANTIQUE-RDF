/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.rdf;

import fr.uga.miashs.sempic.model.rdf.SempicOnto;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdfconnection.RDFConnection;
import org.apache.jena.rdfconnection.RDFConnectionFactory;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.apache.jena.sparql.modify.request.QuadAcc;
import org.apache.jena.sparql.modify.request.UpdateDeleteWhere;
import org.apache.jena.update.Update;
import org.apache.jena.vocabulary.RDFS;

/**
 *
 * @author jerome.david@univ-grenoble-alpes.fr
 */
@Stateless
public class RDFStore {

    public final static String ENDPOINT_QUERY = "http://localhost:3030/sempic/sparql"; // SPARQL endpoint
    public final static String ENDPOINT_UPDATE = "http://localhost:3030/sempic/update"; // SPARQL UPDATE endpoint
    public final static String ENDPOINT_GSP = "http://localhost:3030/sempic/data"; // Graph Store Protocol
    public final static String ENDPOINT_DBPEDIA = "http://fr.dbpedia.org/sparql"; // Dbpedia

    protected final RDFConnection cnx;

    public RDFStore() {
        cnx = RDFConnectionFactory.connect(ENDPOINT_QUERY, ENDPOINT_UPDATE, ENDPOINT_GSP);
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
        /*
        Model m = cnx.queryConstruct("CONSTRUCT { "
                + "?s <" + RDFS.label + "> ?o "
                + "} WHERE {"
                + "?s <" + RDFS.subClassOf + "> <" + c.getURI() + "> ."
                + "?s <" + RDFS.label + "> ?o ."
                + "}");
        */
        return m.listSubjects().toList();
    }
    
    
    /**
     * Retourne tous les types Depiction
     *
     * @return
     */
    public List<Resource> listDepictionClasses() {
        List<Resource> classes = this.listSubClassesOf(SempicOnto.Depiction);
        
        return classes;
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
        List<Resource> classes = this.listSubClassesOf(SempicOnto.Depiction);
        
        return classes;
    }
    
    /**
     * TODO Retourne toutes les villes françaises > 50000 habitants (dbpedia)
     *
     * @return
     */
    public List<Resource> listPopulatedPlaces()  {

        String ns = "PREFIX dbo: " + Namespaces.dbo
            + "PREFIX dbr: " + Namespaces.dbr
            + "PREFIX foaf: " + Namespaces.foaf;

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

        //cnx.addParam("timeout", "10000") ;
        
        
        Query q = QueryFactory.create(ns + query);
        Model m = cnx.queryConstruct(q); 
        
        
        //Model m = cnx.queryConstruct(ns + query);

        /*
        String dbo = "http://dbpedia.org/ontology/";
        String dbr = "http://fr.dbpedia.org/resource/";
        String foaf = "http://xmlns.com/foaf/0.1/";

        m.setNsPrefix( "dbo", dbo );
        m.setNsPrefix( "dbr", dbr );
        m.setNsPrefix( "foaf", foaf );
        */

        
        return m.listSubjects().toList();


       
        
    }
    
    
    
    
    
    /**
     * TODO Retourne toutes les villes françaises > 50000 habitants (dbpedia)
     *
     * @return
     */
    /*
    public List<Resource> listPopulatedPlacesQuery()  {
        

        
        String queryStr = "SELECT ?prop ?place WHERE { <http://dbpedia.org/resource/%C3%84lvdalen> ?prop ?place .}";
        Query query = QueryFactory.create(queryStr);

        // Remote execution.
        try ( QueryExecution qexec = QueryExecutionFactory.sparqlService("http://dbpedia.org/sparql", query) ) {
            // Set the DBpedia specific timeout.
            ((QueryEngineHTTP)qexec).addParam("timeout", "10000") ;

            // Execute.
            ResultSet rs = qexec.execSelect();
            ResultSetFormatter.out(System.out, rs, query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        

        
        return m.listSubjects().toList();


       
        
    }
    /*
    
/*    
    public String getCities() {
        // Test dbpedia
        List<
        try {
            List<Resource> places = this.listPopulatedPlaces();
            places.forEach(p -> {
                System.out.println(p);
            });
        } catch (QueryParseException qpe) {
            qpe.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        return m.listSubjects().toList();
    }
    */
    
    
    /**
     * TODO Retourne la listes des auteurs de photos (personnes
     *
     * @return
     */
    /*
    public List<Resource> listAuthors() {
        // http://dbpedia.org/ontology/PopulatedPlace
        
        //return classes;
    }
    */
 
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
     * Query a Photo and retrieve all the direct properties of the photo and if
     * the property are depic, takenIn or takenBy, it also retrieve the labels
     * of the object of these properties
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
