package util.strategy;


import util.LexMapping;

import java.lang.reflect.Field;
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
    public String compress(long input) {
        int convertedInt = (int) input;
        int numBytes = ((INTEGERBYTELENGTH - Integer.numberOfLeadingZeros(convertedInt))
                + (VARBYTELENGTH - 1)) / VARBYTELENGTH;

        /* if the integer is 0, we still need 1 byte */
        numBytes = (numBytes > 0) ? numBytes : 1;
        StringBuilder sb = new StringBuilder();

        /* Process each byte in the number */
        for(int i = 0; i < numBytes; i++) {

            /* Take the least significant 7 bits of input and set the MSB to 1 */
            sb.append(Integer.toBinaryString((convertedInt & 0b1111111) | 0b10000000));

            /* Shift the input right by 7 places */
            convertedInt >>= VARBYTELENGTH;
        }

        /*reset the MSB on the last byte */
        sb.replace(0, 1, "0");

        return sb.toString();
    }


    /**
     * Variable byte decompression
     * @param input The invlist string to decompress
     * @return
     */
    public Map<String, Map<Integer, Integer>> decompress(String input, List<LexMapping> words) {

        Map<String, Map<Integer, Integer>> output = new HashMap<>();

        try {
            final Field field = String.class.getDeclaredField("value");
            field.setAccessible(true);

            final char[] data = (char[]) field.get(input); //input.toCharArray();

            final int byteLength = 8;

            boolean checkCurrentInt;
            boolean isDocument = true;

            int counter = byteLength;
            int currentOffset = 0;

            StringBuilder sb = new StringBuilder();

            // Assume string is 011001001001011010010101
            //                  I       I       I

            /*
                1. While reading invlist file, decompress, using stored file pointers from lexicon.
                2. When file pointer is reached, call this function and send in word and integer string
             */

            for (LexMapping l : words) {
                Map<Integer, Integer> documentMapping = new HashMap<>();

                int documentID = -1;
                int frequency = -1;

                for (int i = currentOffset; i < l.getOffset(); i++) {

                    checkCurrentInt = currentOffset % byteLength == 0;

                    if (checkCurrentInt && (i != 0)) {
                        if (isDocument) {
                            documentID = singleDecompress(sb.toString());
                            sb.setLength(0);
                        }
                        else {
                            frequency = singleDecompress(sb.toString());
                            sb.setLength(0);
                        }
                        isDocument = !isDocument;

                    }

                    if (documentID >=0 && frequency >= 0) {
                        documentMapping.put(documentID, frequency);
                        documentID = -1;
                        frequency = -1;
                    }
                    sb.append(data[i]);
                    currentOffset++;
                }
                output.put(l.getWord(), documentMapping);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return output;
    }


    private int singleDecompress (String input) {
        int lengthOfString = input.length();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < (lengthOfString/VARBYTELENGTHFULL); i++) {

            sb.append(String.valueOf(input.toCharArray(), i * VARBYTELENGTHFULL, VARBYTELENGTHFULL));

        }

        return Integer.parseInt(sb.toString(),2);
    }
}
