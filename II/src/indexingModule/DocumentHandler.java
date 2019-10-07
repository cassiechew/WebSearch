package indexingModule;
import util.Document;
import util.DocumentFactory;
import util.SkipTags;
import util.SwitchTags;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;


/**
 * This is a class that handles the reading and creation of documents.
 */
public class DocumentHandler {

    /**
     * The current data file being used
     */
    private File currentFile;

    private boolean hasStopFile;


    private Map<String, String> stoplistHashtable;

    /**
     * The data map to write to the out file
     */
    private Map<Integer, Document> dataMap;

    private DocumentFactory documentFactory;
    private int currentDocumentCount;

    private StringBuilder documentNo = new StringBuilder();
    private StringBuilder header = new StringBuilder();
    private StringBuilder textData = new StringBuilder();

    private long documentLocationInFile = 0;


    private boolean readHeader;
    private boolean readText;


    public void setCurrentFile(String fileName) {
        currentFile = new File(fileName);
    }

    public void setDocumentFactory(DocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }


    public DocumentHandler() {
        readHeader = false;
        readText = false;
        currentDocumentCount = 0;
        hasStopFile = false;
    }


    /**
     * Quick word processing method
     *
     * @return An arraylist of the produced documents from the infile
     */
    public List<Document> readFile() {   //read file and map doc ID (0,1,2....) to docNo

        List<Document> documentList = new Vector<>();
        String buffer;


        this.dataMap = new HashMap<>();

        try (
                FileReader fileReader = new FileReader(this.currentFile);
                LineNumberReader reader = new LineNumberReader(fileReader);
        ) {
            this.documentLocationInFile = reader.getLineNumber();
            while ((buffer = reader.readLine()) != null) {

                //System.out.print("Processing: " + (documentList.size() + 1) + " Documents read... " + "\r");
                documentProcessor(documentList, buffer, reader);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Oops! We failed at reading the file!");
        }

        syncDataMap();
        return documentList;

    }

    /**
     * Quick word processing method
     *
     * @return An arraylist of the produced documents from the infile
     */
    public List<Document> readFile(int startingOffset, int endingOffset) {   //read file and map doc ID (0,1,2....) to docNo

        List<Document> documentList = new Vector<>();
        String buffer;


        this.dataMap = new HashMap<>();

        try (
                FileReader fileReader = new FileReader(this.currentFile);
                LineNumberReader reader = new LineNumberReader(fileReader);
        ) {
            this.documentLocationInFile = reader.getLineNumber();

            while ((buffer = reader.readLine()) != null) {
                if (reader.getLineNumber() == (endingOffset + 1)) break;
                if (reader.getLineNumber() <= startingOffset) continue;
                //System.out.println(buffer);
                documentProcessor(documentList, buffer, reader);

            }

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Oops! We failed at reading the file!");
        }

        syncDataMap();
        return documentList;

    }


    private void documentProcessor(List<Document> documentList, String buffer, LineNumberReader reader) {

        /* This checks if the document has ended and will generate a document object */
        if (buffer.equals(SwitchTags.CLOSEDOC.getText())) {

            documentList.add(this.documentFactory.createDocument(
                    documentNo.toString(),
                    (hasStopFile) ? stoppingFunction(header) : header.toString(),
                    (hasStopFile) ? stoppingFunction(textData) : textData.toString(),
                    this.documentLocationInFile));
            documentNo.setLength(0);
            header.setLength(0);
            textData.setLength(0);
            this.documentLocationInFile = reader.getLineNumber();

        }

        documentTagSwitcher(buffer);
        documentContentReader(buffer);

    }


    /**
     * A function to read the content of documents
     *
     * @param buffer The current string being read
     */
    private void documentContentReader(String buffer) {
        /* This reads the heading of the current document being read */
        if (readHeader || readText) {

            if (buffer.equals(SwitchTags.HEADLINE.getText())) {
                return;
            }

            if (buffer.equals(SkipTags.PARA.getText()) || buffer.equals(SkipTags.CLOSEPARA.getText()) ||
                    buffer.equals(SkipTags.TEXT.getText()) || buffer.equals(SkipTags.HEADLINE.getText())) {
                return;
            }
            if (readHeader) {
                header.append(processString(buffer));
            } else {

                textData.append(processString(buffer));
            }
        }
    }


    /**
     * A function to quickly flag when the reader encounters a start/stop reading tag
     *
     * @param buffer The current string being read
     */
    private void documentTagSwitcher(String buffer) {
        /* These check if there is any key tags that appear for relevant information to start gathering */
        if (buffer.equals(SwitchTags.HEADLINE.getText()) || buffer.equals(SwitchTags.CLOSEHEAD.getText())) {
            readHeader = !readHeader;
        } else if (buffer.equals(SwitchTags.TEXT.getText()) || buffer.equals(SwitchTags.CLOSETEXT.getText())) {
            readText = !readText;
        } else if (buffer.contains(SwitchTags.DOCNO.getText()) || buffer.contains(SwitchTags.CLOSEDOCNO.getText())) {
            buffer = buffer.replace(SwitchTags.DOCNO.getText() + " ", "");
            buffer = buffer.replace(" " + SwitchTags.CLOSEDOCNO.getText(), "");
            documentNo.append(buffer);
        }
    }


    /**
     * A quick function to tokenize and normalize a block of text
     *
     * @param buffer The block of text to process
     * @return The processed block of text
     */
    public String processString(String buffer) {
        return buffer
                .toLowerCase().replaceAll("n't", " not")
                .replaceAll("'re", " are").replaceAll("'m", " am")
                .replaceAll("'ll", " will").replaceAll("'ve", " have")
                .replaceAll("'s", "")
                .replaceAll("(?!,)\\p{Punct}", " ")
                .replaceAll("(?<!\\S)\\p{Punct}+|\\p{Punct}+(?!\\S)", " ")
                .replaceAll(" {2}", " ");
    }


    /**
     * Synchronizes the outfile datamap with the document registry
     */
    private void syncDataMap() {
        this.dataMap = this.documentFactory.getDocumentRegistry();
    }


    /**
     * Scans the list of the inputted stop word list and builds the Hashtable for function usage
     *
     * @param stoplist The path to the stoplist to read
     */
    public void scanStopList(String stoplist) {

        File stoplistFile = new File(stoplist);

        if (!stoplistFile.exists() && !stoplistFile.isDirectory()) {
            System.out.println("This stoplist file does not exist!");
            System.exit(1);
        }
        this.hasStopFile = true;
        this.stoplistHashtable = new HashMap<>();
        try (
                FileReader fileReader = new FileReader(stoplistFile);
                BufferedReader bufferedReader = new BufferedReader(fileReader)
        ) {
            String buffer;

            while ((buffer = bufferedReader.readLine()) != null) {
                this.stoplistHashtable.put(buffer, null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * A hashing function for strings
     *
     * @param s The string to hash
     */
    @Deprecated
    private String hashString(String s)
            throws NoSuchAlgorithmException {

        /* The hashing strategy to use */
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(s.getBytes(StandardCharsets.UTF_8));

        byte[] hashBytes = md.digest();
        StringBuilder sb = new StringBuilder();

        for (byte b : hashBytes) {
            sb.append((String.format("%02x", b)));
        }

        return sb.toString();

    }


    /**
     * Removes all the stop words from the provided text
     *
     * @param textData The string of data to remove the stop words from
     * @return The string with the stop words removed
     */
    public String stoppingFunction(StringBuilder textData) {

        String[] textArray = textData.toString().split(" ");
        //String hash;


        for (int i = 0; i < textArray.length; i++) {


            if (this.stoplistHashtable.containsKey(textArray[i])) {
                textArray[i] = "";
            }
        }


        return String.join(" ", textArray).replaceAll("\\s+", " ");
    }

    /**
     * Used to create a document
     *
     * @param documentNo The number of the document
     * @param heading    The heading of the document
     * @param textData   The content data of the document
     * @return The produced document
     */
    @Deprecated
    private Document generateDocument(String documentNo, String heading, String textData) {

        int id = currentDocumentCount;
        Document document = new Document(documentNo, id, heading, textData, 0, 0, new DocumentFactory(null));
        currentDocumentCount++;
        dataMap.put(id, document);

        return document;
    }


    /**
     * Writes the mapping data to an out file
     *
     * @param mapFileName The name of the outfile to write the mapping data
     */
    public void writeOutFile(String mapFileName) {

        File outfile = new File(mapFileName);
        StringBuilder sb = new StringBuilder();

        try (
                FileWriter fw = new FileWriter(outfile)
        ) {

            /*if (!outfile.isFile() && !outfile.createNewFile())
            {
                throw new IOException("Error creating new file: " + outfile.getAbsolutePath());
            }*/

            for (Map.Entry<Integer, Document> entry : dataMap.entrySet()) {
                sb.append(entry.getKey());
                sb.append(" ");
                sb.append(entry.getValue().getDocumentNo());
                sb.append(" ");
                sb.append(entry.getValue().getDocumentLength());
                sb.append(" ");
                sb.append(entry.getValue().getDocumentLocationInFileByLine());
                sb.append("\n");
                fw.write(sb.toString());
                sb.setLength(0);
            }

            fw.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
