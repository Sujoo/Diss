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
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class PrepareHIT2_Groups {

    private BufferedReader wordListReader;
    private PrintWriter hit2Writer;

    private ListMultimap<String, String> phraseExamplesMap;
    private Random random;
    private List<String> wordIdsToInclude;
    private static final int hitsToCreate = 50;
    private static final int groupsPerHIT = 4;

    public static void main(String[] args) throws Exception {
        PrepareHIT2_Groups p = new PrepareHIT2_Groups("ReferenceFiles\\ApparelWordList.csv", "HIT2Uploads\\ApparelGroups4.csv");
        p.prepare();
        p.outputFor4WordGroups();
        //p.outputForWordPairs();
        //p.outputPairwise();
    }

    public PrepareHIT2_Groups(String inputFile, String hit2Output) throws Exception {
        wordListReader = new BufferedReader(new FileReader(inputFile));
        hit2Writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(hit2Output), "UTF-8")));
        phraseExamplesMap = ArrayListMultimap.create();
        random = new Random();
        wordIdsToInclude = Lists.newArrayList();
    }
    
    public void setupExtraWordList() {
        String words = "35,47,54,57,59,65,66,73,83,84,90,93,96,105,110,117,146,158,159,163,168,171,174,175,181,182,186,194,201,207,212,213,215,216,219,220,223,224,225,229,235,236,240,242,243,245,248,249,251,253,254,255,257,258,259,261,263,266,267,268,273,275,278,279,280,284,289,293,295,296,297,304,306,310,314,317,318,320,327,334,336,339,348,349,351,352,355,356,358,359,361,363,364,371,372,375,376,377,378,380,381,382,383,385,388,389,391,393,395,397,406,408,410,413,417,420,421,422,428,429,431,432,437,439,440,442,443,445,446,447,454,455,458,460,463,465,467,468,470,478,479,481,482,483,486,489,491,500,506,508,512,515,516,517,521,522,524,525,526,527,529,530,532,538,540,545,546,548,549,550,551,553,555,557,558,559,560,561,565,566,567,568,569,570,571,578,579,580,581,582,583,585,589,590,591,592,595,596,598,601,606,607,608,609,611,615,616,617,618,621,623,624,627,628,630,631,633,638,639,640,642,644,648,650,653,655,656,658,661,662,664,667,669,671,674,675,677,679,680,683,685,686,687,689,690,691,693,695,697,700,703,704,705,709,710,717,718,719,722,723,725,729,730,733,735,736,737,738,741,743,745,747,748,750,755,758,759,760,761,764,766,768,769,776,777,779,784,785,787,788,791,792,793,794,798,799,800,802,809,810,811,812,813,815,816,817,818,823,824,825,826,827,832,839,840,843,846,850,851,854,856,859,860,862,863,865,870,871,873,874,876,877,879,882,883,884,885,890,892,898,900,901,902,903,904,907,908,911,914,916,918,919,920,924,926,928,931,935,937,940,941,944,946,948,950,951,953,956,958,960,962,964,966,968";
        wordIdsToInclude = Lists.newArrayList(words.split(","));        
    }

    public void prepare() throws Exception {
        setupExtraWordList();
        
        String currentLine = null;
        currentLine = wordListReader.readLine();
        while ((currentLine = wordListReader.readLine()) != null) {
            // id phrase reviewIds Sample Sentence Fragments
            String[] fields = currentLine.split("\t");
            String id = fields[0];
            String phrase = fields[1];
            // String reviewIds = fields[2];
            String frags = fields[3];

            String key = id + "_" + phrase;

//            if (reviewIds.split(",").length == 3 || wordIdsToInclude.contains(id) || reviewIds.split(",").length == 2) {
            if (wordIdsToInclude.contains(id)) {
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
        System.out.println("Word Count: " + phraseExamplesMap.keySet().size());
    }

    public void outputFor4WordGroups() {
        String header = "";
        for (int i = 1; i <= groupsPerHIT; i++) {
            header += "g" + i + "1,g" + i + "2,g" + i + "3,g" + i + "4,";
        }

        hit2Writer.println(header.substring(0, header.length()-1));
        for (int i = 1; i <= hitsToCreate; i++) {
            for (int j = 1; j <= groupsPerHIT; j++) {
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
