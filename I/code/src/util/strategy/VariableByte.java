package util.strategy;


/**
 * Variable byte compression/decompression strategy for use
 */
public class VariableByte implements Strategy {


    /**
     * Variable byte compression algorithm implementation
     * @param input The number to compress
     * @return A string representation of the compressed binary data
     */
    public String compress(long input) {
        int convertedInt = (int) input;
        int numBytes = ((32 - Integer.numberOfLeadingZeros(convertedInt)) + 6) / 7;

        /* if the integer is 0, we still need 1 byte */
        numBytes = (numBytes > 0) ? numBytes : 1;
        StringBuilder sb = new StringBuilder();

        /* Process each byte in the number */
        for(int i = 0; i < numBytes; i++) {

            /* Take the least significant 7 bits of input and set the MSB to 1 */
            sb.append(Integer.toBinaryString((convertedInt & 0b1111111) | 0b10000000));

            /* Shift the input right by 7 places */
            convertedInt >>= 7;
        }

        /*reset the MSB on the last byte */
        sb.replace(0, 1, "0");

        return sb.toString();
    }



    public int decompress(String input) {
        return 0;
    }
}
