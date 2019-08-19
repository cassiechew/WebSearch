package indexing;

import util.Compressor;
import util.Document;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * Generates the inverted list from the provided documents.
 *
 */
public class InvIndexGenerator {


    /** Contains each unique term that occurs in the collection and a pointer to the inverted list for that term */
    private File lexiconFile;

    /** Contains the inverted list information, consisting only of numerical data */
    private File invlistsFile;

    /** The compressor that will be used to handle all compression/decompression of data */
    private Compressor compressor;

    /** Flag to determine whether compression will be used or not */
    private boolean compress;

    /** The internal representation of the lexicon-inverted list */
    private Map<String, Map<Integer, Integer>> lexiconInvlist;


    public InvIndexGenerator (String lexiconFilename, String invlistFilename, boolean compress,
                              String compressionStrategy) {

        lexiconFile = new File(lexiconFilename);
        invlistsFile = new File(invlistFilename);

        this.lexiconInvlist = new HashMap<>();

        this.compress = compress;

        compressor = (compress) ? new Compressor(compressionStrategy) : null;
        clearFiles();
    }


    /**
     * A quick method to remove remaining file data
     */
    private void clearFiles () {
        try (
                PrintWriter printWriter = new PrintWriter(invlistsFile);
                PrintWriter secondPrintWriter = new PrintWriter(lexiconFile)
        ) {
            printWriter.write("");
            secondPrintWriter.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Creates the full inverted list data
     * @param documentList The list of documents to process
     */
    public void createList (List<Document> documentList, boolean verbose) {

        for (Document d : documentList
             ) {

            mapLexiconData(d.getDocumentID(), d.getHeadline().split(" "), verbose);
            mapLexiconData(d.getDocumentID(), d.getTextData().split(" "), verbose);

        }

        lexiconInvlist.remove("");

    }


    /**
     * The mapping function for the lexicon data. It reads through the document headers and text content and maps the
     * words and frequencies of each word to the internal inverted list
     * @param documentID The ID of the current document being processed
     * @param textData The text data to process from the current document
     */
    private void mapLexiconData (int documentID, String[] textData, boolean verbose) {

        for (String s : textData) {
            if (!lexiconInvlist.containsKey(s)) {
                SortedMap<Integer, Integer> newSet = new TreeMap<>();
                newSet.put(documentID, 1);
                if (verbose) System.out.println(s);
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
        lexiconPairData.remove("");
        writeLexiconData(lexiconPairData);

        //System.out.println(varByteConversion(93823132));
    }





    /**
     * Writes the lexicon file, containing the current indexed words and the pointer location in the index file
     * @param lexiconPairData The map of the lexicon and pointer data
     */
    private void writeLexiconData (Map<String, Long> lexiconPairData) {

        try (
                FileWriter fileWriter = new FileWriter(lexiconFile);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)
        ){
            StringBuilder sb = new StringBuilder();

            for (String key : lexiconPairData.keySet()) {

                sb.append(key);
                sb.append(" ");
                sb.append(lexiconInvlist.get(key).size());
                sb.append(" ");
                sb.append(lexiconPairData.get(key));
                sb.append("\n");

                bufferedWriter.write(sb.toString());
                sb.setLength(0);
            }

            bufferedWriter.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * Writes the numerical-binary data for the inverted list
     * @return returns the mapping of the words to pointer locations in the binary file
     */
    private Map<String, Long> writeInvertedListData () {

        Map<String, Long> lexiconPairData = new HashMap<>();

        int prev;


        try (
                FileOutputStream fileOutputStream = new FileOutputStream(invlistsFile);//RandomAccessFile invlistRAFile = new RandomAccessFile(invlistsFile, "rw");
                FileChannel fileChannel = fileOutputStream.getChannel();
        ){


            for (String key: lexiconInvlist.keySet()
                 ) {
                Map<Integer, Integer> mappingData = lexiconInvlist.get(key);
                lexiconPairData.put(key, fileChannel.position());

                prev = 0;

                for (Integer documentID : mappingData.keySet()) {



                    byte[] write = ((this.compress) ? compressor.compress( documentID - prev) :
                            ByteBuffer.allocate(4).putInt(documentID-prev).array());
                    fileChannel.write(ByteBuffer.wrap(write));

                    write = ((this.compress) ? compressor.compress( mappingData.get(documentID)) :
                            ByteBuffer.allocate(4).putInt(mappingData.get(documentID)).array());
                    fileChannel.write(ByteBuffer.wrap(write));

                    prev = documentID;

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lexiconPairData;
    }

}
