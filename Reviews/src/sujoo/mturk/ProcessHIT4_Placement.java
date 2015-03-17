package sujoo.mturk;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;

public class ProcessHIT4_Placement {
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
        ProcessHIT4_Placement p = new ProcessHIT4_Placement("HIT4Downloads\\ApparelGroups4a.csv", "ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\TailApparelGroups.csv");
        p.prepare();
        p.processFrom10();
        p.printOutput();
    }

    public ProcessHIT4_Placement(String inputFile, String inputWordListFile, String inputGroupFile) throws Exception {
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
            if (hitResult.length != 32) {
                System.out.println("hi");
            }
            int wordId1 = Integer.parseInt(hitResult[28].split(" : ")[0].replace("#", ""));
            String[] results = hitResult[31].split("\\|");
            String result1 = results[0];
            if (result1.equals("err")) {
                System.out.println("mturk fail");
            }
            if (!wordAssignmentCounts.containsKey(wordId1)) {
                Multiset<String> set = HashMultiset.create();
                wordAssignmentCounts.put(wordId1, set);
            }
            wordAssignmentCounts.get(wordId1).add(result1);
            
            if (!hitResult[29].equals("")) {
                int wordId2 = Integer.parseInt(hitResult[29].split(" : ")[0].replace("#", ""));
                String result2 = results[1];
                if (result2.equals("err")) {
                    System.out.println("mturk fail");
                }
                if (!wordAssignmentCounts.containsKey(wordId2)) {
                    Multiset<String> set = HashMultiset.create();
                    wordAssignmentCounts.put(wordId2, set);
                }
                wordAssignmentCounts.get(wordId2).add(result2);
            }
        }

        mTurkResultsReader.close();
    }
    
    public void processFrom10() throws Exception {
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
        // 28 Input.w1  29 Input.w2    30 Input.w3    31 Input.w4    32 Input.w5
        // 33 Input.w6    34 Input.w7    35 Input.w8    36 Input.w9    37 Input.w10
        // 38 Answer.keyphrases1  39 Answer.word#

        String currentLine = null;
        currentLine = mTurkResultsReader.readLine();
        while ((currentLine = mTurkResultsReader.readLine()) != null) {
            String[] hitResult = currentLine.split("\t");
            if (hitResult.length != 40) {
                System.out.println("hi");
            }
            String[] results = hitResult[39].split("\\|");

            getWordAssignment(hitResult[28], results[0]);
            getWordAssignment(hitResult[29], results[1]);
            getWordAssignment(hitResult[30], results[2]);
            getWordAssignment(hitResult[31], results[3]);
            getWordAssignment(hitResult[32], results[4]);
            getWordAssignment(hitResult[33], results[5]);
            getWordAssignment(hitResult[34], results[6]);
            getWordAssignment(hitResult[35], results[7]);
            getWordAssignment(hitResult[36], results[8]);
            getWordAssignment(hitResult[37], results[9]);
        }

        mTurkResultsReader.close();
    }
    
    public void getWordAssignment(String wordInput, String wordAssignment) {
        if (!wordInput.equals("")) {
            int wordId = Integer.parseInt(wordInput.split(" : ")[0].replace("#", ""));
            if (wordId == 657) {
                System.out.println("fit is great");
            }
            if (wordAssignment.equals("err")) {
                System.out.println("mturk fail: err");
            }
            if (!wordAssignmentCounts.containsKey(wordId)) {
                Multiset<String> set = HashMultiset.create();
                wordAssignmentCounts.put(wordId, set);
            }
            wordAssignmentCounts.get(wordId).add(wordAssignment);
        }
    }
    
    public void printOutput() {
        for (Integer wordId : wordAssignmentCounts.keySet()) {
            System.out.println(wordListMap.get(wordId) + " : " + wordId);
            int selectedGroup = 0;
            for (String assignment : wordAssignmentCounts.get(wordId).elementSet()) {
                int count = wordAssignmentCounts.get(wordId).count(assignment); 
                System.out.print("G" + assignment + ":" + count + ", ");
                if (count >= 3 && !assignment.equals("ng")) {
                    if (selectedGroup == 0) {
                        selectedGroup = Integer.parseInt(assignment);
                        groupWordMap.put(selectedGroup, wordId);
                        System.out.println("Assigned to Group " + selectedGroup);
                    } else {
                        System.out.println("wtf double 3 vote!");
                    }
                }
            }
            System.out.println();
            System.out.println();
        }
        
        for (Integer group : groupWordMap.keySet()) {
            System.out.print(group + "\t");
            String wordsCsv = "";
            for (Integer i : groupWordMap.get(group)) {
                wordsCsv += i + ",";
            }
            System.out.println(wordsCsv.subSequence(0, wordsCsv.length()-1));
        }
    }
}
