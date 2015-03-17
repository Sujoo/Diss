package sujoo.mturk;

/**
 * Identify similar words in groups
 */

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimap;

public class PrepareHIT2a_InterGroups {

    private PrintWriter hit2Writer;

    private ListMultimap<Integer, String> phraseExamplesMap;
    private Map<Integer, String> wordListMap;
    private Multimap<Integer, Integer> groupWordMap;

    public static void main(String[] args) throws Exception {
        PrepareHIT2a_InterGroups p = new PrepareHIT2a_InterGroups("Book", "HIT2Uploads\\BookGroups4.csv");
        p.outputWordGroups();
    }

    public PrepareHIT2a_InterGroups(String type, String hit2Output) throws Exception {
        if (type.equals("Book")) {
            MTurkUtils.readBooks();
        } else if (type.equals("Camera")) {
            MTurkUtils.readCamera();
        } else if (type.equals("Apparel")) {
            MTurkUtils.readApparel();
        }
        
        phraseExamplesMap = MTurkUtils.getPhraseExamplesMap();
        wordListMap = MTurkUtils.getWordListMap();
        groupWordMap = MTurkUtils.getGroupWordMap();
        
        hit2Writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(hit2Output), "UTF-8")));
    }

    public void outputWordGroups() {
        // Print Header
        hit2Writer.println("g1");

        for (int groupId : groupWordMap.keySet()) {
            printGroupTable("g1", groupId);            
            hit2Writer.println();
        }
        hit2Writer.close();
    }

    public void printGroupTable(String groupName, int groupId) {
        hit2Writer.print("<table class=noselect><tbody>");
        int counter = 0;
        for (Integer wordId : groupWordMap.get(groupId)) {
            if (counter == 0) {
                hit2Writer.print("<tr>");
            }
            hit2Writer.print("<td>" + getCellHTML(groupName, wordId) + "</td>");
            counter++;
            if (counter == 6) {
                hit2Writer.print("</tr>");
                counter = 0;
            }
        }
        if (counter != 0) {
            hit2Writer.print("</tr>");
        }
        hit2Writer.print("</tbody></table>");
    }
    
    public String getCellHTML(String groupName, int wordId) {
        List<String> examples = phraseExamplesMap.get(wordId);
        String htmlExs = "";
        for (String ex : examples) {
            htmlExs += "..." + ex + "...<br/>";
        }
        htmlExs = htmlExs.substring(0, htmlExs.length() - 5);
        return "<span><span class=" + groupName + " id=" + wordId + ">" + wordListMap.get(wordId) + "</span><div class=ex>" + htmlExs + "</div></span>";
    }
}
