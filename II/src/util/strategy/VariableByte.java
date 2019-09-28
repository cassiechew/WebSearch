package util.strategy;


import util.LexMapping;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Variable byte compression/decompression strategy for use
 */
public class VariableByte extends AStrategy implements Strategy {

    private static final int INTEGERBYTELENGTH  = 32;
    private static final int VARBYTELENGTH      = 7;
    private static final int VARBYTELENGTHFULL  = 8;

    /**
     * Variable byte compression algorithm implementation
     * @param input The number to compress
     * @return A string representation of the compressed binary data
     */
    @Deprecated
    public String _compress(long input) {
        int convertedInt = (int) input;
        int numBytes = ((INTEGERBYTELENGTH - Integer.numberOfLeadingZeros(convertedInt))
                + (VARBYTELENGTH - 1)) / VARBYTELENGTH;

        /* if the integer is 0, we still need 1 byte */
        numBytes = (numBytes > 0) ? numBytes : 1;
        StringBuilder sb = new StringBuilder();



        /* Process each byte in the number */
        for(int i = 0; i < numBytes; i++) {

            /* Take the least significant 7 bits of input and set the MSB to 1 */
            sb.insert(0, Integer.toBinaryString((convertedInt & 0b1111111) | 0b10000000));

            /* Shift the input right by 7 places */
            convertedInt >>= VARBYTELENGTH;
        }

        /*reset the MSB on the last byte */
        sb.replace(0, 1, "0");

        return sb.toString();
    }


    /**
     * Variable byte compression algorithm implementation
     * @param input The number to compress
     * @return A string representation of the compressed binary data
     */
    public byte[] compress(int input) {
        // first find out how many bytes we need to represent the integer
        int numBytes = ((INTEGERBYTELENGTH - Integer.numberOfLeadingZeros(input))
                + (VARBYTELENGTH - 1)) / VARBYTELENGTH;        // if the integer is 0, we still need 1 byte
        numBytes = numBytes > 0 ? numBytes : 1;
        byte[] output = new byte[numBytes];
        // for each byte of output ...
        for(int i = numBytes - 1; i >= 0; i--) {
            // ... take the least significant 7 bits of input and set the MSB to 1 ...
            output[i] = (byte) ((input & 0b1111111) | 0b10000000);
            // ... shift the input right by 7 places, discarding the 7 bits we just used
            input >>= VARBYTELENGTH;
        }
        // finally reset the MSB on the last byte
        output[numBytes-1] &= 0b01111111;


        return output;
    }

    /**
     * Variable byte decompression
     * @param invindexFileName The name of the inverted list file
     * @param lexicon The lexicon hashmap containing the documents, and offset numbers
     * @param mappingData The mapping data that pairs document IDs to raw document IDs in the collection
     * @param queries The query terms to search.
     */
    public void decompress(String invindexFileName, Map<String, LexMapping> lexicon, Map<Integer, String> mappingData, String[] queries) {

        File invListFile = new File(invindexFileName);


        try (
                RandomAccessFile invIndexRAF = new RandomAccessFile(invListFile, "r")
                ) {

            for (String query : queries) {

                LexMapping lexMapping = lexicon.get(query);
                int noIntsToRead = 2 * lexMapping.getNoDocuments();
                int[] intStore = new int[noIntsToRead];

                byte[] tempByteStore;
                short counter = 3;

                invIndexRAF.seek(lexMapping.getOffset());

                for (int i = 0; i < noIntsToRead; i++) {
                    boolean lastByteFound = false;
                    tempByteStore = new byte[4];

                    do {

                        byte[] singleByte = new byte[1];
                        invIndexRAF.read(singleByte);
                        if(checkByte(singleByte)) {
                            lastByteFound = true;
                            tempByteStore[counter] = (byte) (singleByte[0] & 0b1111111);
                            intStore[i] = ByteBuffer.wrap(tempByteStore).getInt();
                            counter = 3;
                        }
                        else {
                            tempByteStore[counter] = (byte) (singleByte[0] & 0b1111111);
                            counter--;
                        }

                    } while (!lastByteFound);

                }
                outputData(query, lexMapping, mappingData, intStore);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean checkByte (byte[] input) {
        return ((input[input.length-1] >> VARBYTELENGTH) & 1) == 0;
    }


}
