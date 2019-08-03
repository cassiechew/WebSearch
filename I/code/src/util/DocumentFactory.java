package util;

import java.util.Map;

public class DocumentFactory {



    private Map<Integer, Document> documentRegistry;



    public DocumentFactory (Map<Integer, Document> documentRegistry) {
        super();

        this.documentRegistry = documentRegistry;
    }


    public void registerDocument (Integer documentID, Document document) {

        this.documentRegistry.put(documentID, document);

    }


    public Document createDocument (String documentNo, String heading, String textData) {
        return new Document (documentNo, documentRegistry.size() , heading, textData, this);
    }


}
