package sujoo.mturk;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.json.JSONArray;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterators;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.PeekingIterator;
import com.google.common.collect.Sets;

import sujoo.nlp.stanford.datatypes.WordLexem;

public class ProcessResultsFromHIT1 {
    private BufferedReader reader;
    private BufferedReader reader2;
    private Map<String,List<WordLexem>> reviewMap;
    
    private ListMultimap<String, WordLexem> keyphrasesPerReview;
    private ListMultimap<String, Integer> indexesPerReview;

    public static void main(String[] args) throws Exception {
        ProcessResultsFromHIT1 p = new ProcessResultsFromHIT1();
        p.prepareIdFile();
        p.process();
        p.printKeyphraseResults();
        
//        p.processOld();
//        p.printKeyphraseResultsOld();
    }

    public ProcessResultsFromHIT1() throws Exception {
        reader = new BufferedReader(new FileReader("first100shoes_results.csv"));
        //reader = new BufferedReader(new FileReader("testresults.csv"));
        reader2 = new BufferedReader(new FileReader("ShoeReviewIds.csv"));
        
        reviewMap = Maps.newHashMap();
        keyphrasesPerReview = ArrayListMultimap.create();
        indexesPerReview = ArrayListMultimap.create();
    }
    
    public void prepareIdFile() throws Exception {
        // 0 reviewId 1 origReviewText  2 wordLexemList
        // 0:They:they:PRP;1:have:have:VBP;2:a:a:DT;3:very:very:RB;4:foul:foul:JJ;5:oder:oder:NN;

        String currentLine = null;
        currentLine = reader2.readLine();
        while ((currentLine = reader2.readLine()) != null) {
            String[] fields = currentLine.split("\t");
            String reviewId = fields[0];
            String[] wordList = fields[2].split("(?<!:);(?!:)");
            reviewMap.put(reviewId, new ArrayList<WordLexem>());
            for (int i = 0; i < wordList.length; i++) {
                String[] wordInfo = wordList[i].split(":");
                if (wordInfo.length == 4) {
                    reviewMap.get(reviewId).add(new WordLexem(wordInfo[1],wordInfo[2],wordInfo[3]));
                } else if (wordInfo.length == 3) {
                    reviewMap.get(reviewId).add(new WordLexem(wordInfo[1],wordInfo[2],"UK"));
                } else if (wordInfo.length == 2) {
                    reviewMap.get(reviewId).add(new WordLexem(wordInfo[1],"NotWord","UK"));
                } else {
                    reviewMap.get(reviewId).add(new WordLexem("NotWord","NotWord","UK"));
                }
            }
        }

        reader2.close();
    }

    public void process() throws Exception {        
        // 0 HITId    1 HITTypeId   2 Title   3 Description 4 Keywords    5 Reward  6 CreationTime    7 MaxAssignments  8 RequesterAnnotation
        // 9 AssignmentDurationInSeconds 10 AutoApprovalDelayInSeconds  11 Expiration  12 NumberOfSimilarHITs 13 LifetimeInSeconds   14 AssignmentId
        // 15 WorkerId    16 AssignmentStatus    17 AcceptTime  18 SubmitTime  19 AutoApprovalTime    20 ApprovalTime    21 RejectionTime
        // 22 RequesterFeedback   23 WorkTimeInSeconds   24 LifetimeApprovalRate    25 Last30DaysApprovalRate  26 Last7DaysApprovalRate
        // 27 Input.review1Id 28 Input.review2Id 29 Input.review1   30 Input.review2   31 Answer.keyphrases1  32 Answer.keyphrases2
        // 33 Approve 34 Reject
        
        String currentLine = null;
        currentLine = reader.readLine();
        while ((currentLine = reader.readLine()) != null) {
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

        reader.close();
    }
    
    public void processOld() throws Exception {        
        // 0 HITId    1 HITTypeId   2 Title   3 Description 4 Keywords    5 Reward  6 CreationTime    7 MaxAssignments  8 RequesterAnnotation
        // 9 AssignmentDurationInSeconds 10 AutoApprovalDelayInSeconds  11 Expiration  12 NumberOfSimilarHITs 13 LifetimeInSeconds   14 AssignmentId
        // 15 WorkerId    16 AssignmentStatus    17 AcceptTime  18 SubmitTime  19 AutoApprovalTime    20 ApprovalTime    21 RejectionTime
        // 22 RequesterFeedback   23 WorkTimeInSeconds   24 LifetimeApprovalRate    25 Last30DaysApprovalRate  26 Last7DaysApprovalRate
        // 27 Input.review1Id 28 Input.review2Id 29 Input.review1   30 Input.review2   31 Answer.keyphrases1  32 Answer.keyphrases2
        // 33 Approve 34 Reject
        
        String currentLine = null;
        currentLine = reader.readLine();
        while ((currentLine = reader.readLine()) != null) {
            String[] hitResult = currentLine.split("\t");
            if (hitResult.length > 32) {
                String reviewId1 = hitResult[27];
                String reviewId2 = hitResult[28];
                String jsonString1 = hitResult[31];
                String jsonString2 = hitResult[32];

                JSONArray arr = new JSONArray(jsonString1);
                for (int i = 0; i < arr.length(); i++) {
                    int index = Integer.parseInt(arr.getJSONObject(i).getString("index"));
                    WordLexem word = reviewMap.get(reviewId1).get(index);
                    keyphrasesPerReview.put(reviewId1, word);
                }
                
                arr = new JSONArray(jsonString2);
                for (int i = 0; i < arr.length(); i++) {
                    int index = Integer.parseInt(arr.getJSONObject(i).getString("index"));
                    WordLexem word = reviewMap.get(reviewId2).get(index);
                    keyphrasesPerReview.put(reviewId2, word);
                }
            }
        }

        reader.close();
    }
    
    public void printKeyphraseResults() throws Exception {
        
        System.out.println("Total Reviews," + indexesPerReview.keySet().size());
        
        Multiset<String> allKeyphrases = HashMultiset.create();
        // For each reviewId
        for (String reviewId : indexesPerReview.keySet()) {
            // Determine which indexes (aka words) are selected by at least two Turkers
            Set<Integer> selectedIndexes = Sets.newTreeSet();
            Multiset<Integer> allIndexes = HashMultiset.create(indexesPerReview.get(reviewId));
            for (Integer index : allIndexes.elementSet()) {
                if (allIndexes.count(index) >= 2) {
                    selectedIndexes.add(index);
                }
            }
            
            // Determine the selected words and phrases
            PeekingIterator<Integer> itr = Iterators.peekingIterator(selectedIndexes.iterator());
            while(itr.hasNext()) {
                int currentIndex = itr.next();
                String termThing = reviewMap.get(reviewId).get(currentIndex).shortString();
                while (itr.hasNext() && itr.peek() == (currentIndex + 1)) {
                    currentIndex = itr.next();
                    termThing += " " + reviewMap.get(reviewId).get(currentIndex).shortString();
                }

                allKeyphrases.add(termThing);
            }
        }
        
        List<String> keyList = Lists.newArrayList();
        for (String term : Multisets.copyHighestCountFirst(allKeyphrases).elementSet()) {
            System.out.println(term + "," + allKeyphrases.count(term));
            
            if (allKeyphrases.count(term) > 1) {
                for (int i = 0; i < allKeyphrases.count(term); i++) {
                    keyList.add(term);
                }
            }
        }
        
        Random r = new Random();
        System.out.println();
        System.out.println("Test Clusters");
        for (int i = 0; i < 20; i++) {
            Set<String> currentWords = Sets.newHashSet();
            for (int j = 0; j < 4; j++) {
                String potentialWord = keyList.get(r.nextInt(keyList.size()));
                while (currentWords.contains(potentialWord)) {
                    potentialWord = keyList.get(r.nextInt(keyList.size()));
                }
                System.out.print(potentialWord + " | ");
                currentWords.add(potentialWord);
            }
            System.out.println();
        }
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
