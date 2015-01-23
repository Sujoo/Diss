package sujoo.mturk;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;

public class ProcessResultsFromHIT4 {
    private BufferedReader mTurkResultsReader;
    private BufferedReader wordListReader;
    private BufferedReader groupReader;


    private Map<Integer, String> wordListMap;
    private ListMultimap<Integer, Integer> groupWordMap;
    private Map<Integer, Multiset<String>> wordAssignmentCounts;

    // replace "," with \t
    // replace ^"(.*) with $1
    // replace (.*)"$ with $1
    // replace "\t" with ","
    // replace "" with "

    public static void main(String[] args) throws Exception {
        ProcessResultsFromHIT4 p = new ProcessResultsFromHIT4("HIT4Downloads\\ApparelGroups1.csv", "ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\ApparelGroups.csv");
        p.prepare();
        p.process();
        p.printOutput();
    }

    public ProcessResultsFromHIT4(String inputFile, String inputWordListFile, String inputGroupFile) throws Exception {
        mTurkResultsReader = new BufferedReader(new FileReader(inputFile));
        wordListReader = new BufferedReader(new FileReader(inputWordListFile));
        groupReader = new BufferedReader(new FileReader(inputGroupFile));

        wordListMap = Maps.newHashMap();
        groupWordMap = ArrayListMultimap.create();
        wordAssignmentCounts = Maps.newHashMap();
    }

    public void prepare() throws Exception {
        // 0 id 1 phrase  2 reviewIds   3 examples sentence fragments
        String currentLine = null;
        currentLine = wordListReader.readLine();
        while ((currentLine = wordListReader.readLine()) != null) {
            String[] fields = currentLine.split("\t");
            String wordId = fields[0];
            String phrase = fields[1];
            wordListMap.put(Integer.parseInt(wordId), phrase);
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
        // 27 Input.tables  28 Input.w1    29 Input.w2    30 Answer.keyphrases1  
        // 31 Answer.word#

        String currentLine = null;
        currentLine = mTurkResultsReader.readLine();
        while ((currentLine = mTurkResultsReader.readLine()) != null) {
            String[] hitResult = currentLine.split("\t");
            int wordId1 = Integer.parseInt(hitResult[28].split(" : ")[0].replace("#", ""));
            int wordId2 = Integer.parseInt(hitResult[29].split(" : ")[0].replace("#", ""));
            String[] results = hitResult[31].split("\\|");
            String result1 = results[0];
            String result2 = results[1];
            if (result1.equals("err") || result2.equals("err")) {
                System.out.println("mturk fail");
            }
            
            if (!wordAssignmentCounts.containsKey(wordId1)) {
                Multiset<String> set = HashMultiset.create();
                wordAssignmentCounts.put(wordId1, set);
            }
            if (!wordAssignmentCounts.containsKey(wordId2)) {
                Multiset<String> set = HashMultiset.create();
                wordAssignmentCounts.put(wordId2, set);
            }
            
            wordAssignmentCounts.get(wordId1).add(result1);
            wordAssignmentCounts.get(wordId2).add(result2);
        }

        mTurkResultsReader.close();
    }
    
    public void printOutput() {
        for (Integer wordId : wordAssignmentCounts.keySet()) {
            System.out.println(wordListMap.get(wordId) + " : " + wordId);
            for (String assignment : wordAssignmentCounts.get(wordId).elementSet()) {
                System.out.print("G" + assignment + ":" + wordAssignmentCounts.get(wordId).count(assignment) + ", ");
            }
            System.out.println();
            System.out.println();
        }
    }
}
