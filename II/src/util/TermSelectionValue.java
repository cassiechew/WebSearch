package util;

public class TermSelectionValue implements Comparable{

    private double TSV;


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
