package queryingModule;

/**
 * A Simple Okapi BM25 function implementation
 */
class BM25 {

    /**
     * The similarity calculation
     *
     * @param numDocuments      The number of documents in the collection
     * @param freqOfDocWithTerm The number of documents that contain the term
     * @param freqOfTermInDoc   The frequency of the term in the document
     * @param docLength         The length of the document
     * @param avgDocLength      The average document length
     * @return The calculated similarity score of the document to the query term using okapi BM25
     */
    static double calculateSimilarity(double numDocuments, double freqOfDocWithTerm,
                                      double freqOfTermInDoc,
                                      double docLength, double avgDocLength) {
        /* The k1 weight */
        double k1 = 1.2;

        /* The b weight */
        double b = 0.75;

        return Math.log(
                (numDocuments - freqOfDocWithTerm + 0.5) / freqOfDocWithTerm + 0.5
        ) *
                (((k1 + 1) * freqOfTermInDoc) / ((k1 * ((1 - b) + ((b * docLength) / avgDocLength))) + freqOfTermInDoc));

    }
}