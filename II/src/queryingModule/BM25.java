package queryingModule;

public class BM25 {

    private static double k1 = 1.2;
    private static double b = 0.75;

    public static double calculateSimilarity(double numDocuments, double freqOfDocWithTerm,
                                              double weight, double freqOfTermInDoc,
                                              double docLength, double avgDocLength) {

        return  Math.log((numDocuments-freqOfDocWithTerm+0.5)/freqOfDocWithTerm+0.5) *
                (((k1+1)*freqOfTermInDoc)/((k1*((1-b)+((b*docLength)/avgDocLength)))+freqOfTermInDoc));

    }



}
