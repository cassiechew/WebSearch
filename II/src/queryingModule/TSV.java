package queryingModule;

import util.LexMapping;
import util.TermSelectionValue;

import java.util.HashMap;


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
    public static TermSelectionValue calculateTSV(
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
     * Calculates the factorial of a number
     *
     * @param n The number to calculate the factorial
     * @return the factorial value
     */
    private static double factorialCalculation(double n) {

        if (n <= 0) return 1;
        return factorialCalculation(n - 1) * n;

    }
}
