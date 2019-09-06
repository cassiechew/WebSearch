package util;


import util.strategy.Standard;
import util.strategy.Strategy;
import util.strategy.VariableByte;

import java.util.Map;


/**
 * A context class to contain the compression strategies used in the program
 */
public class Compressor {


    /** The strategy to use for this compressor */
    private Strategy strategy;

    public Compressor (String strategy) {

        switch (strategy) {
            case "none":
                this.strategy = new Standard();
                break;
            case "varbyte":
                this.strategy = new VariableByte();
                break;
            default:
                System.out.println("You have not entered a valid strategy! -> " + strategy);
                System.exit(1);
        }
    }


    /**
     * Calls the compress method in the strategy
     * @param input The number to compress
     * @return The compressed binary string of the inputted number
     */
    public byte[] compress (int input) {
         return strategy.compress(input);
    }


    /**
     * Calls the decompress method in the strategy
     * @param invindexFileName The name of the inverted list file
     * @param lexicon The lexicon hashmap containing the documents, and offset numbers
     * @param mappingData The mapping data that pairs document IDs to raw document IDs in the collection
     * @param query The query terms to search.
     */
    public void decompress (String invindexFileName, Map<String, LexMapping> lexicon, Map<Integer, MapMapping> mappingData, String query) {
        strategy.decompress(invindexFileName, lexicon, mappingData, query);
    }
}
