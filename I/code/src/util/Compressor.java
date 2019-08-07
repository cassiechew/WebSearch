package util;


import util.strategy.Strategy;
import util.strategy.VariableByte;

/**
 * A context class to contain the compression strategies used in the program
 */
public class Compressor {

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

    public String compress (long input) {

         return strategy.compress(input);

    }


    public int decompress (String input) {
        return strategy.decompress(input);
    }



}
