/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.rdf;

import fr.uga.miashs.sempic.model.rdf.SempicOnto;
import org.apache.jena.ontology.impl.ObjectPropertyImpl;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.impl.PropertyImpl;
import org.apache.jena.rdf.model.impl.ResourceImpl;
import org.apache.jena.sparql.vocabulary.FOAF;
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
        newPerson1.addLiteral(FOAF.age, 20);
 
        // Jerome
        Resource newPerson2 = m.createResource(Namespaces.resNS + "Jerome", SempicOnto.Male);
        newPerson2.addLiteral(RDFS.label, "Jérome");
        newPerson2.addLiteral(FOAF.age, 20);

        // Julie femme de Manuel, soeur de Jérome
        Resource newPerson3 = m.createResource(Namespaces.resNS + "Julie", SempicOnto.Female);
        newPerson3.addLiteral(RDFS.label, "Julie");
        newPerson3.addLiteral(FOAF.age, 25);
        m.add(newPerson1, SempicOnto.hasWife, newPerson3);
        m.add(newPerson3, SempicOnto.hasBrother, newPerson2);

        // Daniel père de Jérome et Julie
        Resource newPerson4 = m.createResource(Namespaces.resNS + "Daniel", SempicOnto.Male);
        newPerson4.addLiteral(RDFS.label, "Daniel");
        newPerson4.addLiteral(FOAF.age, 30);
        m.add(newPerson4, SempicOnto.hasDaughter, newPerson3);
        m.add(newPerson4, SempicOnto.hasSon, newPerson2);

        // Fatima mère de Daniel, grand mère de Jérome et Julie
        Resource newPerson5 = m.createResource(Namespaces.resNS + "Fatima", SempicOnto.Female);
        newPerson5.addLiteral(RDFS.label, "Fatima");
        newPerson5.addLiteral(FOAF.age, 30);
        m.add(newPerson5, SempicOnto.hasSon, newPerson4);
        m.add(newPerson5, SempicOnto.hasGrandSon, newPerson2);
        m.add(newPerson5, SempicOnto.hasGrandDaughter, newPerson3);

        // SonGoku ami de Daniel
        Resource goku = m.createResource(Namespaces.resNS + "SonGoku", SempicOnto.Male);
        goku.addLiteral(RDFS.label, "SonGoku");
        m.add(goku, SempicOnto.isFriend, newPerson4);
        
        // Laika chien de SonGoku
        Resource laika = m.createResource(Namespaces.resNS + "Laika", SempicOnto.Dog);
        laika.addLiteral(RDFS.label, "Laika");
        m.add(goku, SempicOnto.hasDog, laika);
        
        // Medor chien de Manuel
        Resource newDog = m.createResource(Namespaces.resNS + "Medor", SempicOnto.Dog);
        newDog.addLiteral(RDFS.label, "Medor");
        m.add(newPerson1, SempicOnto.hasDog, newDog);
        
        // Felix chat de Jerome
        Resource newCat = m.createResource(Namespaces.resNS + "Felix", SempicOnto.Cat);
        newCat.addLiteral(RDFS.label, "Felix");
        m.add(newPerson2, SempicOnto.hasCat, newCat);
        
        // Tika chien de Julie
        Resource tika = m.createResource(Namespaces.resNS + "Tika", SempicOnto.Dog);
        tika.addLiteral(RDFS.label, "Tika");
        Property colour = new PropertyImpl(Namespaces.dbo + "colour"); // propriété dbpedia colour
        Resource yellow = new ResourceImpl(Namespaces.dbr + "Yellow"); // ressource dbpedia Yellow
        tika.addProperty(colour, yellow);
        m.add(newPerson3, SempicOnto.owns, tika);
        
        // Babouche chat de Julie
        Resource babouche = m.createResource(Namespaces.resNS + "Babouche", SempicOnto.Cat);
        babouche.addLiteral(RDFS.label, "Babouche");
        babouche.addProperty(colour, yellow);
        m.add(newPerson3, SempicOnto.owns, babouche);
        
        // Babar animal de Julie
        Resource babar = m.createResource(Namespaces.resNS + "Babar", SempicOnto.Animal);
        babar.addLiteral(RDFS.label, "Babar");
        Resource purple = new ResourceImpl(Namespaces.dbr + "Purple"); // ressource dbpedia Purple
        babar.addProperty(colour, purple);
        m.add(newPerson3, SempicOnto.owns, babar);
        
        // Television un produit
        Resource tele = m.createResource(Namespaces.resNS + "Tele", SempicOnto.Product);
        tele.addLiteral(RDFS.label, "Télévision");

        // Evenements
        Resource newEvent1 = m.createResource(Namespaces.resNS + "Anniversaire", SempicOnto.Event);
        newEvent1.addLiteral(RDFS.label, "Anniversaire");

        Resource newEvent2 = m.createResource(Namespaces.resNS + "Cremaillere", SempicOnto.Event);
        newEvent2.addLiteral(RDFS.label, "Crémaillère");

        Resource newEvent3 = m.createResource(Namespaces.resNS + "PotDepart", SempicOnto.Event);
        newEvent3.addLiteral(RDFS.label, "Pôt de départ");

        // Monument
        Resource newMonument = m.createResource(Namespaces.resNS + "TourEiffel", SempicOnto.Monument);
        newMonument.addLiteral(RDFS.label, "Tour Eiffel");
        
        Resource bastille = m.createResource(Namespaces.resNS + "Bastille", SempicOnto.Monument);
        bastille.addLiteral(RDFS.label, "Bastille");

        // Nature
        Resource newNature1 = m.createResource(Namespaces.resNS + "Montagne", SempicOnto.Nature);
        newNature1.addLiteral(RDFS.label, "Montagne");

        Resource newNature2 = m.createResource(Namespaces.resNS + "Plage", SempicOnto.Nature);
        newNature2.addLiteral(RDFS.label, "Plage");
        
        Resource desert = m.createResource(Namespaces.resNS + "Desert", SempicOnto.Nature);
        desert.addLiteral(RDFS.label, "Desert");
        
        m.write(System.out, "turtle");
        s.saveModel(m);

        // Initialisation des villes récupérées dans Dbpedia
        s.cleanAllResourceDbpedia();
        s.createPopulatedPlaces();
    }
}
