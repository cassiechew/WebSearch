package util;


/**
 * Contains relevant information for storage when paired with a word number in a hashmap
 */
public class LexMapping {

    //private String word;
    /** The number of documents that contain this term */
    private int noDocuments;

    /** The offset position to start reading in the inverted list file. */
    private Integer offset;



    public LexMapping(int noDocuments, Integer offset) {
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
