package util.strategy;


import util.LexMapping;
import util.MapMapping;

import java.util.List;
import java.util.Map;
import java.util.Vector;

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
    byte[] compress(int input);


    /**
     * The compression method used by the implementing strategy to be called from the compressor context
     */
     Vector<Integer> decompress(String invindexFileName, Map<String, LexMapping> lexicon, Map<Integer, MapMapping> mappingData, String query);
}
