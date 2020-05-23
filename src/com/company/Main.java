package com.company;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        File testFile = new File("C:\\Users\\hamza\\IdeaProjects\\XML-JSON Converter\\src\\com\\company\\test.txt");
        StringBuilder dataString = new StringBuilder();

        try (Scanner fileScanner = new Scanner(testFile)) {

            while (fileScanner.hasNext()) {
                dataString.append(fileScanner.nextLine());
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }

        String data = dataString.toString();
        DataObject dataObject = null;

        if (data.matches("<.+(>.*<\\/.+>|\\/>)")) { //matches <tag>text</tag> Or <tag/>
            dataObject = new XMLToJSON(data);
        } else if (data.matches("\\{\\s*\"\\w*\"\\s*:.*\\}")) { // matches { "key" : "value" }
            dataObject = new JSONToXML(data);
        } else {
            System.out.println("Unknown Expression");
            System.exit(0);
        }

        dataObject.parse();
        System.out.println(dataObject.getConvertedResult());
    }
}

