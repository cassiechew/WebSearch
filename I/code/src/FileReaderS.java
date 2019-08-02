/**
 * This is a class that handles file IO.
 */



import java.io.*;

import java.util.*;

import util.Document;
import util.SkipTags;
import util.SwitchTags;


public class FileReaderS {


    private static FileReaderS fileReaderS = null;

    /** The current data file being used */
    private File currentFile;

    /** The data map to write to the out file */
    private Map<UUID, String> dataMap;


    public void setCurrentFile(String fileName) {
        currentFile = new File(fileName);
    }

    /**
     * Singleton pattern implemented to ensure file safety
     * @return this object
     */
    public static FileReaderS getFileReaderS() {
        if (fileReaderS == null) {
            fileReaderS = new FileReaderS();

        }
        return fileReaderS;

    }

    private FileReaderS() {}


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
                    documentArrayList.add(generateDocument(
                            documentNo.toString(), header.toString(), textData.toString()
                    ));
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
                    header.append(buffer);
                }

                /* This reads the text content of the current document */
                if (readText) {

                    if (buffer.equals(SkipTags.PARA.getText()) || buffer.equals(SkipTags.CLOSEPARA.getText()) ||
                            buffer.equals(SkipTags.TEXT.getText()) || buffer.equals(SkipTags.HEADLINE.getText())) {
                        continue;
                    }

                    textData.append(buffer.replaceAll("\\p{Punct}", "").toLowerCase());
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
     * Used to create a document
     * @param documentNo The number of the document
     * @param heading The heading of the document
     * @param textData The content data of the document
     * @return
     */
    private Document generateDocument (String documentNo, String heading, String textData) {

        UUID uuid = UUID.randomUUID();
        dataMap.put(uuid, documentNo);

        return new Document(documentNo, uuid, heading, textData);
    }


    private void writeOutFile () {

        try {
            File outfile = new File("map");

            if (!outfile.isFile() && !outfile.createNewFile())
            {
                throw new IOException("Error creating new file: " + outfile.getAbsolutePath());
            }

            FileWriter fw = new FileWriter(outfile);
            PrintWriter pw = new PrintWriter(fw);

            for (Map.Entry<UUID, String> entry : dataMap.entrySet()) {
                pw.print(entry.getKey() + " " + entry.getValue() + "\n");
            }

            pw.flush();
            pw.close();
            fw.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

    }






}
