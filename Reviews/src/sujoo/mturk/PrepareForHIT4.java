package sujoo.mturk;

/**
 * Identify group for term
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PrepareForHIT4 {
    private BufferedReader wordListReader;
    private BufferedReader groupReader;
    private PrintWriter hit4Writer;

    private ListMultimap<Integer, String> phraseExamplesMap;
    private Map<Integer, String> wordListMap;
    private ListMultimap<Integer, Integer> groupWordMap;
    private Random random;
    private String wordsToAssign;
    private List<List<Integer>> wordsPerHIT;
    
    private static final int assignPerPage = 2;

    public static void main(String[] args) throws Exception {
        PrepareForHIT4 p = new PrepareForHIT4("ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\ApparelGroups.csv", "hit4Output.csv");
        p.prepare();
        p.outputForWordAssignment();
    }

    public PrepareForHIT4(String inputWordListFile, String inputGroupFile, String hit4Output) throws Exception {
        wordsToAssign = "1,14,17,18,22,23,25,30,31,35,39,40,45,47,48,49,51,13,27,34,20,19";
        
        wordListReader = new BufferedReader(new FileReader(inputWordListFile));
        groupReader = new BufferedReader(new FileReader(inputGroupFile));
        hit4Writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(hit4Output), "UTF-8")));
        
        phraseExamplesMap = ArrayListMultimap.create();
        wordListMap = Maps.newHashMap();
        groupWordMap = ArrayListMultimap.create();
        random = new Random();
        wordsPerHIT = Lists.newArrayList();
    }

    public void prepare() throws Exception {
        String currentLine = null;
        currentLine = wordListReader.readLine();
        while ((currentLine = wordListReader.readLine()) != null) {
            // id phrase reviewIds Sample Sentence Fragments
            String[] fields = currentLine.split("\t");
            int id = Integer.parseInt(fields[0]);
            String phrase = fields[1];
            // String reviewIds = fields[2];
            String frags = fields[3];

            wordListMap.put(id, phrase);

            String[] examples = frags.split(",");
            if (examples.length > 3) {
                int one = random.nextInt(examples.length);
                int two = random.nextInt(examples.length);
                int three = random.nextInt(examples.length);

                while (two == one) {
                    two = random.nextInt(examples.length);
                }
                while (three == two || three == one) {
                    three = random.nextInt(examples.length);
                }

                phraseExamplesMap.put(id, examples[one]);
                phraseExamplesMap.put(id, examples[two]);
                phraseExamplesMap.put(id, examples[three]);
            } else {
                for (int i = 0; i < examples.length; i++) {
                    phraseExamplesMap.put(id, examples[i]);
                }
            }
        }
        wordListReader.close();
        
        currentLine = null;
        currentLine = groupReader.readLine();
        while ((currentLine = groupReader.readLine()) != null) {
            // GroupId  Words
            String[] fields = currentLine.split("\t");
            int id = Integer.parseInt(fields[0]);
            String[] wordIds = fields[1].split(",");
            for (int i = 0; i < wordIds.length; i++) {
                groupWordMap.put(id, Integer.parseInt(wordIds[i]));            }
        }
        
        String[] words = wordsToAssign.split(",");
        int counter = 0;
        List<Integer> currentList = Lists.newArrayList();
        for (int i = 0; i < words.length; i++) {
            currentList.add(Integer.parseInt(words[i]));
            counter++;
            if (counter == assignPerPage) {
                wordsPerHIT.add(currentList);
                counter = 0;
                currentList = Lists.newArrayList();
            }
        }
        if (counter != 0) {
            wordsPerHIT.add(currentList);
        }
    }

    public void outputForWordAssignment() {        
        hit4Writer.println("tables,w1,w2");
        for (List<Integer> wordIds : wordsPerHIT) {
            printGroupTables();
            printWords(wordIds);
            hit4Writer.println();
        }
        hit4Writer.close();
    }
    
    public void printWords(List<Integer> wordIds) {
        for (Integer wordId : wordIds) {
            // #1 : fits : dropdown
            hit4Writer.print("#" + wordId + " : " + getCellHTML(wordId) + " : " + getGroupDropdownHTML() + ",");
        }
        
        if (wordIds.size() < assignPerPage) {
            for (int i = 0; i < assignPerPage - wordIds.size(); i++) {
                hit4Writer.print(" ,");
            }
        }
    }
    
    public String getGroupDropdownHTML() {
     // <select name="selectWord1">
        // <option value="err" selected>--select group--</option>
        // <option value="g1">Group 1</option>
        // <option value="g2">Group 2</option>
        // <option value="g3">Group 3</option>
        // <option value="g4">Group 4</option>
        // <option value="ng">No Similar Group</option>
        // </select>
        String result = "<select name=word#><option value=err selected>--select group--</option>";
        for (Integer groupId : groupWordMap.keySet()) {
            result += "<option value=" + groupId + ">Group " + groupId + "</option>";
        }
        result += "<option value=ng>No Similar Group</option></select>";
        return result;
    }
    
    public void printGroupTables() {
        hit4Writer.print("<table><tbody>");
        int counter = 0;
        for (Integer groupId : groupWordMap.keySet()) {
            if (counter == 0) {
                hit4Writer.print("<tr>");
            }
            hit4Writer.print("<td>");
            hit4Writer.print("<h4 style=color: gray;><strong>Group #" + groupId + "</strong></h4>");
            printGroupTable(groupId);
            hit4Writer.print("</td>");
            counter++;
            if (counter == 4) {
                hit4Writer.print("</tr>");
                counter = 0;
            }
        }
        if (counter != 0) {
            hit4Writer.print("</tr>");
        }
        hit4Writer.print("</tbody></table>,");
    }

    public void printGroupTable(int groupId) {
        hit4Writer.print("<table class=noselect><tbody>");
        int counter = 0;
        for (Integer wordId : groupWordMap.get(groupId)) {
            if (counter == 0) {
                hit4Writer.print("<tr>");
            }
            hit4Writer.print("<td>" + getCellHTML(wordId) + "</td>");
            counter++;
            if (counter == 3) {
                hit4Writer.print("</tr>");
                counter = 0;
            }
        }
        if (counter != 0) {
            hit4Writer.print("</tr>");
        }
        hit4Writer.print("</tbody></table>");
    }

    public String getCellHTML(int id) {
        // <span class="g1" id="0">soft<div class="ex">So very soft
        // fabric<br/>Really soft</div></span>

        List<String> examples = phraseExamplesMap.get(id);
        String phrase = wordListMap.get(id);
        String htmlExs = "";
        for (String ex : examples) {
            htmlExs += "..." + ex + "...<br/>";
        }
        htmlExs = htmlExs.substring(0, htmlExs.length() - 5);
        return "<span><span id=" + id + ">" + phrase + "</span><div class=ex>" + htmlExs + "</div></span>";
    }
}
