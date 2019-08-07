package util.strategy;


/**
 * An interface to contain the methods needed to compress/decompress files to allow for different strategies of
 * compression/decompression
 */
public interface Strategy {

    /**
     * The compression method used by the implementing strategy to be called from the compressor context
     * @param input The number to be compressed into a binary string
     * @return The binary string to return
     */
    String compress(long input);


    /**
     * The compression method used by the implementing strategy to be called from the compressor context
     * @param input The string to decompress
     * @return The number gained from the decompression
     */
    int decompress(String input);
}
