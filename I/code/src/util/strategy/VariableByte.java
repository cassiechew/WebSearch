package util.strategy;


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



    public int decompress(String input) {

        // Assume string is 011001001001011010010101
        //                  I       I       I

        /*
            1. While reading invlist file, decompress, using stored file pointers from lexicon.
            2. When file pointer is reached, call this function and send in word and integer string
         */


        return 0;

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
