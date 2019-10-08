package queryingModule;

import indexingModule.DocumentHandler;
import util.*;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.*;

public class QueryProcessing {

    /**
     * The minheap of accumulators to return to the main search program
     */
    private PriorityQueue<Accumulator> accumulatorMinHeap;

    /**
     * The map of accumulators to document IDs
     */
    private HashMap<Integer, Accumulator> accumulators;

    /**
     * The original queries
     */
    private Vector<String> queries;

    /**
     * The average document length
     */
    private double averageDocLength;

    /**
     * The internal lexicon file
     */
    private HashMap<String, LexMapping> lexicon;

    /**
     * The internal mapping file
     */
    private HashMap<Integer, MapMapping> mapping;

    /**
     * The file of the inverted list document
     */
    private String invlist;

    public QueryProcessing(String invlist, HashMap<String, LexMapping> lexicon, HashMap<Integer, MapMapping> mapping, double averageDocLength) {
        this.accumulatorMinHeap = new PriorityQueue<>();
        this.accumulators = new HashMap<>();
        this.invlist = invlist;
        this.lexicon = lexicon;
        this.mapping = mapping;
        this.averageDocLength = averageDocLength;
        this.queries = new Vector<>();
    }


    /**
     * A function for stopping the queries.
     *
     * @param queries The queries to run the stop function
     * @return The query to stop
     */
    private static Vector<String> stop(Vector<String> queries, String stopfile) {
        String[] queryArray = new String[queries.size()];

        DocumentHandler documentHandler = new DocumentHandler();
        Vector<String> out = new Vector<>();

        for (String s : queries) {
            out.add(documentHandler.processString(s));
        }

        documentHandler.scanStopList(stopfile);
        out.toArray(queryArray);
        queryArray = documentHandler.stoppingFunction(new StringBuilder().append(String.join(" ", queryArray))).split(" ");
        out = new Vector<>(Arrays.asList(queryArray));
        out.remove("");
        return out;
    }


    /**
     * Returns the number of accumulators needed to print as defined on user input
     *
     * @param n The number of terms to print
     * @return A queue of accumulators to print
     */
    public PriorityQueue<Accumulator> getTopNAccumulators(int n) {
        PriorityQueue<Accumulator> output = new PriorityQueue<>();
        accumulatorMinHeap.addAll(accumulators.values());

        int c = 0;

        if (n == 0) {
            return accumulatorMinHeap;
        }

        while ((c < n) && (accumulatorMinHeap.size() > 0)) {
            output.add(accumulatorMinHeap.poll());
            c++;
        }

        return output;
    }


    public void clearCache () {
        this.accumulatorMinHeap.clear();
        this.accumulators.clear();
    }

    /**
     * The function that runs the ranking of documents
     *
     * @param accumulators    The min heap of the accumulators
     * @param hasStoplist     Is there a stop list
     * @param queryTerms      The query terms to process
     * @param stopfile        The stopfile to read
     * @param queryProcessing The Query processing module
     * @param termsToPrint    The terms to pring
     */
    public static void processQueriesToAccumulators(
            PriorityQueue<Accumulator> accumulators,
            int numResultsToGet,
            Map<String, List<Document>> potentialQueries,
            double numberOfDocsInPool,
            boolean hasStoplist, Vector<String> queryTerms, String stopfile, QueryProcessing queryProcessing, Vector<String> termsToPrint) {

        if (hasStoplist) queryTerms = QueryProcessing.stop(queryTerms, stopfile);
        if (queryTerms.size() == 0) {
            System.out.println("You have not inserted a term that fits with the stoplist!");
            System.exit(1);
        }

        queryProcessing.setQueries(termsToPrint);

        queryProcessing.accumulatorCycle(Arrays.copyOf(queryTerms.toArray(), queryTerms.size(), String[].class), potentialQueries, numberOfDocsInPool);

        accumulators.addAll(queryProcessing.getTopNAccumulators(numResultsToGet));

    }


    /**
     * Umbrella method for generating accumulators.
     *
     *
     * @param queryTerms The terms for the query
     */
    private void accumulatorCycle(String[] queryTerms, Map<String, List<Document>> potentialQueries, double numberOfDocsInPool) {
        this.accumulators = new HashMap<>();
        try (
                RandomAccessFile invlist = new RandomAccessFile(new File(this.invlist), "r");
        ) {

            for (String query : queryTerms) {
                generateAccumulators(query, invlist, potentialQueries, numberOfDocsInPool);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates accumulators for a single query term
     *
     * @param query   The query term
     * @param invlist The inverted list file
     */
    private void generateAccumulators(
            String query, RandomAccessFile invlist, Map<String, List<Document>> potentialQueries,
            double numberOfDocsInPool) throws IOException {

        final int NOBYTES = 4;

        LexMapping lexMapping;

        int[] intStore;
        int noIntsToRead;

        if (!lexicon.containsKey(query)) {
            System.out.println("This term does not exist in our documents! " + query);
            //System.exit(1);
            return;
        }

        lexMapping = lexicon.get(query);
        noIntsToRead = 2 * lexMapping.getNoDocuments();
        intStore = new int[noIntsToRead];

        processInvlistData(intStore, noIntsToRead, NOBYTES, invlist, lexMapping);

        if (this.queries.contains(query)) {
            calculateAccumulators(intStore, lexMapping, true, potentialQueries, numberOfDocsInPool, query);
        } else {
            calculateAccumulators(intStore, lexMapping, false, potentialQueries, numberOfDocsInPool, query);
        }
    }


    /**
     * Parses the inverted list for data relating to the query term
     *
     * @param intStore     The place to store the pulled data
     * @param noIntsToRead The number of integers to read
     * @param NOBYTES      The size of the integers to read
     * @param invlist      The inverted list data
     * @param lexMapping   The lexicon mapping refer to util.LexMapping
     * @throws IOException Throws an IOException
     */
    private static void processInvlistData(int[] intStore, int noIntsToRead, final int NOBYTES, RandomAccessFile invlist, LexMapping lexMapping)
            throws IOException {
        invlist.seek(lexMapping.getOffset());

        int pointer = 0;
        for (int i = 0; i < noIntsToRead; i++) {

            //get docID and frequency
            byte[] docIDFrequency = new byte[NOBYTES];
            //reading bytes and stored in byte []
            invlist.read(docIDFrequency);
            //convert byte array to bytebuffer
            ByteBuffer wrapped = ByteBuffer.wrap(docIDFrequency);
            int output = wrapped.getInt();
            intStore[i] = (i % 2 == 0) ? output + pointer : output;
            pointer += (i % 2 == 0) ? output : 0;

        }
    }


    private void setQueries(Vector<String> queries) {
        for (String s : queries) {
            this.queries.add(s.toLowerCase());
        }
    }

    /**
     * Calculates the Accumulators for a term
     *
     * @param intStore   The storage of the pulled ints from the invlist
     * @param lexMapping The lexicon mapping refer to util.LexMapping
     */
    private void calculateAccumulators(int[] intStore, LexMapping lexMapping, boolean okapi, Map<String, List<Document>> potentialQueries, double numberOfDocsInPool, String query) {

        for (int i = 0; i < intStore.length; i += 2) {
            MapMapping mapMapping = mapping.get(intStore[i]);

            if (!accumulators.containsKey(intStore[i])) {

                accumulators.put(intStore[i], new Accumulator(intStore[i], (okapi) ?
                        BM25.calculateSimilarity(mapping.size(), lexMapping.getNoDocuments(), intStore[i + 1], mapMapping.getDocumentWeight(), averageDocLength)
                        :
                        RSJ.calculateRJSSimilarity(lexMapping, mapping.size(), potentialQueries.get(query).size(), numberOfDocsInPool)));

            } else {
                Accumulator accumulator = accumulators.get(intStore[i]);

                double similarityScore = (okapi) ?
                        BM25.calculateSimilarity(mapping.size(), lexMapping.getNoDocuments(), intStore[i + 1], mapMapping.getDocumentWeight(), averageDocLength)
                        :
                        RSJ.calculateRJSSimilarity(lexMapping, mapping.size(), potentialQueries.get(query).size(), numberOfDocsInPool);


                accumulator.setPartialSimilarityScore(similarityScore);
            }
        }
    }
}
