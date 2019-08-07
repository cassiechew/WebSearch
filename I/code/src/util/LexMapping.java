package util;

public class LexMapping {

    private String word;
    private Integer offset;

    public LexMapping (String word, Integer offset) {
        this.word   = word;
        this.offset = offset;
    }

    public String getWord() {
        return word;
    }

    public Integer getOffset() {
        return offset;
    }

}
