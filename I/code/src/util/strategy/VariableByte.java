package util.strategy;


import util.LexMapping;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Variable byte compression/decompression strategy for use
 */
public class VariableByte implements Strategy {

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
        for(int i = 0; i < numBytes; i++) {
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
     * @param input The invlist string to decompress
     * @return The integer
     */
    public int decompress(byte[] input) {
        return ByteBuffer.wrap(input).getInt();
    }


    /**
     * Decompressing algorithm for a single digit
     * @param input The binary string representation of the compressed data
     * @return The decompressed integer
     */
    private int singleDecompress (String input) {
        int lengthOfString = input.length();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < (lengthOfString/VARBYTELENGTHFULL); i++) {
            sb.append(String.valueOf(input.toCharArray(), i * VARBYTELENGTHFULL, VARBYTELENGTHFULL));
        }
        return Integer.parseInt(sb.toString(),2);
    }
}
