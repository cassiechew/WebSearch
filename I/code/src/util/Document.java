package util;

import java.util.UUID;

public class Document {

    /** DOCNO */
    private String documentNo;
    /** Unique Identifier */
    private UUID documentID;

    private String headline;
    private String textData;

    public Document (String documentNo, UUID documentID, String headline, String textData) {
        super();
        this.documentNo = documentNo;
        this.documentID = documentID;
        this.headline = headline;
        this.textData = textData;
    }


    public String getDocumentNo() {
        return documentNo;
    }

    public UUID getDocumentID() {
        return documentID;
    }

    public String getHeadline() {
        return headline;
    }

    public String getTextData() {
        return textData;
    }
}
