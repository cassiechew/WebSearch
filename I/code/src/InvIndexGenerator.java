import util.Document;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Generates the inverted list from the provided documents.
 *
 */
public class InvIndexGenerator {



    /** Contains each unique term that occurs in the collection and a “pointer” to the inverted list for that term */
    private File lexiconFile;

    /** Contains the inverted list information, consisting only of numerical data */
    private File invlistsFile;

    /** Contains the mapping information from document id numbers
     * (as used in the inverted lists) to the actual document names */
    private File mapFile;


    private Map<String, Map<Integer, Integer>> lexiconInvlist;


    public InvIndexGenerator (String lexiconFilename, String invlistFilename, String mapFilename) {

        lexiconFile = new File(lexiconFilename);
        invlistsFile = new File(invlistFilename);
        mapFile = new File(mapFilename);

    }


    //go through header and body text to count words

    public void createList (List<Document> documentList) {

        for (Document d : documentList
             ) {

            //store document number, frequency it appears here


            for (String s : d.getHeadline().split(" ")) {
                if (!lexiconInvlist.containsKey(s)) {
                    lexiconInvlist.put(s, new HashMap<>(d.getDocumentID(), 1));
                }
                else {
                    if (lexiconInvlist.get(s).containsKey(d.getDocumentID())) {
                        lexiconInvlist.get(s).replace(d.getDocumentID(), lexiconInvlist.get(s).get(d.getDocumentID()));
                    }
                    else {
                        lexiconInvlist.get(s).put(d.getDocumentID(), 1);
                    }
                }
            }

            for (String s : d.getTextData().split(" ")) {
                if (!lexiconInvlist.containsKey(s)) {
                    lexiconInvlist.put(s, new HashMap<>(d.getDocumentID(), 1));
                }
                else {
                    if (lexiconInvlist.get(s).containsKey(d.getDocumentID())) {
                        lexiconInvlist.get(s).replace(d.getDocumentID(), lexiconInvlist.get(s).get(d.getDocumentID()));
                    }
                    else {
                        lexiconInvlist.get(s).put(d.getDocumentID(), 1);
                    }
                }
            }
        }

    }

    //create lexicon
    //create inverted list



}
