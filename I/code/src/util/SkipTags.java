package util;

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
