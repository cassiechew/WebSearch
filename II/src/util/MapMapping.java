package util;

import java.util.Vector;

public class MapMapping {

    /** The name of the document in its raw ID */
    private String documentNameID;

    /** The number of documents that contain this term */
    private Vector<Double> docWeights;



    public MapMapping(String documentNameID) {
        this.documentNameID = documentNameID;
        this.docWeights = new Vector<>();

    }

    public void addTermWeight (double i) {
        this.docWeights.addElement(i);
    }




}
