package util;

import java.util.Vector;


/**
 * Contains relevant information for mapping data and needed document data for the OKAPI BM25
 */
public class MapMapping {

    /**
     * The name of the document in its raw ID
     */
    private String documentNameID;

    /**
     * The weight of the document being the length of the document
     */
    private int documentWeight;

    /**
     * The line number of where the document begins
     */
    private int documentLocationPointer;


    public MapMapping(String documentNameID, int documentWeight, int documentLocationPointer) {
        this.documentNameID = documentNameID;
        this.documentWeight = documentWeight;
        this.documentLocationPointer = documentLocationPointer;
    }

    public int getDocumentLocationPointer() {
        return documentLocationPointer;
    }

    public String getDocumentNameID() {
        return documentNameID;
    }

    public int getDocumentWeight() {
        return documentWeight;
    }
}
