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
import com.google.common.collect.TreeMultimap;

public class PrepareHIT4_Placement {
    private BufferedReader wordListReader;
    private BufferedReader groupReader;
    private PrintWriter hit4Writer;

    private ListMultimap<Integer, String> phraseExamplesMap;
    private Map<Integer, String> wordListMap;
    private TreeMultimap<Integer, Integer> groupWordMap;
    private Random random;
    private String wordsToAssign;
    private List<List<Integer>> wordsPerHIT;
    
    private int assignPerPage;

    public static void main(String[] args) throws Exception {
        String wordsToAssign = "35,47,54,57,59,65,66,83,84,90,93,96,105,110,117,146,158,159,163,168,171,174,175,181,182,186,194,201,207,212,213,215,216,220,223,224,225,229,235,236,240,242,245,248,249,251,253,254,255,258,259,261,263,266,267,268,273,275,278,279,280,284,289,293,295,296,297,304,306,310,314,317,318,320,327,334,336,339,348,349,351,352,355,356,358,359,361,363,364,371,375,376,377,378,380,381,383,385,388,389,391,393,395,406,408,410,413,417,420,421,422,428,429,431,432,437,439,440,442,445,446,447,454,455,458,460,463,465,467,468,470,478,479,481,482,483,486,489,491,500,506,508,512,515,516,517,521,522,524,526,527,529,530,532,538,545,548,549,550,551,555,557,558,559,560,561,565,566,567,568,569,570,571,578,579,580,581,582,583,589,590,592,595,596,598,601,606,607,608,609,611,615,616,617,618,621,623,624,627,630,631,633,638,639,640,642,648,650,653,655,656,658,661,662,664,667,669,671,674,675,677,679,680,683,685,686,687,689,691,693,695,697,700,703,704,705,709,710,717,718,719,722,725,729,733,736,737,738,741,743,745,747,748,755,758,759,760,761,764,766,768,769,777,779,784,785,787,788,791,792,793,794,798,799,800,802,809,810,811,812,813,815,816,817,818,823,824,825,826,827,832,839,840,846,850,851,854,856,859,860,862,863,865,870,871,873,874,876,877,879,882,885,890,898,900,901,902,903,904,907,908,911,914,916,919,920,924,928,935,937,940,941,944,946,948,950,951,953,956,958,960,962,964,966,968";
        PrepareHIT4_Placement p = new PrepareHIT4_Placement("ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\TailApparelGroups.csv", "HIT4Uploads\\ApparelGroups4a.csv",wordsToAssign,10);
        p.prepare();
        p.outputForWordAssignment();
    }

    public PrepareHIT4_Placement(String inputWordListFile, String inputGroupFile, String hit4Output, String wordsToAssign, int assignPerPage) throws Exception {
        this.wordsToAssign = wordsToAssign;
        this.assignPerPage = assignPerPage;

        wordListReader = new BufferedReader(new FileReader(inputWordListFile));
        groupReader = new BufferedReader(new FileReader(inputGroupFile));
        hit4Writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(hit4Output), "UTF-8")));
        
        phraseExamplesMap = ArrayListMultimap.create();
        wordListMap = Maps.newHashMap();
        groupWordMap = TreeMultimap.create();
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
        String header = "tables,";
        for (int i = 1; i <= assignPerPage; i++) {
            header += "w" + i + ",";
        }
        hit4Writer.println(header.substring(0,header.length()-1));

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
            result += "<option value=" + groupId + ">" + groupId + "</option>";
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
            if (counter == 4) {
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
