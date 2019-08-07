package util;

import java.util.Map;


/**
 * A factory to streamline the production of documents in a consistent way
 */
public class DocumentFactory {


    /** The internal registry of all created documents */
    private Map<Integer, Document> documentRegistry;


    /**
     * The constructor of the factory
     * @param documentRegistry the initial instantiation of the document registry
     */
    public DocumentFactory (Map<Integer, Document> documentRegistry) {
        super();
        this.documentRegistry = documentRegistry;
    }


    /**
     * Registers a document with the registry with the supplied ID
     * @param documentID The ID to register with
     * @param document THe document to register
     */
    void registerDocument(Integer documentID, Document document) {
        this.documentRegistry.put(documentID, document);
    }


    /**
     * Creates a document and returns it
     * @param documentNo The external ID of the document
     * @param heading The heading of the document
     * @param textData The text content of the document
     * @return The newy created document
     */
    public Document createDocument (String documentNo, String heading, String textData) {
        return new Document(documentNo, documentRegistry.size() , heading, textData, this);
    }

    /**
     * Returns the document registry
     * @return the document registry
     */
    public Map<Integer, Document> getDocumentRegistry () {
        return this.documentRegistry;
    }
}
