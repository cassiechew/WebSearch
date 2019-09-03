package quering;

import util.LexMapping;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Deprecated
public class QueryFileHandlerThread extends Thread implements Runnable {

    File fileToRead;
    fileType fileType;


    public enum fileType {
        LEXICON,
        MAP;
    }

    public QueryFileHandlerThread(String fileToRead, fileType fileType){

        this.fileToRead = new File(fileToRead);
        this.fileType = fileType;

    }

    public void run() {


        HashMap<String, LexMapping> lexicon = null;
        HashMap<Integer, String> mapping = null;

        if (this.fileType.equals(fileType.LEXICON)) {
            lexicon = new HashMap<>();
        }
        else if (this.fileType.equals(fileType.MAP)) {
            mapping = new HashMap<>();
        }

        try (
                BufferedReader bufferedReader = new BufferedReader(new FileReader(fileToRead));
                //put lexicon into memory
        ) {
            String buffer;
            while ((buffer = bufferedReader.readLine()) != null) {
                String[] splitStringData = buffer.split(" ");
                //convert 2nd/3rd to Int and Int
                switch (this.fileType) {
                    case LEXICON:
                        assert lexicon != null;
                        lexicon.put(splitStringData[0], new LexMapping(Integer.parseInt(splitStringData[1]), Integer.parseInt(splitStringData[2])));
                        break;
                    case MAP:
                        assert mapping != null;
                        mapping.put(Integer.parseInt((splitStringData[0])), splitStringData[1]);
                        break;
                    default:
                        System.out.println("Failed to read");
                        System.exit(1);
                }
                Thread.sleep(1);

            }



        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        switch (this.fileType) {
            case LEXICON:
                QueryingData.setLexicon(lexicon);
                QueryingData.setLexDone(true);
            case MAP:
                QueryingData.setMapping(mapping);
                QueryingData.setMapDone(true);
                break;
        }

    }

}
