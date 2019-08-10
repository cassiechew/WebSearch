package util;

public class LexMapping {

    //private String word;
    private int noDocuments;
    private Integer offset;



    public LexMapping (int noDocuments, Integer offset) {
        //this.word   = word;
        this.noDocuments = noDocuments;
        this.offset = offset;
    }

    //public String getWord() {
        //return word;
    //}

    public int getNoDocuments() {
        return noDocuments;
    }

    public Integer getOffset() {
        return offset;
    }

}
