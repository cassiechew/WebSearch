import util.Compressor;
import util.LexMapping;
import java.io.*;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.nio.ByteBuffer;

public class Search {


    private static String compressionStrategy = "none";
    private static Compressor compressor;

    public static void main(String[] args) {

        if (args.length < 4) {
            System.out.println("invalid arguments");
            System.exit(1);
        }

        setCompressionStrategy(args[1]);
        init();


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


        compressor.decompress(args[1], map, map1, Arrays.copyOfRange(args, 3, args.length));
        
    }

    private static void setCompressionStrategy (String invlistFileName) {

        switch (invlistFileName) {
            case "invlist":
                compressionStrategy = "none";
                break;
            case "invlistvb":
                compressionStrategy = "varbyte";
                break;
        }

    }

    private static void init() {
        compressor = new Compressor(compressionStrategy);
    }

}




