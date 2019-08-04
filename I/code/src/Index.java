import indexing.DocumentHandler;
import indexing.InvIndexGenerator;
import util.Document;
import util.DocumentFactory;

import java.io.PrintStream;
import java.util.HashMap;

import java.util.List;
import java.util.Map;

public class Index {

    private static final String LEXICONFILENAME = "lexicon";
    private static final String INVLISTFILENAME = "invlist";
    public static final String MAPFILENAME = "map";


    private static final int FAILURE = 1;

    private static boolean verbose = false;
    private static boolean hasStoplist = false;


    private static List<Document> parsedData;
    private static Map<Integer, Document> documentMap;

    public static void main (String[] args) {


        DocumentHandler documentHandler = new DocumentHandler();
        DocumentFactory documentFactory;
        InvIndexGenerator invIndexGenerator;

        String currentFile = null;
        String stopfile = null;

        boolean[] opsArray = new boolean[args.length];

        documentMap = new HashMap<>();
        documentFactory = new DocumentFactory(documentMap);

        for (int i = 0; i < args.length; i++) {

            if (args[i].equals("-h") | args[i].equals("--help")) {
                usage(System.out);
                return;
            }

            if (args[i].equals("-p") | args[i].equals("--print")) {
                verbose = true;
                opsArray[i] = true;
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

                continue;
            }



        }

        for (int i = 0; i < opsArray.length; i++) {
            if (!opsArray[i]) {
                currentFile = args[i];
            }
        }

        documentHandler.setDocumentFactory(documentFactory);

        documentHandler.setCurrentFile(currentFile);
        if (hasStoplist) {
            documentHandler.scanStopList(stopfile);
        }
        parsedData = documentHandler.readFile();


        invIndexGenerator = new InvIndexGenerator(LEXICONFILENAME, INVLISTFILENAME, MAPFILENAME);

        invIndexGenerator.createList(parsedData);
        invIndexGenerator.writeOutfileData();

        if (verbose) {
            for (Document d : parsedData
                 ) {
                d.printDoc();
            }
        }



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
     * Usage message for CLI ops
     * @param ps The out stream to write to
     */
    private static void usage(PrintStream ps) {
        ps.println("Usage: Index [-p|--print] [-s|-stoplist <source file>] <source file>");
        ps.println("Creates an inverted index of the supplied document");
        ps.println("Options:");
        ps.println("  -p, --print            Prints the cleaned text");
        ps.println("  -s, --stoplist         Uses the supplied stoplist for processing");
        ps.println("  -h, --help             Prints this help message and exits");
    }

}
