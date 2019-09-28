package util;


/**
 * The tags that will be used to flag a skip in word processing
 */
public enum SkipTags {

    PARA ("<P>"),
    CLOSEPARA ("</P>"),
    HEADLINE ("<HEADLINE>"),
    TEXT ("<TEXT>"),
            ;

    private final String text;

    SkipTags(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
