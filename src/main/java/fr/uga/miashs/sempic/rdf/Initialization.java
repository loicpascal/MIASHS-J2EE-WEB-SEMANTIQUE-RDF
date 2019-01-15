/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.rdf;

import fr.uga.miashs.sempic.model.rdf.SempicOnto;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;

/**
 * Initialisation des ressources d'annotations et des villes récupérées dans Dbpedia
 * 
 * @author Matthieu Ramage
 */
public class Initialization {
    public static void main(String[] args) {
        RDFStore s = new RDFStore();
        
        // Initialisation des ressources d'annotations
        s.cleanAllDepicts();
        s.cleanAllAnnotationResource();
        
        // Supprime toutes les ressources, y compris les photos
        //s.cleanAllResource();

        Model m = ModelFactory.createDefaultModel();

        // Manuel
        Resource newPerson1 = m.createResource(Namespaces.resNS + "Manuel", SempicOnto.Male);
        newPerson1.addLiteral(RDFS.label, "Manuel");
        m.write(System.out, "turtle");
 
        // Jerome
        Resource newPerson2 = m.createResource(Namespaces.resNS + "Jerome", SempicOnto.Male);
        newPerson2.addLiteral(RDFS.label, "Jérome");
        m.write(System.out, "turtle");

        // Julie femme de Manuel, soeur de Jérome
        Resource newPerson3 = m.createResource(Namespaces.resNS + "Julie", SempicOnto.Female);
        newPerson3.addLiteral(RDFS.label, "Julie");
        m.add(newPerson1, SempicOnto.hasWife, newPerson3);
        m.add(newPerson3, SempicOnto.hasBrother, newPerson2);
        m.write(System.out, "turtle");

        // Daniel père de Jérome et Julie
        Resource newPerson4 = m.createResource(Namespaces.resNS + "Daniel", SempicOnto.Male);
        newPerson4.addLiteral(RDFS.label, "Daniel");
        m.add(newPerson4, SempicOnto.hasDaughter, newPerson3);
        m.add(newPerson4, SempicOnto.hasSon, newPerson2);
        m.write(System.out, "turtle");

        // Fatima mère de Daniel, grand mère de Jérome et Julie
        Resource newPerson5 = m.createResource(Namespaces.resNS + "Fatima", SempicOnto.Female);
        newPerson5.addLiteral(RDFS.label, "Fatima");
        m.add(newPerson5, SempicOnto.hasSon, newPerson4);
        m.add(newPerson5, SempicOnto.hasGrandSon, newPerson2);
        m.add(newPerson5, SempicOnto.hasGrandDaughter, newPerson3);
        m.write(System.out, "turtle");

        // Medor chien de Manuel
        Resource newDog = m.createResource(Namespaces.resNS + "Medor", SempicOnto.Dog);
        newDog.addLiteral(RDFS.label, "Medor");
        m.add(newPerson1, SempicOnto.hasDog, newDog);
        m.write(System.out, "turtle");
        
        // Felix chat de Jerome
        Resource newCat = m.createResource(Namespaces.resNS + "Felix", SempicOnto.Cat);
        newCat.addLiteral(RDFS.label, "Felix");
        m.add(newPerson2, SempicOnto.hasCat, newCat);
        m.write(System.out, "turtle");
        
        // Tika chien de personne
        Resource tika = m.createResource(Namespaces.resNS + "Tika", SempicOnto.Dog);
        tika.addLiteral(RDFS.label, "Tika");
        m.write(System.out, "turtle");
        
        // Television un produit
        Resource tele = m.createResource(Namespaces.resNS + "Tele", SempicOnto.Product);
        tele.addLiteral(RDFS.label, "Télévision");
        m.write(System.out, "turtle");

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

        // Initialisation des villes récupérées dans Dbpedia
        s.cleanAllResourceDbpedia();
        s.createPopulatedPlaces();
    }
}
