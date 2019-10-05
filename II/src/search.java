import indexingModule.DocumentHandler;
import queryingModule.QueryDocumentHandler;
import queryingModule.QueryProcessing;
import util.Accumulator;
import util.Document;
import util.DocumentFactory;
import util.MapMapping;

import java.util.*;
import java.text.DecimalFormat;

public class search {


    private static boolean BM25         = false;
    private static boolean hasStoplist  = false;

    private final static int FAILURE = 1;

    private static String stopfile              = null;
    private static String queryLabel            = null;
    private static String lexicon               = null;
    private static String map                   = null;
    private static String invlists              = null;

    private static Vector<String> queryTerms    = null;

    private static int numResults               = 0;

    private static boolean advanced             = false;
    private static int noRelevantDocs           = 1;
    private static int noTermsToAdd             = 0;
    private static String sourcefile;

    private static DecimalFormat df = new DecimalFormat("#.###");


    public static void main(String[] args) {
        opsHandler(args);

        final long starttime = System.currentTimeMillis();

        QueryDocumentHandler queryDocumentHandler = new QueryDocumentHandler();
        PriorityQueue<Accumulator> accumulators = new PriorityQueue<>();
        Vector<String> termsToPrint = new Vector<>(queryTerms);

        processQueriesToAccumulators(queryDocumentHandler, accumulators);

        if (advanced) {
            advancedProcessing(queryDocumentHandler, accumulators);

            // To reset the min heap
            accumulators = new PriorityQueue<>();

            processQueriesToAccumulators(queryDocumentHandler, accumulators);

        }

        printResults(starttime, termsToPrint, accumulators, queryDocumentHandler);

    }


    /**
     * The process for advanced ranked retrieval
     * @param queryDocumentHandler The query document handler
     * @param accumulators The accumulators
     */
    private static void advancedProcessing (
            QueryDocumentHandler queryDocumentHandler, PriorityQueue<Accumulator> accumulators
    ) {

        Map<Integer, Document> documentMap = new HashMap<>();
        Map<Integer, Integer> offsets = new HashMap<>();
        Map<Integer, MapMapping> documentMappings = queryDocumentHandler.getMapping();
        DocumentFactory documentFactory = new DocumentFactory(documentMap);

        for (int i = 0; i < noRelevantDocs && i < accumulators.size(); i++) {
            int start = documentMappings.get(Objects.requireNonNull(accumulators.poll()).getDocumentID()).getDocumentLocationPointer();
            int end = documentMappings.get(Objects.requireNonNull(accumulators.poll()).getDocumentID() + 1).getDocumentLocationPointer();

            offsets.put(start, end);
        }

        DocumentHandler documentHandler = new DocumentHandler();
        documentHandler.setCurrentFile(sourcefile);
        documentHandler.setDocumentFactory(documentFactory);

        int original = noTermsToAdd;

        for (int l : offsets.keySet()) {
//            if (noTermsToAdd <= 0) { break; }
            for (Document d : documentHandler.readFile(l, offsets.get(l))) {
//                if (noTermsToAdd <= 0) { break; }
                for (String s : d.getAllText().split(" ")) {
                    if (noTermsToAdd <= 0) {
                        noTermsToAdd = original;
                        break;
                    }
                    if (!queryTerms.contains(s)) {
                        queryTerms.add(s);
                        noTermsToAdd--;
                    }
                }
            }
        }
    }


    /**
     * Function to print the results
     *
     * @param starttime The time when the program started
     * @param termsToPrint The initial terms in the query
     * @param accumulators The accumulator scores
     * @param queryDocumentHandler The query document handler
     */
    private static void printResults (
            final long starttime, Vector<String> termsToPrint,
            PriorityQueue<Accumulator> accumulators, QueryDocumentHandler queryDocumentHandler
    ) {

        StringBuilder sb = new StringBuilder();
        int c = 1;

        System.out.println("Your search Query");
        for (String s : termsToPrint) { System.out.print(s + " "); }
        System.out.println();
        for (Accumulator a : accumulators) {
            sb.append(((queryLabel != null) ? queryLabel + " " : ""));
            sb.append(queryDocumentHandler.getMapping().get(a.getDocumentID()).getDocumentNameID());
            sb.append(" ");
            sb.append(c);
            sb.append(" ");
            sb.append(df.format(a.getPartialSimilarityScore()));
            System.out.println(sb.toString());
            sb.setLength(0);
            c++;
        }

        final long endtime = System.currentTimeMillis();

        System.out.println((endtime - starttime) + " ms");

    }


    /**
     * A function for stopping the queries.
     * @param queries
     * @return
     */
    private static Vector<String> stop(Vector<String> queries) {
        String[] queryArray = new String[queryTerms.size()];

        DocumentHandler documentHandler = new DocumentHandler();
        documentHandler.scanStopList(stopfile);
        queryTerms.toArray(queryArray);
        queryArray = documentHandler.stoppingFunction(new StringBuilder().append(String.join(" ", queryArray))).split(" ");
        return new Vector<>(Arrays.asList(queryArray));
    }


    /**
     * The function that runs the ranking of documents
     * @param queryDocumentHandler The handler to process documents related to the search
     * @param accumulators The min heap of the accumulators
     */
    private static void processQueriesToAccumulators (
            QueryDocumentHandler queryDocumentHandler, PriorityQueue<Accumulator> accumulators) {

        if (hasStoplist) queryTerms = stop(queryTerms);

        queryDocumentHandler.generateIndexDataFromFiles(lexicon, QueryDocumentHandler.fileType.LEXICON, queryTerms);

        queryDocumentHandler.generateIndexDataFromFiles(map, QueryDocumentHandler.fileType.MAP, queryTerms);

        QueryProcessing queryProcessing = new QueryProcessing(invlists, queryDocumentHandler.getLexicon(),
                queryDocumentHandler.getMapping(), queryDocumentHandler.getAverageDocumentLength());

        queryProcessing.accumulatorCycle(Arrays.copyOf(queryTerms.toArray(), queryTerms.size(), String[].class));

        accumulators.addAll(queryProcessing.getTopNAccumulators(numResults));

    }

    /**
     * Exits the program after a failure
     * @param arg The argument to reference for failure
     */
    private static void exit(String arg) {
        System.err.println("Missing file for " + arg);
        usage();
        System.exit(FAILURE);
    }


    /**
     * A simple handler for CLI options management
     * @param args The cli options
     */
    private static void opsHandler (String[] args) {

        boolean[] opsArray  = new boolean[args.length]; //dynamic placement of args
        int opsCount        = 0;

        queryTerms = new Vector<>();

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-h") | args[i].equals("--help")) {
                usage();
                System.exit(FAILURE);
            }
            if (args[i].equals("-BM25")) {
                opsArray[i] = true;
                opsCount++;
                continue;
            }
            if (args[i].equals("-q") | args[i].equals("--query-label")) {

                opsArray[i] = true;
                extOpsChecker(args, i);

                queryLabel = args[i + 1];

                opsArray[i + 1] = true;
                opsCount = opsCount + 2;
                continue;
            }
            if (args[i].equals("-n") | args[i].equals("--num-results")) {

                opsArray[i] = true;
                extOpsChecker(args, i);

                numResults = Integer.parseInt(args[i + 1]);

                opsArray[i + 1] = true;
                opsCount = opsCount + 2;
                continue;
            }
            if (args[i].equals("-l") | args[i].equals("--lexicon")) {

                opsArray[i] = true;
                extOpsChecker(args, i);

                lexicon = args[i + 1];

                opsArray[i + 1] = true;
                opsCount = opsCount + 2;
                continue;
            }
            if (args[i].equals("-i") | args[i].equals("--invlists")) {

                opsArray[i] = true;
                extOpsChecker(args, i);

                invlists = args[i + 1];

                opsArray[i + 1] = true;
                opsCount = opsCount + 2;
                continue;
            }
            if (args[i].equals("-s") | args[i].equals("--stoplist")) {
                hasStoplist = true;
                opsArray[i] = true;
                extOpsChecker(args, i);

                stopfile = args[i+1];

                opsArray[i+1] = true;
                opsCount = opsCount + 2;
                continue;
            }
            if (args[i].equals("-m") | args[i].equals("--map")) {

                opsArray[i] = true;
                extOpsChecker(args, i);

                map = args[i + 1];

                opsArray[i + 1] = true;
                opsCount = opsCount + 2;

            }//TODO add access to advanced feature lol
            if (args[i].equals("-a") | args[i].equals("--advanced")) {

                opsArray[i] = true;
                advanced = true;


                extOpsChecker(args, i);
                extOpsChecker(args, i+1);

                noRelevantDocs = Integer.parseInt(args[i + 1]);
                noTermsToAdd = Integer.parseInt((args[i + 2]));
                sourcefile = args[i+3];

                opsArray[i + 1] = true;
                opsArray[i + 2] = true;
                opsArray[i + 3] = true;


                opsCount = opsCount + 4;

            }
        }
        for (int i = 0; i < args.length; i++) {

            if (!opsArray[i]) {
//                System.out.println("Q TERM FOUND");
                //currentFile = args[i];
                queryTerms.add(args[i]);
                opsCount++;
            }
        }
        if (opsCount != opsArray.length) {
            System.out.println("Length failure");
            System.out.println(opsCount + " " + opsArray.length);
            System.exit(FAILURE);
        }
    }

    private static void extOpsChecker(String[] args, int i) {
        if (args.length <= i+1) {
            exit(args[i]);
        }
        else if (args[i+1].startsWith("-")) {
            exit(args[i]);
        }
    }




    /**
     * Usage message for CLI ops
     */
    private static void usage() {
        System.out.println("Usage: Index [-p|--print] [-s|-stoplist <src>] [-t, --time]\n" +
                "          [-c, --compress <strategy>] [-h, --help] <source file>");
        System.out.println("Creates an inverted index of the supplied document");
        System.out.println("Options:");
        System.out.println("  -p, --print            Prints the cleaned text");
        System.out.println("  -s, --stoplist         Uses the supplied stoplist for processing");
        System.out.println("  -t, --time             Times the excecution time");
        System.out.println("  -c, --compress         Use variable byte compression");
        System.out.println("  -h, --help             Prints this help message and exits");
        System.out.println();
        System.out.println("Strategies:");
        System.out.println("  varbyte         ->     Variable Byte Compression");


    }
}
