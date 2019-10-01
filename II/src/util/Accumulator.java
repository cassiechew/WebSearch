package util;

public class Accumulator implements Comparable{

    private int documentID;

    private double partialSimilarityScore = 0;

    public Accumulator (int documentID, double partialSimilarityScore) {
        this.documentID = documentID;
        this.partialSimilarityScore = partialSimilarityScore;
    }

    public void setPartialSimilarityScore(double partialSimilarityScore) {
        this.partialSimilarityScore += partialSimilarityScore;
    }

    public double getPartialSimilarityScore() {
        return partialSimilarityScore;
    }

    public int getDocumentID() {
        return documentID;
    }

    @Override
    public int compareTo(Object o) {
        Accumulator temp = (Accumulator)o;
        return Double.compare(((Accumulator) o).partialSimilarityScore, this.partialSimilarityScore);
    }
}
