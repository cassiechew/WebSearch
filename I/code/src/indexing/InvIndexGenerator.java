package indexing;

import util.Document;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Generates the inverted list from the provided documents.
 *
 */
public class InvIndexGenerator {


    /** Contains each unique term that occurs in the collection and a pointer to the inverted list for that term */
    private File lexiconFile;

    /** Contains the inverted list information, consisting only of numerical data */
    private File invlistsFile;


    private Map<String, Map<Integer, Integer>> lexiconInvlist;


    public InvIndexGenerator (String lexiconFilename, String invlistFilename) {

        lexiconFile = new File(lexiconFilename);
        invlistsFile = new File(invlistFilename);

        this.lexiconInvlist = new HashMap<>();

    }


    /**
     * Creates the full inverted list data
     * @param documentList The list of documents to process
     */
    public void createList (List<Document> documentList) {

        for (Document d : documentList
             ) {

            mapLexiconData(d.getDocumentID(), d.getHeadline().split(" "));
            mapLexiconData(d.getDocumentID(), d.getTextData().split(" "));

        }

        lexiconInvlist.remove("");

            //store document number, frequency it appears here

        //System.out.println(lexiconInvlist.toString());
    }


    /**
     * The mapping function for the lexicon data. It reads through the document headers and text content and maps the
     * words and frequencies of each word to the internal inverted list
     * @param documentID The ID of the current document being processed
     * @param textData The text data to process from the current document
     */
    private void mapLexiconData (int documentID, String[] textData) {

        for (String s : textData) {
            if (!lexiconInvlist.containsKey(s)) {
                HashMap<Integer, Integer> newSet = new HashMap<>();
                newSet.put(documentID, 1);
                lexiconInvlist.put(s, newSet);
            }
            else {
                if (lexiconInvlist.get(s).containsKey(documentID)) {
                    lexiconInvlist.get(s).replace(documentID, lexiconInvlist.get(s).get(documentID) + 1);
                }
                else {
                    lexiconInvlist.get(s).put(documentID, 1);
                }
            }
        }
    }


    /**
     * The branching function to start the writing to the outfiles
     */
    public void writeOutfileData () {

        Map<String, Long> lexiconPairData;

        lexiconPairData = writeInvertedListData();
        writeLexiconData(lexiconPairData);
    }


    private byte[] varByteConversion (long input) {
        int convertedInt = (int) input;
        int numBytes = ((32 - Integer.numberOfLeadingZeros(convertedInt)) + 6) / 7;
        // if the integer is 0, we still need 1 byte
        numBytes = numBytes > 0 ? numBytes : 1;
        byte[] output = new byte[numBytes];
        System.out.println(Integer.toBinaryString((convertedInt & 0b1111111) | 0b10000000));
        // for each byte of output ...
        for(int i = 0; i < numBytes; i++) {
            // ... take the least significant 7 bits of input and set the MSB to 1 ...
            output[i] = (byte) ((convertedInt & 0b1111111) | 0b10000000);
            // ... shift the input right by 7 places, discarding the 7 bits we just used
            //System.out.print(Integer.toBinaryString(output[i]) + " ");
            convertedInt >>= 7;
        }
        // finally reset the MSB on the last byte
        output[0] &= 0b01111111;


        /*String binaryRepresentation = Long.toBinaryString(input| 0x100000000L ).substring(1);
        //System.out.println(binaryRepresentation);

        System.out.println(0b110100101010 + " " + (0b110100101010 >> 7));


        int numBytes = ((int) Math.ceil(((double)binaryRepresentation.length()) / 7.0));

        byte[] bytes = new byte[numBytes];
        String[] data = new String[numBytes];


        for (int i = 0; i < numBytes; i++) {

            System.out.print(Integer.toBinaryString(output[i]) + " ");

        }*/
        System.out.println();
        return null;

    }


    /**
     * Writes the lexicon file, containing the current indexed words and the pointer location in the index file
     * @param lexiconPairData The map of the lexicon and pointer data
     */
    private void writeLexiconData (Map<String, Long> lexiconPairData) {

        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;

        try {

            fileWriter = new FileWriter(lexiconFile);
            bufferedWriter = new BufferedWriter(fileWriter);

            for (String key : lexiconPairData.keySet()) {
                bufferedWriter.write(key + " " + lexiconPairData.get(key) + "\n");

                varByteConversion(lexiconPairData.get(key));
            }

            bufferedWriter.flush();
            bufferedWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert bufferedWriter != null;
                bufferedWriter.close();
                fileWriter.close();
            } catch (IOException e ) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Writes the numerical-binary data for the inverted list
     * @return returns the mapping of the words to pointer locations in the binary file
     */
    private Map<String, Long> writeInvertedListData () {

        Map<String, Long> lexiconPairData = new HashMap<>();
        RandomAccessFile invlistRAFile;
        FileChannel fileChannel = null;
        StringBuilder stringBuilder;
        ByteBuffer byteBuffer;


        try {

            invlistRAFile = new RandomAccessFile(invlistsFile, "rw");
            fileChannel = invlistRAFile.getChannel();
            stringBuilder = new StringBuilder();

            for (String key: lexiconInvlist.keySet()
                 ) {
                Map<Integer, Integer> mappingData = lexiconInvlist.get(key);
                lexiconPairData.put(key, fileChannel.position());


                for (Integer documentID : mappingData.keySet()) {

                    //stringBuilder.append(Integer.toBinaryString(documentID));
                    stringBuilder.append(Long.toBinaryString( Integer.toUnsignedLong(documentID) | 0x100000000L ).substring(1));

                    stringBuilder.append(" ");
                    //stringBuilder.append(Integer.toBinaryString(mappingData.get(documentID)));
                    stringBuilder.append(Long.toBinaryString( Integer.toUnsignedLong(mappingData.get(documentID)) | 0x100000000L ).substring(1));

                    stringBuilder.append(" ");

                    if (documentID >= 256 | mappingData.get(documentID) >= 256) {
                        System.out.println(documentID + " " + mappingData.get(documentID));
                    }

                }

                stringBuilder.append("\n");
                byteBuffer = ByteBuffer.wrap(stringBuilder.toString().getBytes(StandardCharsets.UTF_8));
                fileChannel.write(byteBuffer);
                stringBuilder.setLength(0);

            }



        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert fileChannel != null;
                fileChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return lexiconPairData;
    }



}
