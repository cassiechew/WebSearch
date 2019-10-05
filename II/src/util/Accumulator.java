package util;


/**
 * A helper class to contain information relating to the accumulator
 */
public class Accumulator implements Comparable {

    /**
     * The id of the document this accumulator is related to
     */
    private int documentID;

    /**
     * The term for the Term selection value
     */
    private String queryTerm;

    /**
     * The similarity score calculated using Okapi BM 25
     */
    private double partialSimilarityScore;

    public Accumulator(int documentID, double partialSimilarityScore) {
        this.documentID = documentID;
        this.partialSimilarityScore = partialSimilarityScore;
    }

    public Accumulator(String queryTerm, double partialSimilarityScore) {
        this.queryTerm = queryTerm;
        this.partialSimilarityScore = partialSimilarityScore;
    }

    public String getQueryTerm() {
        return queryTerm;
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
        return Double.compare(((Accumulator) o).partialSimilarityScore, this.partialSimilarityScore);
    }
}