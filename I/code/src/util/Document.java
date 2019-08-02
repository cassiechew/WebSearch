package util;

import java.util.UUID;

public class Document {

    /** DOCNO */
    private String documentNo;
    /** Unique Identifier */
    private int documentID;

    private String headline;
    private String textData;

    public Document (String documentNo, int documentID, String headline, String textData) {
        super();
        this.documentNo = documentNo;
        this.documentID = documentID;
        this.headline = headline;
        this.textData = textData;
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


    public void printDoc () {
        System.out.println(documentID);
        System.out.println(documentNo);
        System.out.println(headline);
        System.out.println(textData);
        System.out.println();
    }
}
