package indexing;



import java.io.*;

import java.util.*;

import util.Document;
import util.DocumentFactory;
import util.SkipTags;
import util.SwitchTags;

/**
 * This is a class that handles the reading and creation of documents.
 */
public class DocumentHandler {

    private static final String MAPFILENAME = "map";

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


    public DocumentHandler() {
        currentDocumentCount = 0;
    }


    /**
     * Quick word processing method
     * @return An arraylist of the produced documents from the infile
     */
    public ArrayList<Document> readFile() {//List<String> readFile() {

        ArrayList<Document> documentArrayList = new ArrayList<>();

        StringBuilder documentNo = new StringBuilder();
        StringBuilder header = new StringBuilder();
        StringBuilder textData = new StringBuilder();

        String buffer;

        BufferedReader reader = null;
        FileReader fileReader = null;

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
                            stoppingFunction(header),
                            stoppingFunction(textData)));



                    documentNo.setLength(0);
                    header.setLength(0);
                    textData.setLength(0);
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
                else if (readText) {

                    if (buffer.equals(SkipTags.PARA.getText()) || buffer.equals(SkipTags.CLOSEPARA.getText()) ||
                            buffer.equals(SkipTags.TEXT.getText()) || buffer.equals(SkipTags.HEADLINE.getText())) {
                        continue;
                    }

                    textData.append(buffer.replaceAll("(?<!\\S)\\p{Punct}+|\\p{Punct}+(?!\\S)", " ")
                            .toLowerCase());
                }


            }

            reader.close();


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Oops! We failed at reading the file!");
        } finally {
            try {
                reader.close();
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        syncDataMap();
        writeOutFile();
        return documentArrayList;

    }


    /**
     * Synchronizes the outfile datamap with the document registry
     */
    private void syncDataMap () {
        this.dataMap = this.documentFactory.getDocumentRegistry();
    }


    /**
     * Scans the list of the inputted stop word list and builds the Hashtable for function usage
     * @param stoplist The path to the stoplist to read
     */
    public void scanStopList (String stoplist) {

        File stoplistFile = new File (stoplist);
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;

        this.stoplistHashtable = new Hashtable<>();

        try {
            fileReader = new FileReader(stoplistFile);
            bufferedReader = new BufferedReader(fileReader);

            String buffer;

            while ((buffer = bufferedReader.readLine()) != null) {
                hashString(buffer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedReader.close();
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    /**
     * A hashing function for strings
     * @param s The string to hash
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

        FileWriter fw = null;
        BufferedWriter pw = null;

        try {
            File outfile = new File(MAPFILENAME);

            if (!outfile.isFile() && !outfile.createNewFile())
            {
                throw new IOException("Error creating new file: " + outfile.getAbsolutePath());
            }

            fw = new FileWriter(outfile);
            //pw = new BufferedWriter(fw);


            for (Map.Entry<Integer, Document> entry : dataMap.entrySet()) {
                fw.write(entry.getKey() + " " + entry.getValue().getDocumentNo() + "\n");
            }

            fw.flush();



        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                //pw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }






}
