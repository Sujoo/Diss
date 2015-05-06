package sujoo.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class CombineSemEvalFiles {

    public static void main(String[] args) throws Exception {
        String header = "abstract";
        String finisher = "categories and subject descriptors";
        String finisher2 = "categories and subjects descriptors";
        boolean inAbstract = false;
        File semevalFolder = new File("SemEvalFiles");
        
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("SemEvalAbstracts.txt"), "UTF-8")));
        for (final File fileEntry : semevalFolder.listFiles()) {
            BufferedReader reader = new BufferedReader(new FileReader(fileEntry.getPath()));
            String abstractText = "";
            String currentLine = null;
            while ((currentLine = reader.readLine()) != null) {
                if (currentLine.toLowerCase().startsWith(finisher) || currentLine.toLowerCase().startsWith(finisher2)) {
                    inAbstract = false;
                }
                
                if (inAbstract) {
                    abstractText += " " + currentLine;
                }
                
                if (currentLine.toLowerCase().equals(header)) {
                    inAbstract = true;
                }
            }
            writer.println(abstractText);
            reader.close();
        }
        writer.close();
    }
}
