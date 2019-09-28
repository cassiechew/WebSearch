package util;

public class Accumulator {

    private int documentID;

    private double partialSimilarityScore;

    public Accumulator (int documentID, double partialSimilarityScore) {
        this.documentID = documentID;
        this.partialSimilarityScore = partialSimilarityScore;
    }

    public void setPartialSimilarityScore(double partialSimilarityScore) {
        this.partialSimilarityScore += partialSimilarityScore;
    }
}
