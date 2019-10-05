package console_interface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SearchApplicationConsoleInterface {


    public SearchApplicationConsoleInterface() {

    }


    public int[] menu() {

        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                ) {

            String[] parsedInput;
            String userInput;

            while ((userInput = reader.readLine()) != null) {
                parsedInput = userInput.split(" ");
                if (parsedInput.length != 0) {
                    break;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }


    public int[] processInput (String[] parsedInput) {

        int[] output = new int[parsedInput.length];

        for (int i = 0; i < parsedInput.length; i++) {
        }

        return null;

    }

}
