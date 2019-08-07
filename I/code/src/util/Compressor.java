package util;


import util.strategy.Strategy;
import util.strategy.VariableByte;


/**
 * A context class to contain the compression strategies used in the program
 */
public class Compressor {


    /** The strategy to use for this compressor */
    private Strategy strategy;

    public Compressor (String strategy) {

        switch (strategy) {
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
    public String compress (long input) {

         return strategy.compress(input);

    }


    /**
     * Calls the decompress method in the strategy
     * @param input The binary string to decompress
     * @return The number gained from the decompression
     */
    public int decompress (String input) {
        return strategy.decompress(input);
    }
}
