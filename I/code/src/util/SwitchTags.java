package util;

public enum SwitchTags {

    HEADLINE ("<HEADLINE>"),
    CLOSEHEAD ("</HEADLINE>"),
    TEXT ("<TEXT>"),
    CLOSETEXT ("</TEXT>"),
    DOCNO ("<DOCNO>"),
    CLOSEDOCNO ("</DOCNO>"),
    OPENDOC ("<DOC>"),
    CLOSEDOC ("</DOC>")
    ;

    private final String text;

    SwitchTags(final String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

}
