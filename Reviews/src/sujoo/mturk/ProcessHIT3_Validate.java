package sujoo.mturk;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

public class ProcessHIT3_Validate {
    private BufferedReader mTurkResultsReader;
    private BufferedReader wordListReader;
    private BufferedReader groupReader;

    private Map<Integer, String> wordListMap;
    private SetMultimap<Integer, Integer> groupWordMap;
    private Multiset<Integer> okGroups;
    private Map<Integer, Multiset<Integer>> wordsDontBelongInGroup;
    private Set<Integer> groupIds;

    // replace "," with \t
    // replace ^"(.*) with $1
    // replace (.*)"$ with $1
    // replace "\t" with ","
    // replace "" with "

    public static void main(String[] args) throws Exception {
        ProcessHIT3_Validate p = new ProcessHIT3_Validate("HIT3Downloads\\ApparelGroups4.csv", "ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\ApparelGroups.csv");
        p.prepare();
        p.processFrom1PerHIT();
        p.writeOutput();
    }

    public ProcessHIT3_Validate(String inputFile, String inputWordListFile, String inputGroupFile) throws Exception {
        mTurkResultsReader = new BufferedReader(new FileReader(inputFile));
        wordListReader = new BufferedReader(new FileReader(inputWordListFile));
        groupReader = new BufferedReader(new FileReader(inputGroupFile));

        wordListMap = Maps.newHashMap();
        groupWordMap = TreeMultimap.create();
        okGroups = HashMultiset.create();
        wordsDontBelongInGroup = Maps.newHashMap();
        groupIds = Sets.newTreeSet();
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

    public void processFrom3PerHIT() throws Exception {
        // 0 HITId 1 HITTypeId 2 Title 3 Description 4 Keywords 5 Reward 6
        // CreationTime 7 MaxAssignments 8 RequesterAnnotation
        // 9 AssignmentDurationInSeconds 10 AutoApprovalDelayInSeconds 11
        // Expiration 12 NumberOfSimilarHITs 13 LifetimeInSeconds 14
        // AssignmentId
        // 15 WorkerId 16 AssignmentStatus 17 AcceptTime 18 SubmitTime 19
        // AutoApprovalTime 20 ApprovalTime 21 RejectionTime
        // 22 RequesterFeedback 23 WorkTimeInSeconds 24 LifetimeApprovalRate 25
        // Last30DaysApprovalRate 26 Last7DaysApprovalRate
        // 27 Input.g1Id    28 Input.g2Id  29 Input.g3Id
        // 30 Input.g1    31 Input.g2    32 Input.g3
        // 33 Answer.G1   34 Answer.G2   35 Answer.G3
        // 36 Answer.keyphrases1  37 Answer.keyphrases2  38 Answer.keyphrases3

        String currentLine = null;
        currentLine = mTurkResultsReader.readLine();
        while ((currentLine = mTurkResultsReader.readLine()) != null) {
            String[] hitResult = currentLine.split("\t");
            int g1Id = Integer.parseInt(hitResult[27]);
            int g2Id = Integer.parseInt(hitResult[28]);
            int g3Id = Integer.parseInt(hitResult[29]);
            groupIds.add(g1Id);
            groupIds.add(g2Id);
            groupIds.add(g3Id);
            String allBelongG1 = hitResult[33];
            String allBelongG2 = hitResult[34];
            String allBelongG3 = hitResult[35];
            String wordIdsG1 = "";
            String wordIdsG2 = "";
            String wordIdsG3 = "";
            
            if (hitResult.length > 37) {
                wordIdsG1 = hitResult[36];
                wordIdsG2 = hitResult[37];
                wordIdsG3 = hitResult[38];
            }
            
            checkResult(g1Id, allBelongG1, wordIdsG1);
            checkResult(g2Id, allBelongG2, wordIdsG2);
            checkResult(g3Id, allBelongG3, wordIdsG3);
        }

        mTurkResultsReader.close();
    }
    
    public void processFrom1PerHIT() throws Exception {
        // 0 HITId 1 HITTypeId 2 Title 3 Description 4 Keywords 5 Reward 6
        // CreationTime 7 MaxAssignments 8 RequesterAnnotation
        // 9 AssignmentDurationInSeconds 10 AutoApprovalDelayInSeconds 11
        // Expiration 12 NumberOfSimilarHITs 13 LifetimeInSeconds 14
        // AssignmentId
        // 15 WorkerId 16 AssignmentStatus 17 AcceptTime 18 SubmitTime 19
        // AutoApprovalTime 20 ApprovalTime 21 RejectionTime
        // 22 RequesterFeedback 23 WorkTimeInSeconds 24 LifetimeApprovalRate 25
        // Last30DaysApprovalRate 26 Last7DaysApprovalRate
        // 27 Input.g1Id    28 Input.g1    29 Answer.G1   30 Answer.keyphrases1

        String currentLine = null;
        currentLine = mTurkResultsReader.readLine();
        while ((currentLine = mTurkResultsReader.readLine()) != null) {
            String[] hitResult = currentLine.split("\t");
            int g1Id = Integer.parseInt(hitResult[27]);
            if (hitResult.length < 30) {
                System.out.println("Not enough turker inputs for group: " + g1Id);
            } else {
                groupIds.add(g1Id);
                String allBelongG1 = hitResult[29];
                String wordIdsG1 = "";

                if (hitResult.length > 30) {
                    wordIdsG1 = hitResult[30];
                }

                checkResult(g1Id, allBelongG1, wordIdsG1);
            }
        }

        mTurkResultsReader.close();
    }
    
    public void checkResult(int groupId, String allBelong, String wordIds) {        
        if (allBelong.equals("1") && (wordIds.equals("") || wordIds.equals("[]"))) {
            // All words belong in this group
            okGroups.add(groupId);
        } else if (allBelong.equals("") && wordIds.length() > 2) {
            // Some words do not belong in this group
            if (!wordsDontBelongInGroup.containsKey(groupId)) {
                Multiset<Integer> set = HashMultiset.create();
                wordsDontBelongInGroup.put(groupId, set);                
            }
            JSONArray arr = new JSONArray(wordIds);
            for (int j = 0; j < arr.length(); j++) {
                int wordId = Integer.parseInt(arr.getJSONObject(j).getString("index"));
                wordsDontBelongInGroup.get(groupId).add(wordId);
            }
        } else {
            System.out.println("Not enough selected terms or checkbox and term for group: " + groupId);
        }
    }
    
    public void writeOutput() {
        for (Integer groupId : groupIds) {
            System.out.println("Group: " + groupId);
            if (okGroups.count(groupId) == 5) {
                System.out.println("**Excellent : " + okGroups.count(groupId) + "**");
            } else {
                for (Integer wordId : groupWordMap.get(groupId)) {
                    System.out.print(wordListMap.get(wordId) + ", ");
                }
                System.out.println();
                System.out.println("Good Group Votes: " + okGroups.count(groupId));
                System.out.print("Words Dont Belong: ");
                if (wordsDontBelongInGroup.containsKey(groupId)) {
                    for (Integer wordId : wordsDontBelongInGroup.get(groupId).elementSet()) {
                        System.out.print(wordId + ":" + wordListMap.get(wordId) + ":" + wordsDontBelongInGroup.get(groupId).count(wordId) + ", ");
                    }
                }
                System.out.println();
            }
            System.out.println();
        }
    }

}
