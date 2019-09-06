package queryingModule;

import util.Compressor;
import util.LexMapping;
import util.MapMapping;

import java.util.HashMap;
import java.util.PriorityQueue;

public class QueryProcessing {

    private PriorityQueue<Integer> minHeap;

    /** The internal lexicon file */
    private HashMap<String, LexMapping> lexicon;

    /** The internal mapping file */
    private HashMap<Integer, MapMapping> mapping;

    private String invlist;

    private Compressor compressor;

    public QueryProcessing (String invlist, HashMap<String, LexMapping> lexicon, HashMap<Integer, MapMapping> mapping) {
        this.minHeap = new PriorityQueue<>();
        this.compressor = new Compressor("none");
        this.invlist = invlist;
        this.lexicon = lexicon;
        this.mapping = mapping;
    }

    private void parseDecompressResult (String query) {
        compressor.decompress(invlist, lexicon, mapping, query);
    }

    public void calculateDocumentWeights () {
        for (Integer i : mapping.keySet()) {
            //mapping.get(i).addTermWeight(BM25.calculateWeight(documentCounter, ));
        }
    }

}
