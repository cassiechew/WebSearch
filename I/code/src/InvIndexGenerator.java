import java.io.File;


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


    public InvIndexGenerator (String lexiconFilename, String invlistFilename, String mapFilename) {

        lexiconFile = new File(lexiconFilename);
        invlistsFile = new File(invlistFilename);
        mapFile = new File(mapFilename);

    }


    


}
