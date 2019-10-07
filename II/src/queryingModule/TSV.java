package queryingModule;

import util.Accumulator;
import util.LexMapping;
import util.TermSelectionValue;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TSV {

    static double calculateRJSSimilarity(LexMapping lexMapping,
                                         double numberOfDocumentsInCollection, double listOfDocumentPoolContainingT, double numberOfDocsInPool) {

        double a = (listOfDocumentPoolContainingT+0.5);
        double b = (numberOfDocumentsInCollection-lexMapping.getNoDocuments()-numberOfDocsInPool+listOfDocumentPoolContainingT+0.5);
        double c = (lexMapping.getNoDocuments()-listOfDocumentPoolContainingT+0.5);
        double d = (numberOfDocsInPool-listOfDocumentPoolContainingT+0.5);

        double out = Math.log((a*b)/(c*d));
//        System.out.println(">    " + out);
//        System.out.println("$    " + a + " " + b + " " + c + " " + d);
//        System.out.println("@    " + lexMapping.getNoDocuments() + " " + numberOfDocsInPool + " " + listOfDocumentPoolContainingT);

//        System.out.println(lexMapping.getNoDocuments() + " " + numberOfDocumentsInCollection + " "+  listOfDocumentPoolContainingT + " " + numberOfDocsInPool);
        //        System.out.println("<    " + returnVal);

        return (out <= 0) ? 0 : out/3.0;
    }

    public static TermSelectionValue calculateTSV (
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


    private static double factorialCalculation(double n) {

        if (n <= 0) return 1;
        return factorialCalculation(n-1)*n;

    }


}
