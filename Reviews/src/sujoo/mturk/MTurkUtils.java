package sujoo.mturk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

public class MTurkUtils {

    private static Map<Integer, String> wordListMap;
    private static Map<Integer, String> wordListFullMap;
    private static Multimap<Integer, Integer> groupWordMap;
    private static ListMultimap<Integer, Integer> wordReviewMap;
    private static ListMultimap<Integer, String> phraseExamplesMap;
    private static Random random = new Random();

    public static void main(String[] args) throws Exception {
        printApparelGroups();
        //printBookGroups();
        //printCameraGroups();
    }
    
    public static void printApparelGroups() throws Exception {
        readGroups("ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\ApparelGroups.csv");

        printGroups();
        // findOddPhrases();
        findMissingWords();
        // findMissingReviews();
        //printWordList("15,35,47,54,59,65,66,73,81,83,84,90,96,105,111,117,155,158,159,168,169,175,179,194,207,212,216,220,223,224,225,229,235,240,245,248,251,253,254");
    }

    public static void printCameraGroups() throws Exception {
        readGroups("ReferenceFiles\\CameraWordList.csv", "ReferenceFiles\\CameraGroups.csv");

        printGroups();
        // findOddPhrases();
        findMissingWords();
        // findMissingReviews();
    }

    public static void printBookGroups() throws Exception {
        readGroups("ReferenceFiles\\BookWordList.csv", "ReferenceFiles\\BookGroups.csv");

        printGroups();
        // findOddPhrases();
        findMissingWords();
        // findMissingReviews();
    }
    
    public static void readApparel() throws Exception {
        readGroups("ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\ApparelGroups.csv");
    }
    
    public static void readCamera() throws Exception {
        readGroups("ReferenceFiles\\CameraWordList.csv", "ReferenceFiles\\CameraGroups.csv");
    }
    
    public static void readBooks() throws Exception {
        readGroups("ReferenceFiles\\BookWordList.csv", "ReferenceFiles\\BookGroups.csv");
    }
    
    public static Map<Integer, String> getWordListMap() {
        return wordListMap;
    }
    
    public static Multimap<Integer, Integer> getGroupWordMap() {
        return groupWordMap;
    }
    
    public static ListMultimap<Integer, String> getPhraseExamplesMap() {
        return phraseExamplesMap;
    }

    public static void cleanMTurkOutputFile(String inputFileName) throws Exception {
        BufferedReader fileReader = new BufferedReader(new FileReader(inputFileName));
        PrintWriter fileWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(inputFileName + ".csv"), "UTF-8")));
        String currentLine = null;
        while ((currentLine = fileReader.readLine()) != null) {
            // replace "," with \t
            // replace ^"(.*) with $1
            // replace (.*)"$ with $1
            // replace "\t" with ","
            // replace "" with "
            currentLine = currentLine.replaceAll("\",\"", "\t");
            currentLine = currentLine.replaceAll("^\"(.*)", "$1");
            currentLine = currentLine.replaceAll("(.*)\"$", "$1");
            currentLine = currentLine.replaceAll("\"\t\"", "\",\"");
            currentLine = currentLine.replaceAll("\"\"", "\"");
            fileWriter.println(currentLine);
        }
        fileReader.close();
        fileWriter.close();
    }

    public static void readGroups(String wordListFile, String groupFile) throws Exception {
        wordListMap = Maps.newTreeMap();
        wordListFullMap = Maps.newTreeMap();
        groupWordMap = TreeMultimap.create();
        wordReviewMap = ArrayListMultimap.create();
        phraseExamplesMap = ArrayListMultimap.create();
        BufferedReader wordListReader = new BufferedReader(new FileReader(wordListFile));
        BufferedReader groupReader = new BufferedReader(new FileReader(groupFile));

        String currentLine = null;
        currentLine = wordListReader.readLine();
        while ((currentLine = wordListReader.readLine()) != null) {
            String[] fields = currentLine.split("\t");
            String wordId = fields[0];
            String phrase = fields[1];
            String reviewIdList = fields[2];
            String frags = fields[3];
            int id = Integer.parseInt(wordId);
            if (wordListMap.keySet().contains(id)) {
                System.out.println("duplicate id: " + id);
            }
            wordListMap.put(id, phrase);
            wordListFullMap.put(id, currentLine);
            String[] reviewIds = reviewIdList.split(",");
            for (String reviewId : reviewIds) {
                if (Integer.parseInt(reviewId) > 500) {
                    System.out.println("bad reviewId <" + reviewId + ">: " + currentLine);
                }
                wordReviewMap.put(id, Integer.parseInt(reviewId));
            }
            
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
        System.out.println("Word list length: " + wordListMap.keySet().size());

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

    public static void printGroups() {
        for (int groupId : groupWordMap.keySet()) {
            System.out.println("Group: " + groupId);
            for (int wordId : groupWordMap.get(groupId)) {
                System.out.print(wordListMap.get(wordId) + ":" + wordId + ", ");
            }
            System.out.println();
            System.out.println();
        }
    }
    
    public static void printWordList(String w) { 
        String[] wordIds = w.split(",");
        for (int i = 0; i < wordIds.length; i++) {
            System.out.println(wordIds[i] + ": " + wordListMap.get(Integer.parseInt(wordIds[i])) + " : " + phraseExamplesMap.get(Integer.parseInt(wordIds[i])));
        }
    }

    public static void findMissingWords() {
        String missingWordIds = "";
        String missingWords = "";
        int missingCount = 0;
        for (Integer wordId : wordListMap.keySet()) {
            if (!groupWordMap.values().contains(wordId)) {
                // if (wordId < 170) {
                missingCount++;
                missingWordIds += wordId + ",";
                missingWords += wordListMap.get(wordId) + ",";
                // }
            }
        }

        System.out.println("Missing Words: " + missingCount);
        System.out.println(missingWordIds.subSequence(0, missingWordIds.length() - 1));
        System.out.println(missingWords.subSequence(0, missingWords.length() - 1));
    }

    public static void findOddPhrases() {
        int i = 0;
        for (Integer wordId : wordListMap.keySet()) {
            String phrase = wordListMap.get(wordId);
            if (phrase.split(" ").length == 2) {
                i++;
                //System.out.println(wordListFullMap.get(wordId));
                System.out.println(wordId + "\t" + wordListMap.get(wordId));
            }
        }
        System.out.println(i + " odd words found");
    }

    public static void findMissingReviews() {
        Set<Integer> reviewIds = Sets.newTreeSet();
        for (Integer wordId : wordReviewMap.keySet()) {
            if (wordId < 170) {
                reviewIds.addAll(wordReviewMap.get(wordId));
            }
        }

        System.out.println("Reviews used by groups: " + reviewIds.size());
    }

}
