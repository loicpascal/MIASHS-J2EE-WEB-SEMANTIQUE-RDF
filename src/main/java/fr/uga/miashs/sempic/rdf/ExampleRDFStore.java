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
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.RDFS;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
public class ExampleRDFStore {
    public static void main(String[] args) {
        RDFStore s = new RDFStore();

        System.out.println("\n\nOnthologie Depiction :\n");
        
        List<Resource> classesSelect = s.listDepictionClasses();
        classesSelect.forEach(c -> {
            System.out.println(c);
            System.out.println(c.getProperty(RDFS.label, "fr").getLiteral());
        });

        System.out.println("\n\n Instances Depiction :\n");
        
        //List<Resource> res = s.listInstancesByType(SempicOnto.Depiction.getURI());
        //res.forEach(r -> {System.out.println(r);});
        

        System.out.println("\n\n Création photos + annotations :\n");
        /*
        Resource pRes1 = s.createPhoto(1, 1, 1);
        Resource pRes2 = s.createPhoto(2, 1, 1);
        
        s.createAnnotationData(1, SempicOnto.title.getURI(), "Ma super photo");
        s.createAnnotationObject(1, SempicOnto.takenIn.getURI(), Namespaces.dbr + "Grenoble");
        s.createAnnotationData(2, SempicOnto.title.getURI(), "Ma photo selfie");
        */
        
        /*
        //s.createAnnotationObject(1, SempicOnto.depicts.getURI(), tika.getURI());

        //s.createAnnotationObject(1, SempicOnto.takenBy.getURI(), newPerson1.getURI());
        
        
        List<Resource> resDepict = s.getPhotoDepictions(1);
        resDepict.forEach(r -> {System.out.println(r);});
        
        System.out.println("\n\n Object Properties :\n");

        List<Resource> oP = s.getObjectPropertyByDomain("http://miashs.univ-grenoble-alpes.fr/ontologies/sempic.owl#Person");
        oP.forEach(r -> {System.out.println(r);});
      
        System.out.println("\n\n listAuthors :\n");
        
        //s.createAnnotationObject(2, SempicOnto.takenBy.getURI(), newPerson2.getURI());
        
        //List<Resource> resAuthor = s.listAuthors();
        //resAuthor.forEach(r -> {System.out.println(r);});
        
        
        System.out.println("\n\n getSelfies :\n");
        //s.createAnnotationObject(2, SempicOnto.depicts.getURI(), newPerson2.getURI());
        //
        
        List<Resource> resSelfy = s.getSelfies();
        resSelfy.forEach(r -> {System.out.println(r);});
        
        */
        

        //s.cleanAllResource(SempicOnto.Depiction.getURI());
        
     
        // Test dbpedia
        /*
        System.out.println("\nDbpedia listPopulatedPlacesConstruct :\n");
        try {
            List<Resource> places = s.listPopulatedPlacesConstruct();
            places.forEach(p -> {
                System.out.println(p);
                System.out.println(p.getProperty(FOAF.name, "fr").getLiteral());
            });
        } catch (QueryParseException qpe) {
            qpe.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        
        System.out.println("\nDbpedia listPopulatedPlaces :\n");
        try {
            List<Resource> placesSelect = s.listPopulatedPlaces();
            for (Resource p : placesSelect) {
                System.out.println(p.getProperty(FOAF.name, "fr").getLiteral());
                System.out.println(p);
            }
            
            placesSelect.forEach(p -> {
                System.out.println(p.getProperty(FOAF.name, "fr").getLiteral());
                System.out.println(p);
            });
            
        } catch (QueryParseException qpe) {
            qpe.printStackTrace();
        } catch(Exception e) {
            e.printStackTrace();
        }
        */
  
  
        
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
