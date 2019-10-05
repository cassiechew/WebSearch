package queryingModule;

import util.Accumulator;
import util.Compressor;
import util.LexMapping;
import util.MapMapping;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.*;

public class QueryProcessing {

    private PriorityQueue<Accumulator> accumulatorMinHeap;

    private HashMap<Integer, Accumulator> accumulators;

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
        if (n == 0) return accumulatorMinHeap;

        while (c < n && (accumulatorMinHeap.size() > 0)) {
            output.add(accumulatorMinHeap.poll());
            c++;
        }

        return output;
    }

    /**
     * Umbrella method for generating accumulators.
     *
     * TODO: Adda parameter to flag when to use okapi or the robert jones spark
     *
     * @param queryTerms The terms for the query
     */
    public void accumulatorCycle(String[] queryTerms, boolean okapi) {
        this.accumulators = new HashMap<>();
        try (
                RandomAccessFile invlist = new RandomAccessFile(new File(this.invlist), "r");
        ) {

            for (String query : queryTerms) {
                generateAccumulators(query, invlist, okapi);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Generates accumulators for a single query term
     *
     * @param query The query term
     * @param invlist The inverted list file
     */
    private void generateAccumulators(String query, RandomAccessFile invlist, boolean okapi) throws IOException {

        final int NOBYTES = 4;

        LexMapping lexMapping;

        int[] intStore;
        int noIntsToRead;

        boolean docFreqSwitch = false;


        if (!lexicon.containsKey(query)) {
            System.out.println("This term does not exist in our documents! " + query);
            //System.exit(1);
            return;
        }

        lexMapping = lexicon.get(query);
        noIntsToRead = 2 * lexMapping.getNoDocuments();
        intStore = new int[noIntsToRead];

        processInvlistData(intStore, noIntsToRead, NOBYTES, invlist, lexMapping);
        calculateAccumulators(intStore, lexMapping, okapi);

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
    static void processInvlistData(int[] intStore, int noIntsToRead, final int NOBYTES, RandomAccessFile invlist, LexMapping lexMapping)
            throws IOException {
        invlist.seek(lexMapping.getOffset());


        for (int i = 0; i < noIntsToRead; i++) {

            //get docID and frequency
            byte[] docIDFrequency = new byte[NOBYTES];
            //reading bytes and stored in byte []
            invlist.read(docIDFrequency);
            //convert byte array to bytebuffer
            ByteBuffer wrapped = ByteBuffer.wrap(docIDFrequency);
            int output = wrapped.getInt();
            intStore[i] = output;

        }
    }

    /**
     * Calculates the Accumulators for a term
     *
     * @param intStore   The storage of the pulled ints from the invlist
     * @param lexMapping The lexicon mapping refer to util.LexMapping
     */
    private void calculateAccumulators(int[] intStore, LexMapping lexMapping, boolean okapi) {

        for (int i = 0; i < intStore.length; i += 2) {
            MapMapping mapMapping = mapping.get(intStore[i]);
            //if (!docFreqSwitch) {
            if (!accumulators.containsKey(intStore[i])) {

                accumulators.put(intStore[i], new Accumulator(intStore[i],
                        (okapi) ? BM25.calculateSimilarity(mapping.size(), lexMapping.getNoDocuments(),
                        intStore[i + 1], mapMapping.getDocumentWeight(), averageDocLength) : 0));
            } else {
                accumulators.get(intStore[i]).setPartialSimilarityScore(BM25.calculateSimilarity(mapping.size(), lexMapping.getNoDocuments(),
                        intStore[i + 1], mapMapping.getDocumentWeight(), averageDocLength));
            }
        }
    }
}
