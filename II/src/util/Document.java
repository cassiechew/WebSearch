package util;


/**
 * A util.Document type that contains relevant information
 */
public class Document {

    /** DOCNO */
    private String documentNo;
    /** Unique Identifier */
    private int documentID;

    private String headline;
    private String textData;

    private int documentLength;


    public Document (String documentNo, int documentID, String headline, String textData, int documentWeight, DocumentFactory documentFactory) {
        super();
        this.documentNo = documentNo;
        this.documentID = documentID;
        this.headline = headline;
        this.textData = textData;
        this.documentLength = documentWeight;
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

    public int getDocumentLength() { return documentLength; }

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
