package util;

import java.util.Vector;

public class MapMapping {

    /** The name of the document in its raw ID */
    private String documentNameID;

    /** The number of documents that contain this term */
    //private Vector<Double> docWeights;

    private static String[] queryTerms;
    private int documentWeight;



    public MapMapping(String documentNameID, int documentWeight) {
        this.documentNameID = documentNameID;
        this.documentWeight = documentWeight;
        //this.docWeights = new Vector<>();

    }

    public static void setQueryTerms(String[] queryTerms) {
        MapMapping.queryTerms = queryTerms;
    }

    public void addTermWeight (double i) {
        //this.docWeights.addElement(i);
    }

    public String getDocumentNameID() {
        return documentNameID;
    }

    public int getDocumentWeight() {
        return documentWeight;
    }
}
