package indexing; /**
 * This is a class that handles file IO.
 */



import java.io.*;

import java.util.*;

import util.Document;
import util.DocumentFactory;
import util.SkipTags;
import util.SwitchTags;


public class FileReaderS {


    /** The current data file being used */
    private File currentFile;

    @Deprecated
    /** Stoplist to compare to for word removal */
    private String stoplist;

    private Map<Integer, String> stoplistHashtable;

    /** The data map to write to the out file */
    private Map<Integer, Document> dataMap;

    private DocumentFactory documentFactory;

    private int currentDocumentCount;


    public void setCurrentFile(String fileName) {
        currentFile = new File(fileName);
    }

    public void setDocumentFactory (DocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }


    public FileReaderS() {
        currentDocumentCount = 0;
    }


    /**
     * Quick word processing method
     * @return
     */
    public ArrayList<Document> readFile() {//List<String> readFile() {

        ArrayList<Document> documentArrayList = new ArrayList<>();

        StringBuilder documentNo = new StringBuilder();
        StringBuilder header = new StringBuilder();
        StringBuilder textData = new StringBuilder();

        String buffer;

        BufferedReader reader;
        FileReader fileReader;

        boolean readHeader = false;
        boolean readText = false;

        this.dataMap = new HashMap<>();

        try {
            fileReader = new FileReader(this.currentFile);
            reader = new BufferedReader(fileReader);



            while ((buffer = reader.readLine()) != null) {

                /* This checks if the document has ended and will generate a document object */
                if (buffer.equals(SwitchTags.CLOSEDOC.getText())) {
                    documentArrayList.add(this.documentFactory.createDocument(
                            documentNo.toString(),
                            stoppingFunction(header).replaceAll("\\s+", " "),
                            stoppingFunction(textData).replaceAll("\\s+", " ")));



                            /*generateDocument(
                            documentNo.toString(), stoppingFunction(header).replaceAll("\\s+", " "),
                            stoppingFunction(textData).replaceAll("\\s+", " ")));*/
                    documentNo = new StringBuilder();
                    header = new StringBuilder();
                    textData = new StringBuilder();
                }


                /* These check if there is any key tags that appear for relevant information to start gathering */
                if (buffer.equals(SwitchTags.HEADLINE.getText()) || buffer.equals(SwitchTags.CLOSEHEAD.getText())) {
                    readHeader = !readHeader;
                }
                else if ( buffer.equals(SwitchTags.TEXT.getText()) || buffer.equals(SwitchTags.CLOSETEXT.getText())) {
                    readText = !readText;
                }
                else if (buffer.contains(SwitchTags.DOCNO.getText()) || buffer.contains(SwitchTags.CLOSEDOCNO.getText())) {
                    buffer = buffer.replace(SwitchTags.DOCNO.getText() + " ", "");
                    buffer = buffer.replace(" " + SwitchTags.CLOSEDOCNO.getText(), "");
                    documentNo.append(buffer);
                }


                /* This reads the heading of the current document being read */
                if (readHeader) {

                    if (buffer.equals(SwitchTags.HEADLINE.getText())) {
                        continue;
                    }

                    if (buffer.equals(SkipTags.PARA.getText()) || buffer.equals(SkipTags.CLOSEPARA.getText()) ||
                            buffer.equals(SkipTags.TEXT.getText()) || buffer.equals(SkipTags.HEADLINE.getText())) {
                        continue;
                    }
                    header.append(buffer.replaceAll("(?<!\\S)\\p{Punct}+|\\p{Punct}+(?!\\S)", " ")
                            .toLowerCase());
                }

                /* This reads the text content of the current document */
                if (readText) {

                    if (buffer.equals(SkipTags.PARA.getText()) || buffer.equals(SkipTags.CLOSEPARA.getText()) ||
                            buffer.equals(SkipTags.TEXT.getText()) || buffer.equals(SkipTags.HEADLINE.getText())) {
                        continue;
                    }

                //"((?<!\\w)-(?!\\w))|\\p{Punct}",[^A-Za-z0-9\s]

                    //textData.append(buffer.replaceAll("[^a-zA-Z0-9_.-]|(?<!\\S)-|-(?!\\S)|(?<!\\d)\\.|\\.(?!\\d)"," ")
                      //      .toLowerCase());

                    textData.append(buffer.replaceAll("(?<!\\S)\\p{Punct}+|\\p{Punct}+(?!\\S)", " ")
                            .toLowerCase());
                }


            }

            reader.close();


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Oops! We failed at reading the file!");
        }

        writeOutFile();
        return documentArrayList;

    }


    /**
     * Scans the list of the inputted stop word list and builds the Hashtable for function usage
     * @param stoplist
     */
    public void scanStopList (String stoplist) {

        File stoplistFile = new File (stoplist);
        //StringBuilder stringBuilder = new StringBuilder();

        //String temp;

        this.stoplistHashtable = new Hashtable<>();

        //stringBuilder.append("(?<=\\s+)\\b(");

        try {
            FileReader fileReader = new FileReader(stoplistFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String buffer;

            while ((buffer = bufferedReader.readLine()) != null) {
                //stringBuilder.append(buffer + "|");
                hashString(buffer);
            }

            //stringBuilder.deleteCharAt(stringBuilder.length()-1);
            //stringBuilder.append(")\\b(?=\\s+)");



            //this.stoplist = stringBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    /**
     * A hashing function for strings
     * @param s
     */
    public void hashString (String s) {

        this.stoplistHashtable.put(s.hashCode(), s);

    }


    /**
     * Removes all the stop words from the provided text
     * @param textData
     * @return
     */
    private String stoppingFunction (StringBuilder textData) {


        String[] textArray = textData.toString().split(" ");

        for (int i = 0; i < textArray.length; i++) {

            int hash = textArray[i].hashCode();

            if (this.stoplistHashtable.containsKey(hash)) {
                textArray[i] = "";
            }

        }

        return String.join(" ", textArray).replaceAll("\\s+", " ")
                .replaceAll("^\\s+|$\\s+", "");
        /*
        Pattern p = Pattern.compile(stoplist);
        Matcher m = p.matcher(textData.toString());
        String s = m.replaceAll(" ");

        return s;
    */

    }

    @Deprecated
    /**
     * Used to create a document
     *
     * @param documentNo The number of the document
     * @param heading The heading of the document
     * @param textData The content data of the document
     * @return
     */
    private Document generateDocument (String documentNo, String heading, String textData) {

        int id = currentDocumentCount;
        Document document = new Document(documentNo, id, heading, textData, new DocumentFactory(null));
        currentDocumentCount++;
        dataMap.put(id, document);

        return document;
    }


    /**
     * Writes the mapping data to an out file
     */
    private void writeOutFile () {

        try {
            File outfile = new File("map");

            if (!outfile.isFile() && !outfile.createNewFile())
            {
                throw new IOException("Error creating new file: " + outfile.getAbsolutePath());
            }

            FileWriter fw = new FileWriter(outfile);
            PrintWriter pw = new PrintWriter(fw);

            for (Map.Entry<Integer, Document> entry : dataMap.entrySet()) {
                pw.print(entry.getKey() + " " + entry.getValue().getDocumentNo() + "\n");
            }

            pw.flush();
            pw.close();
            fw.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }






}
