import util.Document;

import java.util.List;

public class Index {

    public static final int FAILURE = 1;

    public static boolean verbose = false;
    public static boolean hasStoplist = false;

    private static int argslength = 0;


    private static List<Document> parsedData;

    public static void main (String[] args) {

        FileReaderS FRS = FileReaderS.getFileReaderS();

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


        FRS.setCurrentFile(args[argslength - 1]);
        if (hasStoplist) {
            FRS.scanStopList(args[1]);
        }
        parsedData = FRS.readFile();
        if (verbose) {
            printAllDocs();
        };

    }

    public static void printDoc (int index) {
        System.out.println(parsedData.get(index).getDocumentID());
        System.out.println(parsedData.get(index).getDocumentNo());
        System.out.println(parsedData.get(index).getHeadline());
        System.out.println(parsedData.get(index).getTextData());
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
