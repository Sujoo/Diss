package sujoo.mturk;

/**
 * Identify similar words in groups
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import sujoo.nlp.stanford.datatypes.WordLexem;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class PrepareForHIT2 {

    private BufferedReader wordListReader;
    private PrintWriter hit2Writer;

    private ListMultimap<String, String> phraseExamplesMap;
    private Random random;

    public static void main(String[] args) throws Exception {
        PrepareForHIT2 p = new PrepareForHIT2("ReferenceFiles\\ApparelWordList.csv", "testOutput.csv");
        p.prepare();
        //p.outputFor4WordGroups();
        //p.outputForWordPairs();
        p.outputPairwise();
    }

    public PrepareForHIT2(String inputFile, String hit2Output) throws Exception {
        wordListReader = new BufferedReader(new FileReader(inputFile));
        hit2Writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(hit2Output), "UTF-8")));
        phraseExamplesMap = ArrayListMultimap.create();
        random = new Random();
    }

    public void prepare() throws Exception {
        String currentLine = null;
        currentLine = wordListReader.readLine();
        while ((currentLine = wordListReader.readLine()) != null) {
            // id phrase reviewIds Sample Sentence Fragments
            String[] fields = currentLine.split("\t");
            String id = fields[0];
            String phrase = fields[1];
            String reviewIds = fields[2];
            String frags = fields[3];

            String key = id + "_" + phrase;

            if (reviewIds.split(",").length >= 4) {
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

                    phraseExamplesMap.put(key, examples[one]);
                    phraseExamplesMap.put(key, examples[two]);
                    phraseExamplesMap.put(key, examples[three]);
                } else {
                    for (int i = 0; i < examples.length; i++) {
                        phraseExamplesMap.put(key, examples[i]);
                    }
                }
            }
        }
    }

    public void outputFor4WordGroups() {
        hit2Writer.println("g11,g12,g13,g14,g21,g22,g23,g24,g31,g32,g33,g34,g41,g42,g43,g44,g51,g52,g53,g54");
        for (int i = 1; i <= 20; i++) {
            for (int j = 1; j <= 5; j++) {
                print4WordGroup("g" + j);
                hit2Writer.print(",");
                System.out.print("g" + j + " ");
            }
            System.out.println();
            System.out.println("hit" + i);
            hit2Writer.println();
        }
        hit2Writer.close();
    }

    public void print4WordGroup(String groupName) {
        ArrayList<String> phrases = new ArrayList<String>(phraseExamplesMap.keySet());
        String phrase1 = phrases.get(random.nextInt(phrases.size()));
        String phrase2 = phrases.get(random.nextInt(phrases.size()));
        String phrase3 = phrases.get(random.nextInt(phrases.size()));
        String phrase4 = phrases.get(random.nextInt(phrases.size()));
        while (phrase2.equals(phrase1)) {
            phrase2 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase3.equals(phrase1) || phrase3.equals(phrase2)) {
            phrase3 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase4.equals(phrase1) || phrase4.equals(phrase2) || phrase4.equals(phrase3)) {
            phrase4 = phrases.get(random.nextInt(phrases.size()));
        }
        // <span class="g1" id="0">soft<div class="ex">So very soft
        // fabric<br/>Really soft</div></span>
        hit2Writer.print(getCellHTML(groupName, phrase1) + "," + getCellHTML(groupName, phrase2) + "," + getCellHTML(groupName, phrase3) + "," + getCellHTML(groupName, phrase4));
    }

    public String getCellHTML(String groupName, String phraseKey) {
        String[] fields = phraseKey.split("_");
        String id = fields[0];
        String phrase = fields[1];

        List<String> examples = phraseExamplesMap.get(phraseKey);
        String htmlExs = "";
        for (String ex : examples) {
            htmlExs += "..." + ex + "...<br/>";
        }
        htmlExs = htmlExs.substring(0, htmlExs.length() - 5);
        return "<span><span class=" + groupName + " id=" + id + ">" + phrase + "</span><div class=ex>" + htmlExs + "</div></span>";
    }
    
    public void outputForWordPairs() {
        hit2Writer.println("g11,g12,g21,g22,g31,g32,g41,g42,g51,g52,g61,g62");
        for (int i = 1; i <= 20; i++) {
            for (int j = 1; j <= 6; j++) {
                printWordPair("g" + j);
                hit2Writer.print(",");
                System.out.print("g" + j + " ");
            }
            System.out.println();
            System.out.println("hit" + i);
            hit2Writer.println();
        }
        hit2Writer.close();
    }

    public void printWordPair(String groupName) {
        ArrayList<String> phrases = new ArrayList<String>(phraseExamplesMap.keySet());
        String phrase1 = phrases.get(random.nextInt(phrases.size()));
        String phrase2 = phrases.get(random.nextInt(phrases.size()));
        while (phrase2.equals(phrase1)) {
            phrase2 = phrases.get(random.nextInt(phrases.size()));
        }
        // <span class="g1" id="0">soft<div class="ex">So very soft
        // fabric<br/>Really soft</div></span>
        hit2Writer.print(getCellHTML(groupName, phrase1) + "," + getCellHTML(groupName, phrase2));
    }
    
    public void outputPairwise() {
        System.out.println(phraseExamplesMap.keySet().size());
        Set<Set<String>> sets = Sets.newHashSet();
        int pairsPerLineCounter = 1;
        int maxPerLine = 6;
        hit2Writer.println("g11,g12,g21,g22,g31,g32,g41,g42,g51,g52,g61,g62");
        for (String phrase1 : phraseExamplesMap.keySet()) {
            for (String phrase2 : phraseExamplesMap.keySet()) {
                Set<String> set = Sets.newTreeSet();
                set.add(phrase1);
                set.add(phrase2);
                if (!phrase1.equals(phrase2) && !sets.contains(set)) {
                    hit2Writer.print(getCellHTML("g" + pairsPerLineCounter, phrase1) + "," + getCellHTML("g" + pairsPerLineCounter, phrase2));
                    sets.add(set);
                    hit2Writer.print(",");
                    pairsPerLineCounter++;
                    if (pairsPerLineCounter > maxPerLine) {
                        pairsPerLineCounter = 1;
                        hit2Writer.println();
                    }
                }
            }
        }
        hit2Writer.close();
    }
}
