package util.strategy;

import util.LexMapping;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

public class Standard extends AStrategy implements Strategy{



    public byte[] compress(int input) {
        return ByteBuffer.allocate(4).putInt(input).array();
    }


    public void decompress(String invindexFileName, Map<String, LexMapping> lexicon, Map<Integer, String> mappingData, String[] queries) {


        File file2 = new File(invindexFileName); //open inverted list file
        final int NOBYTES = 4;

        for (String query : queries) {

            //looking for query term inside map
            if (!lexicon.containsKey(query)) {
                System.out.println("This term does not exist in our documents!");
                System.exit(1);
            }
            LexMapping lexMapping = lexicon.get(query); //stores Lexmapping type variable called lexMapping
            int noIntsToRead = 2 * lexMapping.getNoDocuments();
            int[] intStore = new int[noIntsToRead];


            try (
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file2, "r")
            ) {
                randomAccessFile.seek(lexMapping.getOffset());  //getting byteoffset from map
                for (int i = 0; i < noIntsToRead; i++) { //no. of itrations for int

                    //get docID and frequency
                    byte[] docIDFrequency = new byte[NOBYTES];
                    //reading bytes and stored in byte []
                    randomAccessFile.read(docIDFrequency);
                    //convert byte array to bytebuffer
                    ByteBuffer wrapped = ByteBuffer.wrap(docIDFrequency);
                    int output = wrapped.getInt();
                    intStore[i] = output;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            outputData(query, lexMapping, mappingData, intStore);
        }

    }
}
