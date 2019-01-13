/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uga.miashs.sempic.rdf;

/**
 *
 * @author Jerome David <jerome.david@univ-grenoble-alpes.fr>
 */
public class Namespaces {
    
    public final static String photoNS = "http://miashs.univ-grenoble-alpes.fr/photo/";
    public final static String resNS = "http://miashs.univ-grenoble-alpes.fr/resources/";
    public final static String dbo = "http://dbpedia.org/ontology/";
    public final static String dboFR = "http://fr.dbpedia.org/ontology/";
    public final static String dbr = "http://fr.dbpedia.org/resource/";
    public final static String foaf = "http://xmlns.com/foaf/0.1/";

    public static String getPhotoUri(long photoId) {
        return photoNS + photoId;
    }
}
