/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.rdf;

import fr.uga.miashs.sempic.model.rdf.SempicOnto;
import java.util.List;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDFS;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
public class ExampleRDFStore {
    public static void main(String[] args) {
        RDFStore s = new RDFStore();

        /*
        s.cleanAllResource();

        Model m = ModelFactory.createDefaultModel();

        Resource newPerson1 = m.createResource(Namespaces.resNS + "Manuel", SempicOnto.Male);
        newPerson1.addLiteral(RDFS.label, "Manuel");
        m.write(System.out, "turtle");
 
        Resource newPerson2 = m.createResource(Namespaces.resNS + "Jerome", SempicOnto.Male);
        newPerson2.addLiteral(RDFS.label, "Jérome");
        m.write(System.out, "turtle");

        // Julie femme de Jérome
        Resource newPerson3 = m.createResource(Namespaces.resNS + "Julie", SempicOnto.Female);
        newPerson3.addLiteral(RDFS.label, "Julie");
        m.add(newPerson1, SempicOnto.hasWife, newPerson3);
        m.write(System.out, "turtle");

        // Daniel père de Jérome et Julie
        Resource newPerson4 = m.createResource(Namespaces.resNS + "Daniel", SempicOnto.Male);
        newPerson4.addLiteral(RDFS.label, "Daniel");
        m.add(newPerson4, SempicOnto.hasDaughter, newPerson3);
        m.add(newPerson4, SempicOnto.hasSon, newPerson2);
        m.write(System.out, "turtle");

        // Fatima mère de Daniel donc grand mère de Jérome et Julie
        Resource newPerson5 = m.createResource(Namespaces.resNS + "Fatima", SempicOnto.Female);
        newPerson5.addLiteral(RDFS.label, "Fatima");
        m.add(newPerson5, SempicOnto.hasSon, newPerson4);
        m.write(System.out, "turtle");

        Resource newDog = m.createResource(Namespaces.resNS + "Medor", SempicOnto.Dog);
        newDog.addLiteral(RDFS.label, "Medor");
        m.add(newPerson1, SempicOnto.owns, newDog);
        m.write(System.out, "turtle");

        
        Resource newCat = m.createResource(Namespaces.resNS + "Felix", SempicOnto.Cat);
        newCat.addLiteral(RDFS.label, "Felix");
        m.add(newPerson2, SempicOnto.owns, newCat);
        m.write(System.out, "turtle");
        
        
        Resource tika = m.createResource(Namespaces.resNS + "Tika", SempicOnto.Dog);
        tika.addLiteral(RDFS.label, "Tika");
        //m.write(System.out, "turtle");

        
        Resource newEvent1 = m.createResource(Namespaces.resNS + "Anniversaire", SempicOnto.Event);
        newEvent1.addLiteral(RDFS.label, "Anniversaire");
        m.write(System.out, "turtle");

        Resource newEvent2 = m.createResource(Namespaces.resNS + "Cremaillere", SempicOnto.Event);
        newEvent2.addLiteral(RDFS.label, "Crémaillère");
        m.write(System.out, "turtle");

        Resource newEvent3 = m.createResource(Namespaces.resNS + "PotDepart", SempicOnto.Event);
        newEvent3.addLiteral(RDFS.label, "Pôt de départ");
        m.write(System.out, "turtle");

        Resource newMonument = m.createResource(Namespaces.resNS + "TourEiffel", SempicOnto.Monument);
        newMonument.addLiteral(RDFS.label, "Tour Eiffel");
        m.write(System.out, "turtle");

        Resource newNature1 = m.createResource(Namespaces.resNS + "Montagne", SempicOnto.Nature);
        newNature1.addLiteral(RDFS.label, "Montagne");
        m.write(System.out, "turtle");

        Resource newNature2 = m.createResource(Namespaces.resNS + "Plage", SempicOnto.Nature);
        newNature2.addLiteral(RDFS.label, "Plage");
        m.write(System.out, "turtle");
        
        s.saveModel(m);

        //s.deleteModel(m);
        //s.cnx.load(m);
        
 
        System.out.println("\n\nOnthologie Depiction :\n");
        
        //List<Resource> classes = s.listSubClassesOf(SempicOnto.Depiction);
        //classes.forEach(c -> {System.out.println(c);});
        

        System.out.println("\n\n Instances Depiction :\n");
        
        //List<Resource> res = s.listInstancesByType(SempicOnto.Depiction.getURI());
        //res.forEach(r -> {System.out.println(r);});
        


        System.out.println("\n\n Création photos + annotations :\n");
        
        Resource pRes1 = s.createPhoto(1, 1, 1);
        
        s.createAnnotationObject(1, SempicOnto.depicts.getURI(), tika.getURI());
        s.createAnnotationData(1, SempicOnto.title.getURI(), "Ma super photo");
        s.createAnnotationObject(1, SempicOnto.takenIn.getURI(), Namespaces.dbr + "Grenoble");
        s.createAnnotationObject(1, SempicOnto.takenBy.getURI(), newPerson1.getURI());
        
        //List<Resource> resDepict = s.getPhotoDepictions(1);
        //resDepict.forEach(r -> {System.out.println(r);});
      
        System.out.println("\n\n listAuthors :\n");
        Resource pRes2 = s.createPhoto(2, 1, 1);
        s.createAnnotationObject(2, SempicOnto.takenBy.getURI(), newPerson2.getURI());
        
        //List<Resource> resAuthor = s.listAuthors();
        //resAuthor.forEach(r -> {System.out.println(r);});
        
        
        System.out.println("\n\n getSelfies :\n");
        s.createAnnotationObject(2, SempicOnto.depicts.getURI(), newPerson2.getURI());
        s.createAnnotationData(2, SempicOnto.title.getURI(), "Ma photo selfie");
        
        List<Resource> resSelfy = s.getSelfies();
        resSelfy.forEach(r -> {System.out.println(r);});
        
        //s.cleanAllResource(SempicOnto.Depiction.getURI());
        //s.cleanAllDepicts();
     
        s.cleanAllResourceDbpedia();
        s.createPopulatedPlaces();
  */
        // Test dbpedia
        
        System.out.println("\nDbpedia listPopulatedPlacesSelect :\n");
        try {
            List<Resource> places = s.listPopulatedPlacesSelect();
            places.forEach(p -> {
                System.out.println(p);
            });
        } catch (QueryParseException qpe) {
            qpe.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
  
        
//
//        List<Resource> instances = s.createAnonInstances(classes);
//        instances.forEach(i -> {
//            System.out.println(i.getProperty(RDFS.label));
//        });

        //s.deleteModel(m);
        //s.readPhoto(1).getModel().write(System.out,"turtle");
        // print the graph on the standard output
        //pRes.getModel().write(System.out);
    }
}
