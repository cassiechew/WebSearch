package queryingModule;

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

    private HashMap<Integer, Accumulator> accumulators;

    private Vector<String> queries;

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

        while ((c < n) && (accumulatorMinHeap.size() > 0)) {
            System.out.println("afklhjkhlfdd");
            output.add(accumulatorMinHeap.poll());
            c++;
        }

        return output;
    }

    /**
     * Umbrella method for generating accumulators.
     **
     * @param queryTerms The terms for the query
     */
    public void accumulatorCycle(String[] queryTerms, Map<String, List<Document>> potentialQueries, double numberOfDocsInPool) {
        this.accumulators = new HashMap<>();
        try (
                RandomAccessFile invlist = new RandomAccessFile(new File(this.invlist), "r");
        ) {

            for (String query : queryTerms) {
                //System.out.println(query);
                generateAccumulators(query, invlist, potentialQueries, numberOfDocsInPool);
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
    private void generateAccumulators(
            String query, RandomAccessFile invlist, Map<String, List<Document>> potentialQueries,
            double numberOfDocsInPool) throws IOException {

        final int NOBYTES = 4;

        LexMapping lexMapping;

        int[] intStore;
        int noIntsToRead;

//        boolean docFreqSwitch = false;


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
        }
        else {
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



    public void setQueries(Vector<String> queries) {
        //            System.out.println("VVV   " + s);
        //this.queries.addAll(queries);
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
//            System.out.println(intStore[i]);
            //if (!docFreqSwitch) {
//            System.out.println("%%%  " + query);
            if (!accumulators.containsKey(intStore[i])) {

                accumulators.put(intStore[i], new Accumulator(intStore[i], (okapi) ?
                        BM25.calculateSimilarity(mapping.size(), lexMapping.getNoDocuments(), intStore[i + 1], mapMapping.getDocumentWeight(), averageDocLength)
                        :
                        TSV.calculateRJSSimilarity(lexMapping, mapping.size(), potentialQueries.get(query).size(), numberOfDocsInPool)));
//                if (intStore[i] == 7) System.out.println(query + " LOL " + accumulators.get(intStore[i]).getPartialSimilarityScore() + " " + okapi);

            } else {
                Accumulator accumulator = accumulators.get(intStore[i]);
//                System.out.println(accumulator.getPartialSimilarityScore());

//                if (intStore[i] == 7) System.out.println(query + " BEFORE " + accumulators.get(intStore[i]).getPartialSimilarityScore() + " " + okapi);


                double similarityScore = (okapi) ?
                        BM25.calculateSimilarity(mapping.size(), lexMapping.getNoDocuments(), intStore[i + 1], mapMapping.getDocumentWeight(), averageDocLength)
                        :
                        TSV.calculateRJSSimilarity(lexMapping, mapping.size(), potentialQueries.get(query).size(), numberOfDocsInPool);

                //if (!okapi) System.out.println(query + " " + similarityScore + " " + okapi + " " + lexMapping.getNoDocuments() + " " + mapping.size() + " " + potentialQueries.get(query).size() + " " + numberOfDocsInPool);

                accumulator.setPartialSimilarityScore(similarityScore);
//                if (intStore[i] == 7)  System.out.println(query + " AFTER  " + accumulators.get(intStore[i]).getPartialSimilarityScore() + " " + okapi);
            }
        }
    }
}
