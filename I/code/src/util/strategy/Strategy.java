package util.strategy;


/**
 * An interface to contain the methods needed to compress/decompress files to allow for different strategies of
 * compression/decompression
 */
public interface Strategy {
    String compress(long input);
    int decompress(String input);
}
