package sujoo.mturk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterators;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Sets;

import sujoo.nlp.stanford.datatypes.WordLexem;

public class ProcessHIT1_WordIdentify {
    private BufferedReader mTurkResultsReader;
    private BufferedReader reviewIdReader;
    private PrintWriter selectedWordListWriter;
    private Map<String, List<WordLexem>> reviewToWordListMap;

    private ListMultimap<String, WordLexem> keyphrasesPerReview;
    private ListMultimap<String, Integer> indexesPerReview;

    private String reviewIdFile;
    private int outputWordListIndexCounter = 1;

    // replace "," with \t
    // replace ^"(.*) with $1
    // replace (.*)"$ with $1
    // replace "\t" with ","
    // replace "" with "

    public static void main(String[] args) throws Exception {
        //ProcessResultsFromHIT1 p = new ProcessResultsFromHIT1("HIT1Downloads\\allApparelResults.csv", "ReferenceFiles\\ApparelReviewIds.csv", "ReferenceFiles\\ApparelWordList.csv");
        //ProcessResultsFromHIT1 p = new ProcessResultsFromHIT1("HIT1Downloads\\bookResults.csv", "ReferenceFiles\\BookReviewIds.csv", "ReferenceFiles\\BookWordList.csv");
        ProcessHIT1_WordIdentify p = new ProcessHIT1_WordIdentify("HIT1Downloads\\cameraResults.csv", "ReferenceFiles\\CameraReviewIds.csv", "ReferenceFiles\\CameraWordList.csv");
        p.prepareIdFile();
        p.process();
        p.printKeyphraseResults();

        // p.processOld();
        // p.printKeyphraseResultsOld();
    }

    public ProcessHIT1_WordIdentify(String inputFile, String reviewIdFile, String wordListFile) throws Exception {
        this.reviewIdFile = reviewIdFile;
        mTurkResultsReader = new BufferedReader(new FileReader(inputFile));
        // reader = new BufferedReader(new FileReader("testresults.csv"));
        reviewIdReader = new BufferedReader(new FileReader(reviewIdFile));
        selectedWordListWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(wordListFile), "UTF-8")));

        reviewToWordListMap = Maps.newHashMap();
        keyphrasesPerReview = ArrayListMultimap.create();
        indexesPerReview = ArrayListMultimap.create();
    }

    public void prepareIdFile() throws Exception {
        // 0 reviewId 1 origReviewText 2 wordLexemList
        // 0:They:they:PRP;1:have:have:VBP;2:a:a:DT;3:very:very:RB;4:foul:foul:JJ;5:oder:oder:NN;

        String currentLine = null;
        currentLine = reviewIdReader.readLine();
        while ((currentLine = reviewIdReader.readLine()) != null) {
            String[] fields = currentLine.split("\t");
            String reviewId = fields[0];
            String[] wordList = fields[2].split("(?<!:);(?!:)");
            reviewToWordListMap.put(reviewId, new ArrayList<WordLexem>());
            for (int i = 0; i < wordList.length; i++) {
                String[] wordInfo = wordList[i].split(":");
                if (wordInfo.length == 4) {
                    reviewToWordListMap.get(reviewId).add(new WordLexem(wordInfo[1], wordInfo[2], wordInfo[3]));
                } else if (wordInfo.length == 3) {
                    reviewToWordListMap.get(reviewId).add(new WordLexem(wordInfo[1], wordInfo[2], "UK"));
                } else if (wordInfo.length == 2) {
                    reviewToWordListMap.get(reviewId).add(new WordLexem(wordInfo[1], "NotWord", "UK"));
                } else {
                    reviewToWordListMap.get(reviewId).add(new WordLexem("NotWord", "NotWord", "UK"));
                }
            }
        }

        reviewIdReader.close();
    }

    public void process() throws Exception {
        // 0 HITId 1 HITTypeId 2 Title 3 Description 4 Keywords 5 Reward 6
        // CreationTime 7 MaxAssignments 8 RequesterAnnotation
        // 9 AssignmentDurationInSeconds 10 AutoApprovalDelayInSeconds 11
        // Expiration 12 NumberOfSimilarHITs 13 LifetimeInSeconds 14
        // AssignmentId
        // 15 WorkerId 16 AssignmentStatus 17 AcceptTime 18 SubmitTime 19
        // AutoApprovalTime 20 ApprovalTime 21 RejectionTime
        // 22 RequesterFeedback 23 WorkTimeInSeconds 24 LifetimeApprovalRate 25
        // Last30DaysApprovalRate 26 Last7DaysApprovalRate
        // 27 Input.review1Id 28 Input.review2Id 29 Input.review1 30
        // Input.review2 31 Answer.keyphrases1 32 Answer.keyphrases2
        // 33 Approve 34 Reject

        String currentLine = null;
        currentLine = mTurkResultsReader.readLine();
        while ((currentLine = mTurkResultsReader.readLine()) != null) {
            String[] hitResult = currentLine.split("\t");
            if (hitResult.length > 32) {
                String reviewId1 = hitResult[27];
                String reviewId2 = hitResult[28];
                String jsonString1 = hitResult[31];
                String jsonString2 = hitResult[32];

                JSONArray arr = new JSONArray(jsonString1);
                for (int i = 0; i < arr.length(); i++) {
                    int index = Integer.parseInt(arr.getJSONObject(i).getString("index"));
                    indexesPerReview.put(reviewId1, index);
                }

                arr = new JSONArray(jsonString2);
                for (int i = 0; i < arr.length(); i++) {
                    int index = Integer.parseInt(arr.getJSONObject(i).getString("index"));
                    indexesPerReview.put(reviewId2, index);
                }
            }
        }

        mTurkResultsReader.close();
    }

    public void printKeyphraseResults() throws Exception {

        System.out.println("Total Reviews," + indexesPerReview.keySet().size());

        Multimap<String, String> allKeyphrases = HashMultimap.create();
        // For each reviewId
        for (String reviewId : indexesPerReview.keySet()) {
            // Determine which indexes (aka words) are selected by at least two
            // Turkers
            Set<Integer> selectedIndexes = Sets.newTreeSet();
            Multiset<Integer> allIndexes = HashMultiset.create(indexesPerReview.get(reviewId));
            for (Integer index : allIndexes.elementSet()) {
                if (allIndexes.count(index) >= 2) {
                    selectedIndexes.add(index);
                }
            }

            // Determine the selected words and phrases
            PeekingIterator<Integer> itr = Iterators.peekingIterator(selectedIndexes.iterator());
            while (itr.hasNext()) {
                int currentIndex = itr.next();
                int firstIndex = currentIndex;
                String phrase = reviewToWordListMap.get(reviewId).get(currentIndex).simpleString();
                while (itr.hasNext() && itr.peek() == (currentIndex + 1)) {
                    currentIndex = itr.next();
                    phrase += " " + reviewToWordListMap.get(reviewId).get(currentIndex).simpleString();
                }
                int lastIndex = currentIndex;
                allKeyphrases.put(phrase, reviewId + "-" + firstIndex + "-" + lastIndex);
            }
        }
        
        for (String phrase : Multisets.copyHighestCountFirst(allKeyphrases.keys()).elementSet()) {
            ArrayList<String> list = new ArrayList<String>(allKeyphrases.get(phrase));
            writeLineForSelectedPhrase(phrase, list);
        }

        selectedWordListWriter.close();

        List<String> keyList = Lists.newArrayList();
        for (String term : Multisets.copyHighestCountFirst(allKeyphrases.keys()).elementSet()) {
            System.out.println(term + "," + allKeyphrases.get(term).size());

            if (allKeyphrases.get(term).size() > 1) {
                for (int i = 0; i < allKeyphrases.get(term).size(); i++) {
                    keyList.add(term);
                }
            }
        }
        //
        // Random r = new Random();
        // System.out.println();
        // System.out.println("Test Clusters");
        // for (int i = 0; i < 20; i++) {
        // Set<String> currentWords = Sets.newHashSet();
        // for (int j = 0; j < 4; j++) {
        // String potentialWord = keyList.get(r.nextInt(keyList.size()));
        // while (currentWords.contains(potentialWord)) {
        // potentialWord = keyList.get(r.nextInt(keyList.size()));
        // }
        // System.out.print(potentialWord + " | ");
        // currentWords.add(potentialWord);
        // }
        // System.out.println();
        // }

    }

    public void writeLineForSelectedPhrase(String phrase, ArrayList<String> reviewIndexList) throws Exception {
        // 0 reviewId 1 origReviewText 2 wordLexemList
        // 0:They:they:PRP;1:have:have:VBP;2:a:a:DT;3:very:very:RB;4:foul:foul:JJ;5:oder:oder:NN;
        
        String reviewIndexes = "";
        String exampleFragments = "";
        for (String reviewPhraseIndex : reviewIndexList) {
            String[] parts = reviewPhraseIndex.split("-");
            String reviewId = parts[0];
            int firstIndex = Integer.parseInt(parts[1]);
            int lastIndex = Integer.parseInt(parts[2]);
            
            reviewIndexes += reviewId + ",";
            
            reviewIdReader = new BufferedReader(new FileReader(reviewIdFile));
            String currentLine = null;
            currentLine = reviewIdReader.readLine();
            while ((currentLine = reviewIdReader.readLine()) != null) {
                String[] fields = currentLine.split("\t");
                String fileReviewId = fields[0];
                if (reviewId.equals(fileReviewId)) {
                    String currentExample = "";
                    String[] wordList = fields[2].split("(?<!:);(?!:)");
                    // Include the previous 4 words
                    for (int i = firstIndex - 4; i < firstIndex; i++) {
                        if (i >= 0) {
                            String[] wordInfo = wordList[i].split(":");
                            currentExample += wordInfo[1] + " ";
                        }
                    }
                    // Include the phrase
                    for (int i = firstIndex; i <= lastIndex; i++) {
                        // 0 index, 1 word, 2 lexem, 3 pos
                        String[] wordInfo = wordList[i].split(":");
                        currentExample += wordInfo[1] + " ";
                    }
                    // Include the last 4 words
                    for (int i = lastIndex + 1; i < lastIndex + 4; i++) {
                        if (i < wordList.length) {
                            String[] wordInfo = wordList[i].split(":");
                            currentExample += wordInfo[1] + " ";
                        }
                    }
                    
                    exampleFragments += currentExample.substring(0, currentExample.length()-1) + ",";
                }
            }
            reviewIdReader.close();
        }

        
        // id phrase reviewIds Sample Sentence Fragments
        selectedWordListWriter.println(outputWordListIndexCounter + "\t" + phrase + "\t" + reviewIndexes.substring(0, reviewIndexes.length()-1) + "\t" + exampleFragments.substring(0, exampleFragments.length()-1));
        outputWordListIndexCounter++;
    }

    // Legacy to keep just in case
    public void processOld() throws Exception {
        // 0 HITId 1 HITTypeId 2 Title 3 Description 4 Keywords 5 Reward 6
        // CreationTime 7 MaxAssignments 8 RequesterAnnotation
        // 9 AssignmentDurationInSeconds 10 AutoApprovalDelayInSeconds 11
        // Expiration 12 NumberOfSimilarHITs 13 LifetimeInSeconds 14
        // AssignmentId
        // 15 WorkerId 16 AssignmentStatus 17 AcceptTime 18 SubmitTime 19
        // AutoApprovalTime 20 ApprovalTime 21 RejectionTime
        // 22 RequesterFeedback 23 WorkTimeInSeconds 24 LifetimeApprovalRate 25
        // Last30DaysApprovalRate 26 Last7DaysApprovalRate
        // 27 Input.review1Id 28 Input.review2Id 29 Input.review1 30
        // Input.review2 31 Answer.keyphrases1 32 Answer.keyphrases2
        // 33 Approve 34 Reject

        String currentLine = null;
        currentLine = mTurkResultsReader.readLine();
        while ((currentLine = mTurkResultsReader.readLine()) != null) {
            String[] hitResult = currentLine.split("\t");
            if (hitResult.length > 32) {
                String reviewId1 = hitResult[27];
                String reviewId2 = hitResult[28];
                String jsonString1 = hitResult[31];
                String jsonString2 = hitResult[32];

                JSONArray arr = new JSONArray(jsonString1);
                for (int i = 0; i < arr.length(); i++) {
                    int index = Integer.parseInt(arr.getJSONObject(i).getString("index"));
                    WordLexem word = reviewToWordListMap.get(reviewId1).get(index);
                    keyphrasesPerReview.put(reviewId1, word);
                }

                arr = new JSONArray(jsonString2);
                for (int i = 0; i < arr.length(); i++) {
                    int index = Integer.parseInt(arr.getJSONObject(i).getString("index"));
                    WordLexem word = reviewToWordListMap.get(reviewId2).get(index);
                    keyphrasesPerReview.put(reviewId2, word);
                }
            }
        }

        mTurkResultsReader.close();
    }

    public void printKeyphraseResultsOld() throws Exception {
        System.out.println("Total Reviews," + keyphrasesPerReview.keySet().size());

        Multiset<WordLexem> allKeyphrases = HashMultiset.create();
        for (String reviewId : keyphrasesPerReview.keySet()) {
            Multiset<WordLexem> reviewKeyphrases = HashMultiset.create(keyphrasesPerReview.get(reviewId));
            for (WordLexem term : reviewKeyphrases.elementSet()) {
                if (reviewKeyphrases.count(term) >= 2) {
                    allKeyphrases.add(term);
                }
            }
        }

        for (WordLexem term : Multisets.copyHighestCountFirst(allKeyphrases).elementSet()) {
            System.out.println(term + "," + allKeyphrases.count(term));
        }
    }
}
