package quering;

import util.LexMapping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


/**
 * An auxiliary class to handle file IO for the search module
 */
public class QueryDocumentHandler {

    /** The internal lexicon file */
    private HashMap<String, LexMapping> lexicon = new HashMap<>();

    /** The internal mapping file */
    private HashMap<Integer, String> mapping = new HashMap<>();

    /** An enum of file types that this class will have to deal with */
    public enum fileType {
        LEXICON,
        MAP;
    }

    public HashMap<String, LexMapping> getLexicon() {
        return lexicon;
    }

    public HashMap<Integer, String> getMapping() {
        return mapping;
    }

    /**
     * Generates the indexing data from the inverted list files
     * @param fileToRead The name of the file to read
     * @param fileType The type of the file to read
     * @see fileType
     */
    public void generateIndexDataFromFiles (String fileToRead, fileType fileType) {


        try (
                BufferedReader bufferedReader = new BufferedReader(new FileReader(fileToRead));
                //put lexicon into memory
        ) {
            String buffer;
            while ((buffer = bufferedReader.readLine()) != null) {
                String[] splitStringData = buffer.split(" ");
                //convert 2nd/3rd to Int and Int
                switch (fileType) {
                    case LEXICON:
                        lexicon.put(splitStringData[0], new LexMapping(Integer.parseInt(splitStringData[1]), Integer.parseInt(splitStringData[2])));
                        break;
                    case MAP:
                        mapping.put(Integer.parseInt((splitStringData[0])), splitStringData[1]);
                        break;
                    default:
                        System.out.println("Failed to read");
                        System.exit(1);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
