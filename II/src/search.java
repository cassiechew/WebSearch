import queryingModule.QueryDocumentHandler;
import queryingModule.QueryProcessing;
import util.Accumulator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Vector;
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

    private static QueryDocumentHandler queryDocumentHandler;
    private static QueryProcessing queryProcessing;

    private static DecimalFormat df = new DecimalFormat("#.###");



    public static void main(String[] args) {
        opsHandler(args);

        final long starttime = System.currentTimeMillis();

        queryDocumentHandler = new QueryDocumentHandler();

        queryDocumentHandler.generateIndexDataFromFiles(lexicon, QueryDocumentHandler.fileType.LEXICON, queryTerms);

        queryDocumentHandler.generateIndexDataFromFiles(map, QueryDocumentHandler.fileType.MAP, queryTerms);


        queryProcessing = new QueryProcessing(invlists, queryDocumentHandler.getLexicon(),
                queryDocumentHandler.getMapping(), queryDocumentHandler.getAverageDocumentLength());


        queryProcessing.accumulatorCycle(Arrays.copyOf(queryTerms.toArray(), queryTerms.size(), String[].class));

        ArrayList<Accumulator> accumulators = queryProcessing.getTopNAccumulators(numResults);


        StringBuilder sb = new StringBuilder();
        int c = 1;

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
