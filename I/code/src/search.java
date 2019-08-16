import util.LexMapping;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.nio.ByteBuffer;

public class search {


    public static void main(String[] args) {

        if (args.length < 4) {
            System.out.println("invalid arguments");
            System.exit(1);
        }



        HashMap<String, LexMapping> map = new HashMap<>();
        try {
            File file = new File(args[0]);
            BufferedReader lexicon = new BufferedReader(new FileReader(new File(args[0])));

            int line = 0;
            String buffer;
            //put lexicon into memory

            while ((buffer = lexicon.readLine()) != null) {
                String[] lex = buffer.split(" ");
                //convert 2nd/3rd to Int and Int
                int lex1 = Integer.parseInt(lex[1]);
                int lex2 = Integer.parseInt(lex[2]);
                map.put(lex[0], new LexMapping(lex1, lex2));
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<Integer, String> map1 = new HashMap<>();
        try {
            File file1 = new File(args[2]);
            BufferedReader latimes100 = new BufferedReader(new FileReader(new File(args[2])));

            int line1 = 0;
            String buffer1;
            //put mapping table into memory, map DOCID to raw DOCID

            while ((buffer1 = latimes100.readLine()) != null) {
                String[] mappingtable = buffer1.split(" ");
                //convert 1st to Int
                int mappingtable1 = Integer.parseInt((mappingtable[0]));
                map1.put(mappingtable1, mappingtable[1]);
            }


        } catch (IOException e) {
            System.out.println("File I/O error!");
        }




        File file2 = new File(args[1]); //open inverted list file

        for (int j = 3; j < args.length; j++) {

        //looking for query term inside map
            LexMapping lexMapping = map.get(args[j]); //stores Lexmapping type variable called lexMapping
            int[] output1 = new int[2 * lexMapping.getNoDocuments()];


            try (
                    RandomAccessFile randomAccessFile = new RandomAccessFile(file2, "r")
            ) {
                randomAccessFile.seek(lexMapping.getOffset());  //getting byteoffset from map
                for (int i = 0; i < 2 * lexMapping.getNoDocuments(); i++) { //no. of itrations for int

                    //get docID and frequency
                    byte[] docIDFrequency = new byte[4];
                    //reading bytes and stored in byte []
                    randomAccessFile.read(docIDFrequency);
                    //convert byte array to bytebuffer
                    ByteBuffer wrapped = ByteBuffer.wrap(docIDFrequency);
                    int output = wrapped.getInt();
                    output1[i] = i;
                }
            } catch(IOException e)
            {
                e.printStackTrace();
            }

                System.out.println(args[j]);
                System.out.println(lexMapping.getNoDocuments());
            for (int i = 0; i < output1.length; i++){
                if (i % 2 == 0){
                    String rawDocName = map1.get(output1[i]);
                    System.out.print(rawDocName + " ");
                }
                else {
                    System.out.println(output1[i]);
                }

            }

        }

    }




    }




