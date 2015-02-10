package sujoo.mturk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;

/**
 * Identify words that do not belong
 *
 */

public class PrepareForHIT3 {
    
    private BufferedReader wordListReader;
    private BufferedReader groupReader;
    private PrintWriter hit3Writer;

    private ListMultimap<Integer, String> phraseExamplesMap;
    private Map<Integer, String> wordListMap;
    private ListMultimap<Integer, Integer> groupWordMap;
    private Random random;
    private static final int wordsPerRow = 7;

    public static void main(String[] args) throws Exception {
        PrepareForHIT3 p = new PrepareForHIT3("ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\ApparelGroups.csv", "HIT3Uploads\\ApparelGroups4.csv");
        p.prepare();
        p.outputForInitialGroupValidation();
    }

    public PrepareForHIT3(String inputWordListFile, String inputGroupFile, String hit3Output) throws Exception {
        wordListReader = new BufferedReader(new FileReader(inputWordListFile));
        groupReader = new BufferedReader(new FileReader(inputGroupFile));
        hit3Writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(hit3Output), "UTF-8")));
        
        phraseExamplesMap = ArrayListMultimap.create();
        wordListMap = Maps.newHashMap();
        groupWordMap = ArrayListMultimap.create();
        random = new Random();
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
        groupReader.close();
    }

    public void outputForInitialGroupValidation() {        
        //hit3Writer.println("g1Id,g2Id,g3Id,g1,g2,g3");
        hit3Writer.println("g1Id,g1");
        Iterator<Integer> itr = groupWordMap.keySet().iterator();
        while (itr.hasNext()) {
            int g1Id = itr.next();
            //int g2Id = itr.next();
            //int g3Id = itr.next();
            hit3Writer.print(g1Id);// + "," + g2Id + "," + g3Id);
            printGroupTable(g1Id, "g1");
            //printGroupTable(g2Id, "g2");
            //printGroupTable(g3Id, "g3");
            hit3Writer.println();
        }
        hit3Writer.close();
    }

    public void printGroupTable(int groupId, String groupName) {
        hit3Writer.print(",<table class=noselect><tbody>");
        int counter = 0;
        for (Integer wordId : groupWordMap.get(groupId)) {
            if (counter == 0) {
                hit3Writer.print("<tr>");
            }
            hit3Writer.print("<td>" + getCellHTML(groupName, wordId) + "</td>");
            counter++;
            if (counter == wordsPerRow) {
                hit3Writer.print("</tr>");
                counter = 0;
            }
        }
        if (counter != 0) {
            hit3Writer.print("</tr>");
        }
        hit3Writer.print("</tbody></table>");
    }

    public String getCellHTML(String groupName, int id) {
        // <span class="g1" id="0">soft<div class="ex">So very soft
        // fabric<br/>Really soft</div></span>

        List<String> examples = phraseExamplesMap.get(id);
        String phrase = wordListMap.get(id);
        String htmlExs = "";
        for (String ex : examples) {
            htmlExs += "..." + ex + "...<br/>";
        }
        htmlExs = htmlExs.substring(0, htmlExs.length() - 5);
        return "<span><span class=" + groupName + " id=" + id + ">" + phrase + "</span><div class=ex>" + htmlExs + "</div></span>";
    }
}
