/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.rdf;

import fr.uga.miashs.sempic.model.rdf.SempicOnto;
import java.util.List;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
public class ExampleRDFStore {
    public static void main(String[] args) {
        RDFStore s = new RDFStore();

        Model m = ModelFactory.createDefaultModel();

        Resource pRes1 = s.createPhoto(1, 1, 1);
        Resource pRes2 = s.createPhoto(2, 1, 1);

        Resource newTele = m.createResource(SempicOnto.Tele);
        newTele.addLiteral(RDFS.label, "Samsung");
        m.write(System.out, "turtle");

        Resource newPerson = m.createResource(SempicOnto.Person);
        newPerson.addLiteral(RDFS.label, "Meme");
        m.write(System.out, "turtle");

        Resource newDog = m.createResource(SempicOnto.Dog);
        newDog.addLiteral(RDFS.label, "Medor");
        m.add(pRes1, SempicOnto.depicts, newDog);
        m.add(newPerson, SempicOnto.owns, newDog);
        m.write(System.out, "turtle");

        Resource newCat = m.createResource(SempicOnto.Cat);
        newCat.addLiteral(RDFS.label, "Felix");
        m.add(pRes2, SempicOnto.depicts, newCat);
        m.add(newPerson, SempicOnto.owns, newCat);
        m.write(System.out, "turtle");

        s.saveModel(m);
        
        //s.deleteModel(m);
        //s.cnx.load(m);
//        List<Resource> classes = s.listSubClassesOf(SempicOnto.Depiction);
//        classes.forEach(c -> {System.out.println(c);});
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
