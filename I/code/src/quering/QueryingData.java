package quering;

import util.LexMapping;

import java.util.HashMap;


/**
 *
 */
@Deprecated
public class QueryingData {

    private static boolean lexDone = false;
    private static boolean mapDone = false;

    private static HashMap<String, LexMapping> lexicon = new HashMap<>();
    private static HashMap<Integer, String> mapping = new HashMap<>();

    public static boolean isLexDone() {
        return lexDone;
    }

    public static void setLexDone(boolean lexDone) {
        QueryingData.lexDone = lexDone;
    }

    public static boolean isMapDone() {
        return mapDone;
    }

    public static void setMapDone(boolean mapDone) {
        QueryingData.mapDone = mapDone;
    }

    public static HashMap<String, LexMapping> getLexicon() {
        return lexicon;
    }

    public static void setLexicon(HashMap<String, LexMapping> lexicon) {
        QueryingData.lexicon = lexicon;
    }

    public static HashMap<Integer, String> getMapping() {
        return mapping;
    }

    public static void setMapping(HashMap<Integer, String> mapping) {
        QueryingData.mapping = mapping;
    }
}
