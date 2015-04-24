package sujoo.mturk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
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

public class PrepareHIT5_FinalValidate {

    private BufferedReader wordListReader;
    private BufferedReader groupReader;
    private PrintWriter hit5Writer;

    private ListMultimap<Integer, String> phraseExamplesMap;
    private Map<Integer, String> wordListMap;
    private ListMultimap<Integer, Integer> groupWordMap;
    private Random random;
    private static final int wordsPerRow = 6;

    public PrepareHIT5_FinalValidate(String inputWordListFile, String inputGroupFile, String hit5Output) throws Exception {
        wordListReader = new BufferedReader(new FileReader(inputWordListFile));
        groupReader = new BufferedReader(new FileReader(inputGroupFile));
        hit5Writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(hit5Output), "UTF-8")));

        phraseExamplesMap = ArrayListMultimap.create();
        wordListMap = Maps.newHashMap();
        groupWordMap = ArrayListMultimap.create();
        random = new Random();
    }

    public void createOutput() throws Exception {
        prepare();
        writeHITFile();
    }

    private void prepare() throws Exception {
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
            // GroupId Words
            String[] fields = currentLine.split("\t");
            int id = Integer.parseInt(fields[0]);
            String[] wordIds = fields[1].split(",");
            for (int i = 0; i < wordIds.length; i++) {
                groupWordMap.put(id, Integer.parseInt(wordIds[i]));
            }
        }
        groupReader.close();
    }

    private void writeHITFile() {
        hit5Writer.println("g1Id,g1");
        for (int groupId : groupWordMap.keySet()) {
            // Select random intruder
            int randomGroup = random.nextInt(groupWordMap.keySet().size()) + 1;
            while (randomGroup == groupId) {
                randomGroup = random.nextInt(groupWordMap.keySet().size()) + 1;
            }
            List<Integer> groupWords = groupWordMap.get(randomGroup);
            int randomWord = groupWords.get(random.nextInt(groupWords.size()));
            hit5Writer.print(groupId);
            printGroupTable(groupId, "g1", randomWord);
            hit5Writer.println();
            System.out.println("Group " + groupId + ": " + wordListMap.get(randomWord) + "_" + randomWord);
        }
        hit5Writer.close();
    }

    private void printGroupTable(int groupId, String groupName, Integer randomWord) {
        hit5Writer.print(",<table class=noselect><tbody>");
        List<Integer> words = groupWordMap.get(groupId);
        words.add(randomWord);
        Collections.sort(words);
        int counter = 0;
        for (Integer wordId : words) {
            if (counter == 0) {
                hit5Writer.print("<tr>");
            }
            hit5Writer.print("<td>" + getCellHTML(groupName, wordId) + "</td>");
            counter++;
            if (counter == wordsPerRow) {
                hit5Writer.print("</tr>");
                counter = 0;
            }
        }
        if (counter != 0) {
            hit5Writer.print("</tr>");
        }
        hit5Writer.print("</tbody></table>");
    }

    private String getCellHTML(String groupName, int wordId) {
        // <span class="g1" id="0">soft<div class="ex">So very soft
        // fabric<br/>Really soft</div></span>

        List<String> examples = phraseExamplesMap.get(wordId);
        String word = wordListMap.get(wordId);
        String exampleFragmetns = "";
        for (String ex : examples) {
            exampleFragmetns += "..." + ex + "...<br/>";
        }
        exampleFragmetns = exampleFragmetns.substring(0, exampleFragmetns.length() - 5);
        return "<span><span class=" + groupName + " id=" + wordId + ">" + word + " </span><div class=ex>" + exampleFragmetns + "</div></span>"
                + "<span><input type=radio name=intruder value=" + word.replace(" ", "-") + "_" + wordId + "></span>";
    }
}
