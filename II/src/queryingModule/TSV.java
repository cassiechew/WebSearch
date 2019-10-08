package queryingModule;

import util.Document;
import util.LexMapping;
import util.MapMapping;
import util.TermSelectionValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;


public class TSV {

    /**
     * Calculates the Term selection value of a term.
     *
     * @param query                         The query to calculate TSV for
     * @param lexicon                       The lexicon word data
     * @param numberOfDocumentsInCollection The number of documents in the collection
     * @param listOfDocumentPoolContainingT The list of documents in the pool containing the query
     * @param numberOfDocsInPool            The number of documents in the initial pool
     * @return The TSV
     */
    private static TermSelectionValue calculateTSV(
            String query, HashMap<String, LexMapping> lexicon,
            double numberOfDocumentsInCollection, double listOfDocumentPoolContainingT, double numberOfDocsInPool) {


        LexMapping lexMapping = lexicon.get(query);
        if (lexMapping == null) return new TermSelectionValue(query, 0);


        double TSV = Math.pow(lexMapping.getNoDocuments() / numberOfDocumentsInCollection, listOfDocumentPoolContainingT) *
                (factorialCalculation(numberOfDocsInPool)
                        /
                        (factorialCalculation(listOfDocumentPoolContainingT) *
                                factorialCalculation(numberOfDocsInPool - listOfDocumentPoolContainingT)));

        return new TermSelectionValue(query, TSV);
    }


    /**
     * Calculates the Term selection values of each string
     *
     * @param potentialQueries A map of potential queries with their containing documents
     * @param queryCandidates A container for the query candidates
     * @param queryDocumentHandler The document handler for queries
     * @param documentMappings The mappings of documents
     * @param noDocsInPool The number of documents in the pool
     */
    public static void calculateTSVs(Map<String, List<Document>> potentialQueries,
                                      PriorityQueue<TermSelectionValue> queryCandidates,
                                      QueryDocumentHandler queryDocumentHandler,
                                      Map<Integer, MapMapping> documentMappings,
                                      int noDocsInPool
    ) {

        for (String s : potentialQueries.keySet()) {
            queryCandidates.add(
                    calculateTSV(
                            s,
                            queryDocumentHandler.getLexicon(),
                            documentMappings.size(),
                            potentialQueries.get(s).size(),
                            noDocsInPool)
            );
        }
    }


    /**
     * Calculates the factorial of a number
     *
     * @param n The number to calculate the factorial
     * @return the factorial value
     */
    private static double factorialCalculation(double n) {

        if (n <= 1) return 1;

        int out = 1;

        for (int i = 1; i <= n; i++) {
            out *= i;
        }

        return out;

    }
}
