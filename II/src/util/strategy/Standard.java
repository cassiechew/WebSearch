package util.strategy;

import queryingModule.BM25;
import util.LexMapping;
import util.MapMapping;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;


/**
 * Non-compressed encoding strategy
 */
public class Standard extends AStrategy implements Strategy {


    /**
     * Just allocates a bytebuffer size of 32 to store the 32-bit integer
     * @param input The number to be compressed into a binary string
     * @return The byte array that contains the integer
     */
    public byte[] compress(int input) {
        return ByteBuffer.allocate(4).putInt(input).array();
    }

//TODO see if i can merge the mapmapping processing into the decompress thing
    /**
     * Basic reading from the file. 4 bytes at a time.
     * @param invindexFileName The name of the inverted list file
     * @param lexicon The lexicon hashmap containing the documents, and offset numbers
     * @param mappingData The mapping data that pairs document IDs to raw document IDs in the collection
     * @param query The query terms to search.
     */
    public Vector<Integer> decompress(String invindexFileName, Map<String, LexMapping> lexicon, Map<Integer, MapMapping> mappingData, String query) {


        File file2 = new File(invindexFileName); //open inverted list file
        final int NOBYTES = 4;
        Vector documentWeightForQuery;
        List tempList;

        try (
                RandomAccessFile randomAccessFile = new RandomAccessFile(file2, "r")
        ) {

            //for (String query : queries) {

                //looking for query term inside map
                if (!lexicon.containsKey(query)) {
                    System.out.println("This term does not exist in our documents!");
                    System.exit(1);
                }
                LexMapping lexMapping = lexicon.get(query); //stores Lexmapping type variable called lexMapping

                int noIntsToRead = 2 * lexMapping.getNoDocuments();
                int[] intStore = new int[noIntsToRead];
                boolean isFrequency = false;
                int tempDocID = -1;


                randomAccessFile.seek(lexMapping.getOffset());  //getting byteoffset from map
                for (int i = 0; i < noIntsToRead; i++) { //no. of itrations for int

                    //get docID and frequency
                    byte[] docIDFrequency = new byte[NOBYTES];
                    //reading bytes and stored in byte []
                    randomAccessFile.read(docIDFrequency);
                    //convert byte array to bytebuffer
                    ByteBuffer wrapped = ByteBuffer.wrap(docIDFrequency);
                    double output = wrapped.getDouble();

                    if(!isFrequency) {
                        tempDocID = (int)output;
                        isFrequency = !isFrequency;
                    }
                    else {
                        MapMapping mapMapping = mappingData.get(tempDocID);
                        mapMapping.addTermWeight(BM25.calculateWeight(mappingData.size(), output));
                        isFrequency = !isFrequency;

                    }

                    //intStore[i] = output;
                }

                //tempList = Arrays.asList(intStore);
                //documentWeightForQuery = new Vector<>(tempList);
                //return (Vector<Integer>)documentWeightForQuery;

                //outputData(query, lexMapping, mappingData, intStore);
            //}
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
