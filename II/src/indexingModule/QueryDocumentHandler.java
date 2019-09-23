package indexingModule;

import queryingModule.BM25;
import util.LexMapping;
import util.MapMapping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;


/**
 * An auxiliary class to handle file IO for the search module
 */
public class QueryDocumentHandler {

    /** The internal lexicon file */
    private HashMap<String, LexMapping> lexicon = new HashMap<>();

    /** The internal mapping file */
    private HashMap<Integer, MapMapping> mapping = new HashMap<>();

    private int documentCounter = 0;

    /** An enum of file types that this class will have to deal with */
    public enum fileType {
        LEXICON,
        MAP;
    }

    public HashMap<String, LexMapping> getLexicon() {
        return lexicon;
    }

    public HashMap<Integer, MapMapping> getMapping() {
        return mapping;
    }

    /**
     * Generates the indexing data from the inverted list files
     * @param fileToRead The name of the file to read
     * @param fileType The type of the file to read
     * @see fileType
     */
    public void generateIndexDataFromFiles (String fileToRead, fileType fileType, Vector<String> queryTerms) {



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
                        String term = splitStringData[0];
                        lexicon.put(term, new LexMapping(Integer.parseInt(splitStringData[1]), Integer.parseInt(splitStringData[2])));

                        break;
                    case MAP:
                        MapMapping mapMapping = new MapMapping(splitStringData[1]);
                        mapping.put(Integer.parseInt((splitStringData[0])), mapMapping);

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
