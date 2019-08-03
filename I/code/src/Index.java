import indexing.FileReaderS;
import util.Document;
import util.DocumentFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Index {

    private static final int FAILURE = 1;

    private static boolean verbose = false;
    private static boolean hasStoplist = false;

    private static int argslength = 0;

    private static List<Document> parsedData;
    private static Map<Integer, Document> documentMap;

    public static void main (String[] args) {

        FileReaderS fileReaderS = new FileReaderS();
        documentMap = new HashMap<>();
        DocumentFactory documentFactory = new DocumentFactory(documentMap);

        switch (args.length) {

            case 1:
                argslength = 1;
                break;

            case 2:
                if (args[0].equals("-p")) {
                    verbose = true;
                    argslength = 2;
                } else {
                    System.out.println(
                            "Failed: correct usage\n\n    ./index [-s <stoplist>] [-p] <sourcefile>"
                    );
                    System.exit(FAILURE);
                }
                break;
            case 3:
                if (args[0].equals("-s") && !args[1].equals("-p")) {
                    hasStoplist = true;
                    argslength = 3;
                } else {
                    System.out.println(
                            "Failed: correct usage\n\n    ./index [-s <stoplist>] [-p] <sourcefile>"
                    );
                    System.exit(FAILURE);
                }
                break;

            case 4:
                if (args[0].equals("-s") && !args[1].equals("-p")) {
                    hasStoplist = true;
                    argslength = 4;

                    if (args[2].equals("-p")) {
                        verbose = true;
                    } else {
                        System.out.println(
                                "Failed: correct usage\n\n    ./index [-s <stoplist>] [-p] <sourcefile>"
                        );
                        System.exit(FAILURE);
                    }

                } else {
                    System.out.println(
                            "Failed: correct usage\n\n    ./index [-s <stoplist>] [-p] <sourcefile>"
                    );
                    System.exit(FAILURE);
                }
                break;

            default:
                System.out.println(
                        "Failed: correct usage\n\n    ./index [-s <stoplist>] [-p] <sourcefile>"
                );
                System.exit(FAILURE);
        }


        fileReaderS.setDocumentFactory(documentFactory);

        fileReaderS.setCurrentFile(args[argslength - 1]);
        if (hasStoplist) {
            fileReaderS.scanStopList(args[1]);
        }
        parsedData = fileReaderS.readFile();
        if (verbose) {
            for (Document d : parsedData
                 ) {
                d.printDoc();
            }
        }

    }


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

}
