package queryingModule;

public class BM25 {

    private static double k1 = 1.2;
    private static double b = 0.75;

    public static double calculateSimilarity(double weight, double freqOfTermInDoc,
                                              double docLength, double avgDocLength) {

        return  weight * (((k1+1)*freqOfTermInDoc)/((k1*((1-b)+((b*docLength)/avgDocLength)))+freqOfTermInDoc));

    }

    public static double calculateWeight(double numDocuments,double freqOfDocWithTerm) {
        return Math.log((numDocuments-freqOfDocWithTerm+0.5)/freqOfDocWithTerm+0.5);
    }


    public static void calculateAllDocumentWeights () {

    }


}
