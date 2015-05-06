package sujoo.mturk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * Identify words that do not belong
 * 
 */

public class PrepareHIT6_TopicEvaluation {

    private BufferedReader groupReader;
    private PrintWriter hit6Writer;

    private static final int wordsPerRow = 6;
    private String reviewType;
    
    public static void main(String[] args) throws Exception {
        String type = "camera";
        String alg = "LDA";
        PrepareHIT6_TopicEvaluation p = new PrepareHIT6_TopicEvaluation(alg + "Output\\" + type + "_topics.csv", "HIT6Uploads\\" + alg + "_" + type + ".csv", type);
        p.createOutput();
    }

    public PrepareHIT6_TopicEvaluation(String inputGroupFile, String hit5Output, String reviewType) throws Exception {
        groupReader = new BufferedReader(new FileReader(inputGroupFile));
        hit6Writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(hit5Output), "UTF-8")));
        this.reviewType = reviewType;
    }

    public void createOutput() throws Exception {
        prepare();
    }

    private void prepare() throws Exception {
        hit6Writer.println("reviewType,g1Id,g1");
        
        String currentLine = null;
        int groupCounter = 1;
        while ((currentLine = groupReader.readLine()) != null) {
            // GroupId Words
            String[] words = currentLine.split(",");
            writeLine(words, groupCounter);
            groupCounter++;
        }
        groupReader.close();
        hit6Writer.close();
    }

    private void writeLine(String[] words, int groupId) {
            hit6Writer.print(reviewType + "," + groupId);
            printGroupTable(words);
            hit6Writer.println();
    }

    private void printGroupTable(String[] words) {
        hit6Writer.print(",<table class=noselect><tbody>");
        int counter = 0;
        for (int i = 0; i < words.length; i++) {
            if (counter == 0) {
                hit6Writer.print("<tr>");
            }
            hit6Writer.print("<td>" + getCellHTML(words[i]) + "</td>");
            counter++;
            if (counter == wordsPerRow) {
                hit6Writer.print("</tr>");
                counter = 0;
            }
        }
        if (counter != 0) {
            hit6Writer.print("</tr>");
        }
        hit6Writer.print("</tbody></table>");
    }

    private String getCellHTML(String word) {
        return "<span>" + word.toLowerCase() + "</span>";
    }
}
