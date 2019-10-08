package queryingModule;

import indexingModule.DocumentHandler;
import util.Accumulator;
import util.Document;
import util.LexMapping;
import util.MapMapping;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static queryingModule.QueryDocumentHandler.fileType.MAP;


/**
 * An auxiliary class to handle file IO for the search module
 */
public class QueryDocumentHandler {

    /**
     * The internal lexicon file
     */
    private HashMap<String, LexMapping> lexicon = new HashMap<>();

    /**
     * The internal mapping file
     */
    private HashMap<Integer, MapMapping> mapping = new HashMap<>();

    private double totalDocumentsLength = 0;
    private double totalDocuments = 0;

    /**
     * Average Document Length
     */
    private double averageDocumentLength;

    /**
     * An enum of file types that this class will have to deal with
     */
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

    public double getAverageDocumentLength() {
        return averageDocumentLength;
    }


    /**
     * Generates the indexing data from the inverted list files
     *
     * @param fileToRead The name of the file to read
     * @param fileType   The type of the file to read
     * @see fileType
     */
    public void generateIndexDataFromFiles(String fileToRead, fileType fileType) {

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
                        lexiconProcess(splitStringData);
                        break;
                    case MAP:
                        mappingProcess(splitStringData);
                        break;
                    default:
                        System.out.println("Failed to read");
                        System.exit(1);
                }
            }

            if (fileType.equals(MAP)) {
                this.averageDocumentLength = totalDocumentsLength / totalDocuments;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * A function to process the lexicon file data
     *
     * @param splitStringData The split line from the lexicon file
     */
    private void lexiconProcess(String[] splitStringData) {
        String term = splitStringData[0];
        lexicon.put(term, new LexMapping(Integer.parseInt(splitStringData[1]), Integer.parseInt(splitStringData[2])));
    }


    /**
     * A function to process the map file data
     *
     * @param splitStringData The split string from the map file
     */
    private void mappingProcess(String[] splitStringData) {
        int documentLength = Integer.parseInt(splitStringData[2]);
        int documentLocationPointer = Integer.parseInt(splitStringData[3]);
        totalDocumentsLength += documentLength;
        totalDocuments += 1;
        MapMapping mapMapping = new MapMapping(splitStringData[1], documentLength, documentLocationPointer);
        mapping.put(Integer.parseInt((splitStringData[0])), mapMapping);

    }


    /**
     * Gets the offsets of the documents
     *
     * @param accumulators
     * @param listOfDocumentsContainingT
     * @param documentMappings
     * @param offsets
     */
    public void getDocumentOffsets(
            PriorityQueue<Accumulator> accumulators,
            List<Accumulator> listOfDocumentsContainingT,
            Map<Integer, MapMapping> documentMappings,
            Map<Integer, Integer> offsets,
            int noRelevantDocs
    ) {

        for (int i = 0; i < noRelevantDocs && accumulators.size() > 0; i++) {
            Accumulator accumulator = accumulators.poll();

            assert accumulator != null;
            listOfDocumentsContainingT.add(accumulator);

            int start = documentMappings.get(Objects.requireNonNull(accumulator).getDocumentID()).getDocumentLocationPointer();
            int end = documentMappings.get(Objects.requireNonNull(accumulator).getDocumentID() + 1).getDocumentLocationPointer();

            offsets.put(start, end);
        }
    }


    /**
     * Processes the initial document collection to get the related documents of the initial query
     *
     * @param offsets The offsets for the beginning and ending of documents
     * @param documentHandler The document handler to read files
     * @param documentsOfPool The document pool container
     */
    public void getDocumentsOfPool(
            Map<Integer, Integer> offsets,
            DocumentHandler documentHandler,
            List<Document> documentsOfPool
    ) {

        for (int l : offsets.keySet()) {
            List<Document> docs = documentHandler.readFile(l, offsets.get(l));
            documentsOfPool.addAll(docs);
        }
    }


    /**
     * Process the documents to get the potential queries from all documents
     *
     * @param documentsOfPool The documents of the intial first pass of querying
     * @param potentialQueries A container for the new queries
     */
    public void processDocuments(List<Document> documentsOfPool, Map<String, List<Document>> potentialQueries) {

        for (Document d : documentsOfPool) {
            String[] words = d.getAllText().split(" ");
            for (String s : words) {
                if (!potentialQueries.containsKey(s)) {
                    Vector<Document> docList = new Vector<>();
                    docList.add(d);
                    potentialQueries.put(s, docList);
                } else {
                    if (!potentialQueries.get(s).contains(d)) {
                        potentialQueries.get(s).add(d);
                    }
                }
            }
        }
    }
}