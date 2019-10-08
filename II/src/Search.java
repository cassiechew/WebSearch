import indexingModule.DocumentHandler;
import queryingModule.QueryDocumentHandler;
import queryingModule.QueryProcessing;
import queryingModule.TSV;
import util.*;

import java.util.*;
import java.text.DecimalFormat;

public class Search {


    private static boolean BM25 = false;
    private static boolean hasStoplist = false;

    private final static int FAILURE = 1;

    private static String stopfile = null;
    private static String queryLabel = null;
    private static String lexicon = null;
    private static String map = null;
    private static String invlists = null;

    private static Vector<String> queryTerms = null;

    private static int numResults = 0;

    private static boolean advanced = false;
    private static int noRelevantDocs = 1;
    private static int noTermsToAdd = 1;
    private static String sourcefile;

    private static Vector<String> termsToPrint;

    private static QueryProcessing queryProcessing;

    private static DecimalFormat df = new DecimalFormat("#.###");


    public static void main(String[] args) {
        opsHandler(args);

        final long starttime = System.currentTimeMillis();

        QueryDocumentHandler queryDocumentHandler = new QueryDocumentHandler();
        PriorityQueue<Accumulator> accumulators = new PriorityQueue<>();
        PriorityQueue<TermSelectionValue> queryCandidates = new PriorityQueue<>();
        List<Accumulator> listOfDocumentsContainingT;
        Map<String, List<Document>> potentialQueries;

        int noDocsInPool;

        termsToPrint = new Vector<>(queryTerms);

        queryDocumentHandler.generateIndexDataFromFiles(lexicon, QueryDocumentHandler.fileType.LEXICON);
        queryDocumentHandler.generateIndexDataFromFiles(map, QueryDocumentHandler.fileType.MAP);


        queryProcessing = new QueryProcessing(invlists, queryDocumentHandler.getLexicon(),
                queryDocumentHandler.getMapping(), queryDocumentHandler.getAverageDocumentLength());

        runQueryProcessing(accumulators, null, 0, (advanced)?0:numResults);

        if (advanced) {

            queryProcessing.clearCache();

            // Reset the query terms
            listOfDocumentsContainingT = new Vector<>();
            potentialQueries = new HashMap<>();

            noDocsInPool = accumulators.size();

            queryTerms.addAll(advancedProcessing(queryDocumentHandler, accumulators, queryCandidates, listOfDocumentsContainingT, potentialQueries));

            // To reset the min heap
            accumulators.clear();


            runQueryProcessing(accumulators, potentialQueries, noDocsInPool, numResults);
        }

        printResults(starttime, termsToPrint, queryProcessing.getTopNAccumulators(numResults), queryDocumentHandler);

    }


    /**
     * Function to run the query processing for ranked retrieval
     *
     * @param accumulators         The heap of accumulators
     * @param potentialQueries     The map of potential queries
     * @param noDocsInPool         The number of documents in the pool
     */
    private static void runQueryProcessing(
            PriorityQueue<Accumulator> accumulators,
            Map<String, List<Document>> potentialQueries,
            int noDocsInPool, int numDocsToGet
    ) {

        QueryProcessing.processQueriesToAccumulators(
                accumulators, numDocsToGet, potentialQueries, noDocsInPool,
                hasStoplist, queryTerms, stopfile, queryProcessing, termsToPrint
        );
    }


    /**
     * The process for advanced ranked retrieval
     *
     * @param queryDocumentHandler The query document handler
     * @param accumulators         The accumulators
     */
    private static List<String> advancedProcessing(
            QueryDocumentHandler queryDocumentHandler, PriorityQueue<Accumulator> accumulators,
            PriorityQueue<TermSelectionValue> queryCandidates, List<Accumulator> listOfDocumentsContainingT,
            Map<String, List<Document>> potentialQueries

    ) {

        Map<Integer, Document> documentMap = new HashMap<>();
        Map<Integer, Integer> offsets = new HashMap<>();
        Map<Integer, MapMapping> documentMappings = queryDocumentHandler.getMapping();

        DocumentHandler documentHandler = new DocumentHandler();
        DocumentFactory documentFactory = new DocumentFactory(documentMap);

        List<Document> documentsOfPool = new Vector<>();

        int noDocsInPool;

        documentHandler.setCurrentFile(sourcefile);
        documentHandler.setDocumentFactory(documentFactory);

        noDocsInPool = accumulators.size();

        queryDocumentHandler.getDocumentOffsets(accumulators, listOfDocumentsContainingT, documentMappings, offsets, noRelevantDocs);

        queryDocumentHandler.getDocumentsOfPool(offsets, documentHandler, documentsOfPool);

        queryDocumentHandler.processDocuments(documentsOfPool, potentialQueries);

        TSV.calculateTSVs(potentialQueries, queryCandidates, queryDocumentHandler, documentMappings, noDocsInPool);

        return getNewQueryTerms(queryCandidates);

    }


    /**
     * Gets the new query terms from the candidates
     *
     * @param queryCandidates The candidates of queries
     * @return A list of the new queries to add
     */
    private static List<String> getNewQueryTerms(PriorityQueue<TermSelectionValue> queryCandidates) {

        List<String> out = new Vector<>();

        for (int i = 0; (i < noTermsToAdd); i++) {
            TermSelectionValue termSelectionValue = Objects.requireNonNull(queryCandidates.poll());
            String insert = termSelectionValue.getName();
            out.add(insert);
        }

        return out;
    }


    /**
     * Function to print the results
     *
     * @param starttime            The time when the program started
     * @param termsToPrint         The initial terms in the query
     * @param accumulators         The accumulator scores
     * @param queryDocumentHandler The query document handler
     */
    private static void printResults(
            final long starttime, Vector<String> termsToPrint,
            PriorityQueue<Accumulator> accumulators, QueryDocumentHandler queryDocumentHandler
    ) {

        StringBuilder sb = new StringBuilder();
        int c = 1;

        System.out.print("Here are the results for: ");
        for (String s : termsToPrint) {
            System.out.print(s + " ");
        }
        System.out.println();
//        System.out.println(numResults + " " + accumulators.size());
        for (int i = 0; (i < numResults) && (0 < accumulators.size()); i++) {
            Accumulator a = Objects.requireNonNull(accumulators.poll());
            sb.append(((queryLabel != null) ? queryLabel + " " : ""));
            sb.append(queryDocumentHandler.getMapping().get(a.getDocumentID()).getDocumentNameID());
            sb.append(" ");
            sb.append(c);
            sb.append(" ");
            sb.append(df.format(a.getPartialSimilarityScore()));
            System.out.println(sb.toString());
            sb.setLength(0);
//            System.out.println(c + " " + (i<numResults) + " " + i + " " + numResults + " " + (i<accumulators.size()) + " " + accumulators.size());
            c++;
        }

        final long endtime = System.currentTimeMillis();

        System.out.println((endtime - starttime) + " ms");
    }


    /**
     * Exits the program after a failure
     *
     * @param arg The argument to reference for failure
     */
    private static void exit(String arg) {
        System.err.println("Missing file for " + arg);
        usage();
        System.exit(FAILURE);
    }


    /**
     * A simple handler for CLI options management
     *
     * @param args The cli options
     */
    private static void opsHandler(String[] args) {

        boolean[] opsArray = new boolean[args.length]; //dynamic placement of args
        int opsCount = 0;

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

                stopfile = args[i + 1];

                opsArray[i + 1] = true;
                opsCount = opsCount + 2;
                continue;
            }
            if (args[i].equals("-m") | args[i].equals("--map")) {

                opsArray[i] = true;
                extOpsChecker(args, i);

                map = args[i + 1];

                opsArray[i + 1] = true;
                opsCount = opsCount + 2;

            }
            if (args[i].equals("-a") | args[i].equals("--advanced")) {

                opsArray[i] = true;
                advanced = true;

                extOpsChecker(args, i);
                extOpsChecker(args, i + 1);
                extOpsChecker(args, i + 2);


                noRelevantDocs = Integer.parseInt(args[i + 1]);
                noTermsToAdd = Integer.parseInt((args[i + 2]));
                sourcefile = args[i + 3];

                opsArray[i + 1] = true;
                opsArray[i + 2] = true;
                opsArray[i + 3] = true;


                opsCount = opsCount + 4;

            }
        }
        for (int i = 0; i < args.length; i++) {

            if (!opsArray[i]) {
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
        if (args.length <= i + 1) {
            exit(args[i]);
        } else if (args[i + 1].startsWith("-")) {
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
