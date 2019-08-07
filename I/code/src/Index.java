

import indexing.DocumentHandler;
import indexing.InvIndexGenerator;
import util.Document;
import util.DocumentFactory;

import java.util.HashMap;

import java.util.List;
import java.util.Map;

public class Index {

    private static final String LEXICONFILENAME = "lexicon";
    private static final String INVLISTFILENAME = "invlist";
    private static final String MAPFILENAME     = "map";


    private static boolean verbose      = false;
    private static boolean hasStoplist  = false;
    private static boolean timed        = false;

    private final static int FAILURE = 1;

    private static List<Document> parsedData;

    private static String currentFile  = null;
    private static String stopfile     = null;

    public static void main (String[] args) {

        DocumentHandler documentHandler = new DocumentHandler();
        DocumentFactory documentFactory;
        InvIndexGenerator invIndexGenerator;

        Map<Integer, Document> documentMap = new HashMap<>();
        documentFactory = new DocumentFactory(documentMap);

        opsHandler(args);


        final long start = (timed) ? System.currentTimeMillis() : 0;

        System.out.println("\033[H\033[2J");
        System.out.println("Initializing factories and files...");
        documentHandler.setDocumentFactory(documentFactory);
        documentHandler.setCurrentFile(currentFile);

        if (hasStoplist) {
            documentHandler.scanStopList(stopfile);
        }

        System.out.println("Parsing document data...");
        parsedData = documentHandler.readFile();

        System.out.println("Writing mapping data...");
        documentHandler.writeOutFile(MAPFILENAME);
        System.out.println("Parsing complete!");

        System.out.println("Initializing index generator...");
        invIndexGenerator = new InvIndexGenerator(LEXICONFILENAME, INVLISTFILENAME, false);

        System.out.println("Indexing data...");
        invIndexGenerator.createList(parsedData);
        System.out.println("Indexing complete!");

        System.out.println("Writing indexed data to file...");
        invIndexGenerator.writeOutfileData();
        System.out.println("Writing complete!");

        if (verbose) {
            for (Document d : parsedData
                 ) {
                d.printDoc();
            }
        }

        if (timed) {
            final long end = System.currentTimeMillis();
            System.out.println("Total excecution time: " + (end - start));
        }

        System.out.println("Inverted index completed!");

    }

    /**
     * Prints all document information
     */
    @Deprecated
    private static void printAllDocs () {
        for (Document d : parsedData
                ) {
            System.out.println(parsedData.get(parsedData.indexOf(d)).getDocumentID());
            System.out.println(parsedData.get(parsedData.indexOf(d)).getDocumentNo());
            System.out.println(parsedData.get(parsedData.indexOf(d)).getHeadline());
            System.out.println(parsedData.get(parsedData.indexOf(d)).getTextData());
            System.out.println();
        }
    }


    /**
     * A simple handler for CLI options management
     * @param args The cli options
     */
    private static void opsHandler (String[] args) {

        boolean[] opsArray  = new boolean[args.length];
        int opsCount        = 0;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-h") | args[i].equals("--help")) {
                usage();
                return;
            }
            if (args[i].equals("-p") | args[i].equals("--print")) {
                verbose = true;
                opsArray[i] = true;
                opsCount++;
                continue;
            }
            if (args[i].equals("-s") | args[i].equals("--stoplist")) {
                hasStoplist = true;
                opsArray[i] = true;
                if (args.length <= i+1 | args[i+1].startsWith("-")) {
                    System.err.println("Missing file for " + args[i]);
                }
                stopfile = args[i+1];
                opsArray[i+1] = true;

                opsCount = opsCount + 2;

                continue;
            }
            if (args[i].equals("-t") | args[i].equals("--time")) {
                timed = true;
                opsArray[i] = true;
                opsCount++;
            }
        }
        for (int i = 0; i < opsArray.length; i++) {
            if (!opsArray[i]) {
                currentFile = args[i];
                opsCount++;
            }
        }
        if (opsCount != opsArray.length) {
            System.exit(FAILURE);
        }
    }


    /**
     * Usage message for CLI ops
     */
    private static void usage() {
        System.out.println("Usage: Index [-p|--print] [-s|-stoplist <source file>] <source file>");
        System.out.println("Creates an inverted index of the supplied document");
        System.out.println("Options:");
        System.out.println("  -p, --print            Prints the cleaned text");
        System.out.println("  -s, --stoplist         Uses the supplied stoplist for processing");
        System.out.println("  -t, --time             Times the excecution time");
        System.out.println("  -h, --help             Prints this help message and exits");
    }

}
