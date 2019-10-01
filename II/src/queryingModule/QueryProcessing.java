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

    /** The internal lexicon file */
    private HashMap<String, LexMapping> lexicon;

    /** The internal mapping file */
    private HashMap<Integer, MapMapping> mapping;

    private String invlist;

    private Compressor compressor;

    public QueryProcessing (String invlist, HashMap<String, LexMapping> lexicon, HashMap<Integer, MapMapping> mapping, double averageDocLength) {
        this.accumulatorMinHeap = new PriorityQueue<>();
        this.accumulators = new HashMap<>();
        this.compressor = new Compressor("none");
        this.invlist = invlist;
        this.lexicon = lexicon;
        this.mapping = mapping;
        this.averageDocLength = averageDocLength;
    }

    public ArrayList<Accumulator> getTopNAccumulators(int n) {
        ArrayList<Accumulator> output = new ArrayList<>();
        accumulatorMinHeap.addAll(accumulators.values());

        int c = 0;
        while(c < n) {
            output.add(accumulatorMinHeap.poll());
            c++;
        }
        return output;
        //System.out.print(accumulatorMinHeap.toString());
    }

    /**
     * Umbrella method for generating accumulators.
     * @param queryTerms The terms for the query
     */
    public void accumulatorCycle (String[] queryTerms) {

        try (
                RandomAccessFile invlist = new RandomAccessFile(new File(this.invlist), "r");
                ) {

            for (String query : queryTerms) {
                generateAccumulators(query, invlist);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Generates accumulators for a single query term
     * @param query
     */
    private void generateAccumulators (String query, RandomAccessFile invlist) throws IOException {

        final int NOBYTES = 4;

        LexMapping lexMapping;

        int[] intStore;
        int noIntsToRead;

        boolean docFreqSwitch = false;


        if (!lexicon.containsKey(query)) {
            System.out.println("This term does not exist in our documents!");
            System.exit(1);
        }

        lexMapping = lexicon.get(query);
        noIntsToRead = 2 * lexMapping.getNoDocuments();
        intStore = new int[noIntsToRead];

        invlist.seek(lexMapping.getOffset());

        /**
         * Parses the query to the inverted index
         */
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

        /**
         * Calculates the accumulators
         */
        for (int i = 0; i < intStore.length; i += 2) {
            MapMapping mapMapping = mapping.get(intStore[i]);
            //if (!docFreqSwitch) {
                if (!accumulators.containsKey(intStore[i])) {

                    accumulators.put(intStore[i], new Accumulator(intStore[i], BM25.calculateSimilarity(mapping.size(), lexMapping.getNoDocuments(),
                            mapMapping.getDocumentWeight(), intStore[i+1], mapMapping.getDocumentWeight(), averageDocLength)));
                }
                else {
                    accumulators.get(intStore[i]).setPartialSimilarityScore(BM25.calculateSimilarity(mapping.size(), lexMapping.getNoDocuments(),
                            mapMapping.getDocumentWeight(), intStore[i+1], mapMapping.getDocumentWeight(), averageDocLength));
                }
        }
    }



    public void calculateDocumentWeights () {
        for (Integer i : mapping.keySet()) {
            //mapping.get(i).addTermWeight(BM25.calculateWeight(documentCounter, ));
        }
    }

}
