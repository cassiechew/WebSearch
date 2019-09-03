import quering.QueryDocumentHandler;

import util.Compressor;

import java.util.Arrays;


/**
 * A module to search through the inverted list for documents that contain query words
 */
public class Search {


    private static String compressionStrategy = "none";
    private static Compressor compressor;
    private static QueryDocumentHandler queryDocumentHandler;

    public static void main(String[] args) {

        if (args.length < 4) {
            System.out.println("invalid arguments");
            System.exit(1);
        }

        init(args[1]);

        queryDocumentHandler.generateIndexDataFromFiles(args[0], QueryDocumentHandler.fileType.LEXICON);
        queryDocumentHandler.generateIndexDataFromFiles(args[2], QueryDocumentHandler.fileType.MAP);

        compressor.decompress(args[1], queryDocumentHandler.getLexicon(), queryDocumentHandler.getMapping(), Arrays.copyOfRange(args, 3, args.length));

    }


    /**
     * Sets the compression strategy to unpack based on filename
     * @param invlistFileName The name of the inverted list binary data file
     */
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

    /**
     * Initializes the search module
     * @param invlistFileName The name of the inverted list binary data file
     */
    private static void init(String invlistFileName) {

        setCompressionStrategy(invlistFileName);
        compressor = new Compressor(compressionStrategy);
        queryDocumentHandler = new QueryDocumentHandler();
    }

}




