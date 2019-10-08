package queryingModule;

import util.LexMapping;

class RSJ {


    /**
     * Calculates the Robert-Sparck Jones similarity score for a document
     *
     * @param lexMapping                    The lexicon mapping for a document
     * @param numberOfDocumentsInCollection The number of documents in the collection
     * @param listOfDocumentPoolContainingT The number of documents in the initial pool containing the query term
     * @param numberOfDocsInPool            The number of documents in the initial pool
     * @return The RJS Similarity score
     */
    static double calculateRJSSimilarity(LexMapping lexMapping,
                                         double numberOfDocumentsInCollection,
                                         double listOfDocumentPoolContainingT,
                                         double numberOfDocsInPool) {

        double a = (listOfDocumentPoolContainingT + 0.5);
        double b = (numberOfDocumentsInCollection - lexMapping.getNoDocuments() - numberOfDocsInPool + listOfDocumentPoolContainingT + 0.5);
        double c = (lexMapping.getNoDocuments() - listOfDocumentPoolContainingT + 0.5);
        double d = (numberOfDocsInPool - listOfDocumentPoolContainingT + 0.5);

        double out = Math.log((a * b) / (c * d));

        return (out <= 0) ? 0 : out / 3.0;
    }

}
