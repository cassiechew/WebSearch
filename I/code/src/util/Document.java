package util;

import java.util.UUID;


/**
 * A Document type that contains relevant information
 */
public class Document {

    /** DOCNO */
    private String documentNo;
    /** Unique Identifier */
    private int documentID;

    private String headline;
    private String textData;


    public Document (String documentNo, int documentID, String headline, String textData, DocumentFactory documentFactory) {
        super();
        this.documentNo = documentNo;
        this.documentID = documentID;
        this.headline = headline;
        this.textData = textData;
        documentFactory.registerDocument(documentID, this);
    }


    public String getDocumentNo() {
        return documentNo;
    }

    public int getDocumentID() {
        return documentID;
    }

    public String getHeadline() {
        return headline;
    }

    public String getTextData() {
        return textData;
    }


    /**
     * Prints the document
     */
    public void printDoc () {
        System.out.println(documentID);
        System.out.println(documentNo);
        System.out.println(headline);
        System.out.println(textData);
        System.out.println();
    }
}
