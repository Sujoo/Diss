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
    private int hitsToCreate;
    private int groupsPerHIT;

    public static void main(String[] args) throws Exception {
        PrepareHIT2_Groups p = new PrepareHIT2_Groups("ReferenceFiles\\ApparelWordList.csv", "HIT2Uploads\\ApparelGroups4a.csv", 25, 1);
        p.setupExtraWordList("35,47,54,57,59,65,66,73,83,84,90,93,96,105,110,117,146,158,159,163,168,171,174,175,181,182,186,194,201,207,212,213,215,216,219,220,223,224,225,229,235,236,240,242,243,245,248,249,251,253,254,255,257,258,259,261,263,266,267,268,273,275,278,279,280,284,289,293,295,296,297,304,306,310,314,317,318,320,327,334,336,339,348,349,351,352,355,356,358,359,361,363,364,371,372,375,376,377,378,380,381,382,383,385,388,389,391,393,395,397,406,408,410,413,417,420,421,422,428,429,431,432,437,439,440,442,443,445,446,447,454,455,458,460,463,465,467,468,470,478,479,481,482,483,486,489,491,500,506,508,512,515,516,517,521,522,524,525,526,527,529,530,532,538,540,545,546,548,549,550,551,553,555,557,558,559,560,561,565,566,567,568,569,570,571,578,579,580,581,582,583,585,589,590,591,592,595,596,598,601,606,607,608,609,611,615,616,617,618,621,623,624,627,628,630,631,633,638,639,640,642,644,648,650,653,655,656,658,661,662,664,667,669,671,674,675,677,679,680,683,685,686,687,689,690,691,693,695,697,700,703,704,705,709,710,717,718,719,722,723,725,729,730,733,735,736,737,738,741,743,745,747,748,750,755,758,759,760,761,764,766,768,769,776,777,779,784,785,787,788,791,792,793,794,798,799,800,802,809,810,811,812,813,815,816,817,818,823,824,825,826,827,832,839,840,843,846,850,851,854,856,859,860,862,863,865,870,871,873,874,876,877,879,882,883,884,885,890,892,898,900,901,902,903,904,907,908,911,914,916,918,919,920,924,926,928,931,935,937,940,941,944,946,948,950,951,953,956,958,960,962,964,966,968");
        p.prepare(false);
        p.output16WordGroups();
        // p.outputFor4WordGroups();
        // p.outputForWordPairs();
        // p.outputPairwise();
    }

    public PrepareHIT2_Groups(String inputFile, String hit2Output, int hitsToCreate, int groupsPerHIT) throws Exception {
        wordListReader = new BufferedReader(new FileReader(inputFile));
        hit2Writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(hit2Output), "UTF-8")));
        phraseExamplesMap = ArrayListMultimap.create();
        random = new Random();
        wordIdsToInclude = Lists.newArrayList();
        
        this.hitsToCreate = hitsToCreate;
        this.groupsPerHIT = groupsPerHIT;
    }

    public void setupExtraWordList(String words) {
        //String words = "35,47,54,57,59,65,66,73,83,84,90,93,96,105,110,117,146,158,159,163,168,171,174,175,181,182,186,194,201,207,212,213,215,216,219,220,223,224,225,229,235,236,240,242,243,245,248,249,251,253,254,255,257,258,259,261,263,266,267,268,273,275,278,279,280,284,289,293,295,296,297,304,306,310,314,317,318,320,327,334,336,339,348,349,351,352,355,356,358,359,361,363,364,371,372,375,376,377,378,380,381,382,383,385,388,389,391,393,395,397,406,408,410,413,417,420,421,422,428,429,431,432,437,439,440,442,443,445,446,447,454,455,458,460,463,465,467,468,470,478,479,481,482,483,486,489,491,500,506,508,512,515,516,517,521,522,524,525,526,527,529,530,532,538,540,545,546,548,549,550,551,553,555,557,558,559,560,561,565,566,567,568,569,570,571,578,579,580,581,582,583,585,589,590,591,592,595,596,598,601,606,607,608,609,611,615,616,617,618,621,623,624,627,628,630,631,633,638,639,640,642,644,648,650,653,655,656,658,661,662,664,667,669,671,674,675,677,679,680,683,685,686,687,689,690,691,693,695,697,700,703,704,705,709,710,717,718,719,722,723,725,729,730,733,735,736,737,738,741,743,745,747,748,750,755,758,759,760,761,764,766,768,769,776,777,779,784,785,787,788,791,792,793,794,798,799,800,802,809,810,811,812,813,815,816,817,818,823,824,825,826,827,832,839,840,843,846,850,851,854,856,859,860,862,863,865,870,871,873,874,876,877,879,882,883,884,885,890,892,898,900,901,902,903,904,907,908,911,914,916,918,919,920,924,926,928,931,935,937,940,941,944,946,948,950,951,953,956,958,960,962,964,966,968";
        wordIdsToInclude = Lists.newArrayList(words.split(","));
    }

    public void prepare(boolean reviewLength) throws Exception {
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

            if ((wordIdsToInclude.size() > 0 && wordIdsToInclude.contains(id)) || (reviewLength && reviewIds.split(",").length >= 4)) {
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

    public void output4WordGroups() {
        String header = "";
        for (int i = 1; i <= groupsPerHIT; i++) {
            header += "g" + i + ",";
        }

        hit2Writer.println(header.substring(0, header.length() - 1));
        for (int i = 1; i <= hitsToCreate; i++) {
            for (int j = 1; j <= groupsPerHIT; j++) {
                print4WordGroup("g" + j);
                hit2Writer.print(",");
            }
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
        
        hit2Writer.print("<table class=noselect><tbody>");
        hit2Writer.print("<tr>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase1) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase2) + "</td>");
        hit2Writer.print("</tr>");
        hit2Writer.print("<tr>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase3) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase4) + "</td>");
        hit2Writer.print("</tr>");
        hit2Writer.print("</tbody></table>");
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

    public void output16WordGroups() {
        String header = "g1";

        hit2Writer.println(header);
        for (int i = 1; i <= hitsToCreate; i++) {
            print16WordGroup("g1");
            hit2Writer.print(",");
            hit2Writer.println();
        }
        hit2Writer.close();
    }

    public void print16WordGroup(String groupName) {
        ArrayList<String> phrases = new ArrayList<String>(phraseExamplesMap.keySet());
        String phrase1 = phrases.get(random.nextInt(phrases.size()));
        String phrase2 = phrases.get(random.nextInt(phrases.size()));
        String phrase3 = phrases.get(random.nextInt(phrases.size()));
        String phrase4 = phrases.get(random.nextInt(phrases.size()));
        String phrase5 = phrases.get(random.nextInt(phrases.size()));
        String phrase6 = phrases.get(random.nextInt(phrases.size()));
        String phrase7 = phrases.get(random.nextInt(phrases.size()));
        String phrase8 = phrases.get(random.nextInt(phrases.size()));
        String phrase9 = phrases.get(random.nextInt(phrases.size()));
        String phrase10 = phrases.get(random.nextInt(phrases.size()));
        String phrase11 = phrases.get(random.nextInt(phrases.size()));
        String phrase12 = phrases.get(random.nextInt(phrases.size()));
        String phrase13 = phrases.get(random.nextInt(phrases.size()));
        String phrase14 = phrases.get(random.nextInt(phrases.size()));
        String phrase15 = phrases.get(random.nextInt(phrases.size()));
        String phrase16 = phrases.get(random.nextInt(phrases.size()));
        while (phrase2.equals(phrase1)) {
            phrase2 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase3.equals(phrase1) || phrase3.equals(phrase2)) {
            phrase3 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase4.equals(phrase1) || phrase4.equals(phrase2) || phrase4.equals(phrase3)) {
            phrase4 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase5.equals(phrase1) || phrase5.equals(phrase2) || phrase5.equals(phrase3) || phrase5.equals(phrase4)) {
            phrase5 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase6.equals(phrase1) || phrase6.equals(phrase2) || phrase6.equals(phrase3) || phrase6.equals(phrase4) || phrase6.equals(phrase5)) {
            phrase6 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase7.equals(phrase1) || phrase7.equals(phrase2) || phrase7.equals(phrase3) || phrase7.equals(phrase4) || phrase7.equals(phrase5)
                || phrase7.equals(phrase6)) {
            phrase7 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase8.equals(phrase1) || phrase8.equals(phrase2) || phrase8.equals(phrase3) || phrase8.equals(phrase4) || phrase8.equals(phrase5)
                || phrase8.equals(phrase6) || phrase8.equals(phrase7)) {
            phrase8 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase9.equals(phrase1) || phrase9.equals(phrase2) || phrase9.equals(phrase3) || phrase9.equals(phrase4) || phrase9.equals(phrase5)
                || phrase9.equals(phrase6) || phrase9.equals(phrase7) || phrase9.equals(phrase8)) {
            phrase9 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase10.equals(phrase1) || phrase10.equals(phrase2) || phrase10.equals(phrase3) || phrase10.equals(phrase4)
                || phrase10.equals(phrase5) || phrase10.equals(phrase6) || phrase10.equals(phrase7) || phrase10.equals(phrase8)
                || phrase10.equals(phrase9)) {
            phrase10 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase11.equals(phrase1) || phrase11.equals(phrase2) || phrase11.equals(phrase3) || phrase11.equals(phrase4)
                || phrase11.equals(phrase5) || phrase11.equals(phrase6) || phrase11.equals(phrase7) || phrase11.equals(phrase8)
                || phrase11.equals(phrase9) || phrase11.equals(phrase10)) {
            phrase11 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase12.equals(phrase1) || phrase12.equals(phrase2) || phrase12.equals(phrase3) || phrase12.equals(phrase4)
                || phrase12.equals(phrase5) || phrase12.equals(phrase6) || phrase12.equals(phrase7) || phrase12.equals(phrase8)
                || phrase12.equals(phrase9) || phrase12.equals(phrase10) || phrase12.equals(phrase11)) {
            phrase12 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase13.equals(phrase1) || phrase13.equals(phrase2) || phrase13.equals(phrase3) || phrase13.equals(phrase4)
                || phrase13.equals(phrase5) || phrase13.equals(phrase6) || phrase13.equals(phrase7) || phrase13.equals(phrase8)
                || phrase13.equals(phrase9) || phrase13.equals(phrase10) || phrase13.equals(phrase11) || phrase13.equals(phrase12)) {
            phrase13 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase14.equals(phrase1) || phrase14.equals(phrase2) || phrase14.equals(phrase3) || phrase14.equals(phrase4)
                || phrase14.equals(phrase5) || phrase14.equals(phrase6) || phrase14.equals(phrase7) || phrase14.equals(phrase8)
                || phrase14.equals(phrase9) || phrase14.equals(phrase10) || phrase14.equals(phrase11) || phrase14.equals(phrase12)
                || phrase14.equals(phrase13)) {
            phrase14 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase15.equals(phrase1) || phrase15.equals(phrase2) || phrase15.equals(phrase3) || phrase15.equals(phrase4)
                || phrase15.equals(phrase5) || phrase15.equals(phrase6) || phrase15.equals(phrase7) || phrase15.equals(phrase8)
                || phrase15.equals(phrase9) || phrase15.equals(phrase10) || phrase15.equals(phrase11) || phrase15.equals(phrase12)
                || phrase15.equals(phrase13) || phrase15.equals(phrase14)) {
            phrase15 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase16.equals(phrase1) || phrase16.equals(phrase2) || phrase16.equals(phrase3) || phrase16.equals(phrase4)
                || phrase16.equals(phrase5) || phrase16.equals(phrase6) || phrase16.equals(phrase7) || phrase16.equals(phrase8)
                || phrase16.equals(phrase9) || phrase16.equals(phrase10) || phrase16.equals(phrase11) || phrase16.equals(phrase12)
                || phrase16.equals(phrase13) || phrase16.equals(phrase14) || phrase16.equals(phrase15)) {
            phrase16 = phrases.get(random.nextInt(phrases.size()));
        }
        hit2Writer.print("<table class=noselect><tbody>");
        hit2Writer.print("<tr>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase1) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase2) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase3) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase4) + "</td>");
        hit2Writer.print("</tr>");
        hit2Writer.print("<tr>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase5) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase6) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase7) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase8) + "</td>");
        hit2Writer.print("</tr>");
        hit2Writer.print("<tr>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase9) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase10) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase11) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase12) + "</td>");
        hit2Writer.print("</tr>");
        hit2Writer.print("<tr>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase13) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase14) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase15) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase16) + "</td>");
        hit2Writer.print("</tr>");
        hit2Writer.print("</tbody></table>");
    }
    
    public void output12WordGroups() {
        String header = "g1";

        hit2Writer.println(header);
        for (int i = 1; i <= hitsToCreate; i++) {
            print12WordGroup("g1");
            hit2Writer.print(",");
            hit2Writer.println();
        }
        hit2Writer.close();
    }

    public void print12WordGroup(String groupName) {
        ArrayList<String> phrases = new ArrayList<String>(phraseExamplesMap.keySet());
        String phrase1 = phrases.get(random.nextInt(phrases.size()));
        String phrase2 = phrases.get(random.nextInt(phrases.size()));
        String phrase3 = phrases.get(random.nextInt(phrases.size()));
        String phrase4 = phrases.get(random.nextInt(phrases.size()));
        String phrase5 = phrases.get(random.nextInt(phrases.size()));
        String phrase6 = phrases.get(random.nextInt(phrases.size()));
        String phrase7 = phrases.get(random.nextInt(phrases.size()));
        String phrase8 = phrases.get(random.nextInt(phrases.size()));
        String phrase9 = phrases.get(random.nextInt(phrases.size()));
        String phrase10 = phrases.get(random.nextInt(phrases.size()));
        String phrase11 = phrases.get(random.nextInt(phrases.size()));
        String phrase12 = phrases.get(random.nextInt(phrases.size()));
        while (phrase2.equals(phrase1)) {
            phrase2 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase3.equals(phrase1) || phrase3.equals(phrase2)) {
            phrase3 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase4.equals(phrase1) || phrase4.equals(phrase2) || phrase4.equals(phrase3)) {
            phrase4 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase5.equals(phrase1) || phrase5.equals(phrase2) || phrase5.equals(phrase3) || phrase5.equals(phrase4)) {
            phrase5 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase6.equals(phrase1) || phrase6.equals(phrase2) || phrase6.equals(phrase3) || phrase6.equals(phrase4) || phrase6.equals(phrase5)) {
            phrase6 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase7.equals(phrase1) || phrase7.equals(phrase2) || phrase7.equals(phrase3) || phrase7.equals(phrase4) || phrase7.equals(phrase5)
                || phrase7.equals(phrase6)) {
            phrase7 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase8.equals(phrase1) || phrase8.equals(phrase2) || phrase8.equals(phrase3) || phrase8.equals(phrase4) || phrase8.equals(phrase5)
                || phrase8.equals(phrase6) || phrase8.equals(phrase7)) {
            phrase8 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase9.equals(phrase1) || phrase9.equals(phrase2) || phrase9.equals(phrase3) || phrase9.equals(phrase4) || phrase9.equals(phrase5)
                || phrase9.equals(phrase6) || phrase9.equals(phrase7) || phrase9.equals(phrase8)) {
            phrase9 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase10.equals(phrase1) || phrase10.equals(phrase2) || phrase10.equals(phrase3) || phrase10.equals(phrase4)
                || phrase10.equals(phrase5) || phrase10.equals(phrase6) || phrase10.equals(phrase7) || phrase10.equals(phrase8)
                || phrase10.equals(phrase9)) {
            phrase10 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase11.equals(phrase1) || phrase11.equals(phrase2) || phrase11.equals(phrase3) || phrase11.equals(phrase4)
                || phrase11.equals(phrase5) || phrase11.equals(phrase6) || phrase11.equals(phrase7) || phrase11.equals(phrase8)
                || phrase11.equals(phrase9) || phrase11.equals(phrase10)) {
            phrase11 = phrases.get(random.nextInt(phrases.size()));
        }
        while (phrase12.equals(phrase1) || phrase12.equals(phrase2) || phrase12.equals(phrase3) || phrase12.equals(phrase4)
                || phrase12.equals(phrase5) || phrase12.equals(phrase6) || phrase12.equals(phrase7) || phrase12.equals(phrase8)
                || phrase12.equals(phrase9) || phrase12.equals(phrase10) || phrase12.equals(phrase11)) {
            phrase12 = phrases.get(random.nextInt(phrases.size()));
        }
        hit2Writer.print("<table class=noselect><tbody>");
        hit2Writer.print("<tr>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase1) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase2) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase3) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase4) + "</td>");
        hit2Writer.print("</tr>");
        hit2Writer.print("<tr>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase5) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase6) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase7) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase8) + "</td>");
        hit2Writer.print("</tr>");
        hit2Writer.print("<tr>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase9) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase10) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase11) + "</td>");
        hit2Writer.print("<td>" + getCellHTML(groupName, phrase12) + "</td>");
        hit2Writer.print("</tr>");
        hit2Writer.print("</tbody></table>");
    }
}
