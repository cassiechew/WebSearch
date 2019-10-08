package util;


/**
 * This is a utility class that holds the term selection value for query terms
 */
public class TermSelectionValue implements Comparable{


    /**
     * The term selection value calculated the TSV class
     */
    private double TSV;


    /**
     * The query term
     */
    private String name;


    public TermSelectionValue (String name, double TSV) {
        this.name = name;
        this.TSV = TSV;
    }

    public double getTSV() {
        return TSV;
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Object o) {
        return Double.compare(((TermSelectionValue)o).TSV, this.TSV);
    }
}
