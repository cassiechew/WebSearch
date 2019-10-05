package queryingModule;

import util.LexMapping;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TSV {

    //TODO make new class to hold tsv results
    public static double calculateRJSSimilarity () {

    }

    public static double calculateTSV (
            String query, HashMap<String, LexMapping> lexicon, RandomAccessFile invlist,
            double numberOfDocumentsInCollection, List<Integer> listOfDocumentPoolContainingT)
            throws IOException {
        System.out.println(query);
        LexMapping lexMapping = lexicon.get(query);
        if(Objects.isNull(lexMapping)) return 0;
        //System.out.println(lexMapping + " " + query);
        int noIntsToRead = 2 * lexMapping.getNoDocuments();
        int[] intStore = new int[noIntsToRead];
        QueryProcessing.processInvlistData(intStore, noIntsToRead, 4, invlist,lexMapping);

        double frequencyOfTermInDocumentCollection = 0;

        for (int i = 0; i < intStore.length; i+=2) {
            if (listOfDocumentPoolContainingT.contains(intStore[i])) {
                frequencyOfTermInDocumentCollection += 1;
            }
//            frequencyOfTermInDocumentCollection += intStore[i];
        }

        return Math.pow(lexMapping.getNoDocuments()/numberOfDocumentsInCollection, frequencyOfTermInDocumentCollection)*
                (factorialCalculation(listOfDocumentPoolContainingT.size())
                        /
                        (factorialCalculation(frequencyOfTermInDocumentCollection)*factorialCalculation(listOfDocumentPoolContainingT.size()-frequencyOfTermInDocumentCollection)));

    }


    public static double factorialCalculation (double n) {

        if (n <= 0) return 1;
        return factorialCalculation(n-1)*n;

    }


}
