package sujoo.mturk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class MTurkUtils {

    private static Map<Integer, String> wordListMap;
    private static Map<Integer, String> wordListFullMap;
    private static ListMultimap<Integer, Integer> groupWordMap;
    private static ListMultimap<Integer, Integer> wordReviewMap;

    public static void main(String[] args) throws Exception {
        //cleanFile();
        printApparelGroups();
    }
    
    public static void cleanFile() throws Exception {
        cleanMTurkOutputFile("HIT4Downloads\\ApparelGroups4.csv","HIT4Downloads\\ApparelGroups4-.csv");
    }

    public static void printApparelGroups() throws Exception {
        readGroups("ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\ApparelGroups.csv");
        // readGroups("ReferenceFiles\\ApparelWordList.csv", "TestGroups.csv");
        printGroups();
        // findOddPhrases();
        // findMissingWords();
        // findMissingReviews();
    }

    public static void cleanMTurkOutputFile(String inputFileName, String outputFileName) throws Exception {
        BufferedReader fileReader = new BufferedReader(new FileReader(inputFileName));
        PrintWriter fileWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFileName), "UTF-8")));
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
        groupWordMap = ArrayListMultimap.create();
        wordReviewMap = ArrayListMultimap.create();
        BufferedReader wordListReader = new BufferedReader(new FileReader(wordListFile));
        BufferedReader groupReader = new BufferedReader(new FileReader(groupFile));

        String currentLine = null;
        currentLine = wordListReader.readLine();
        while ((currentLine = wordListReader.readLine()) != null) {
            String[] fields = currentLine.split("\t");
            String wordId = fields[0];
            String phrase = fields[1];
            String reviewIdList = fields[2];
            int w = Integer.parseInt(wordId);
            if (wordListMap.keySet().contains(w)) {
                System.out.println("duplicate id: " + w);
            }
            wordListMap.put(Integer.parseInt(wordId), phrase);
            wordListFullMap.put(Integer.parseInt(wordId), currentLine);
            String[] reviewIds = reviewIdList.split(",");
            for (String reviewId : reviewIds) {
                if (Integer.parseInt(reviewId) > 500) {
                    System.out.println("bad reviewId <" + reviewId + ">: " + currentLine);
                }
                wordReviewMap.put(Integer.parseInt(wordId), Integer.parseInt(reviewId));
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
            if (phrase.split(" ").length >= 4) {
                i++;
                System.out.println(wordId + "\t" + wordListFullMap.get(wordId));
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

        System.out.println(reviewIds.size());
    }

}
