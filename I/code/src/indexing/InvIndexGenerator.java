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

                    stringBuilder.append(Integer.toBinaryString(documentID));
                    stringBuilder.append(" ");
                    stringBuilder.append(Integer.toBinaryString(mappingData.get(documentID)));
                    stringBuilder.append(" ");

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
