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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;
import com.google.common.collect.TreeMultiset;

public class ProcessHIT2a_InterGroups {
    private BufferedReader mTurkResultsReader;
    private BufferedReader wordListReader;
    private PrintWriter groupWriter;
    private Map<Integer, String> wordListMap;

    private Multimap<String, Integer> selectedWords;

    public static void main(String[] args) throws Exception {
        ProcessHIT2a_InterGroups p = new ProcessHIT2a_InterGroups("HIT2Downloads\\BookGroups4.csv", "ReferenceFiles\\BookWordList.csv", "ReferenceFiles\\TestGroups2.csv");
        p.prepareIdFile();
        p.process();
        p.outputGroups();
    }

    public ProcessHIT2a_InterGroups(String inputFile, String wordListFile, String outputFile) throws Exception {
        mTurkResultsReader = new BufferedReader(new FileReader(inputFile));
        wordListReader = new BufferedReader(new FileReader(wordListFile));
        groupWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8")));

        wordListMap = Maps.newHashMap();
        selectedWords = ArrayListMultimap.create();
    }

    public void prepareIdFile() throws Exception {
        // 0 id 1 phrase 2 reviewIds 3 examples sentence fragments

        String currentLine = null;
        currentLine = wordListReader.readLine();
        while ((currentLine = wordListReader.readLine()) != null) {
            String[] fields = currentLine.split("\t");
            String wordId = fields[0];
            String phrase = fields[1];
            wordListMap.put(Integer.parseInt(wordId), phrase);
        }

        wordListReader.close();
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
        // 27 Input.g1 28 Answer.G1 29 Answer.keyphrases1

        String currentLine = null;
        currentLine = mTurkResultsReader.readLine();
        while ((currentLine = mTurkResultsReader.readLine()) != null) {
            String[] hitResult = currentLine.split("\t");
            String HITId = hitResult[0];
            
            if (hitResult.length < 29) {
                System.out.println("Turker did not provide any inputs: " + HITId + " : " + hitResult[15]);
            } else if (!hitResult[28].equals("1")) {
                String jsonString = hitResult[29];
                JSONArray arr = new JSONArray(jsonString);
                for (int j = 0; j < arr.length(); j++) {
                    selectedWords.put(HITId, arr.getJSONObject(j).getInt("index"));
                }   
            }

        }

        mTurkResultsReader.close();
    }

    public void outputGroups() throws Exception {
        // For each group
        for (String hitId : selectedWords.keySet()) {
            Multiset<Integer> wordIdCounts = TreeMultiset.create();
            for (Integer wordId : selectedWords.get(hitId)) {
                wordIdCounts.add(wordId);
            }
            
            for (Integer wordId : wordIdCounts.elementSet()) {
                if (wordIdCounts.count(wordId) >= 3) {
                    System.out.print(wordId + "(" + wordIdCounts.count(wordId) + "),");
                }
            }
            System.out.println();
        }
    }

}
