package sujoo.mturk;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

public class ProcessHIT6_TopicEvaluation {
    private BufferedReader mTurkResultsReader;
    private ListMultimap<Integer, Double> groupRatings;
    
    public static void cleanMTurkFile(String folder, String file) throws Exception {
        MTurkUtils.cleanMTurkOutputFile(folder + file);
    }

    public static void main(String[] args) throws Exception {
        System.out.println("LDA Camera");
        runEval("HIT6Downloads\\" + "LDACameraRedux.csv");
        System.out.println("LLR Camera");
        runEval("HIT6Downloads\\" + "LLRCameraRedux.csv");
        System.out.println("TEST Camera");
        runEval("HIT6Downloads\\" + "TESTCamera.csv");
        
        // cleanMTurkFile("HIT6Downloads\\", "TESTCamera");
//        System.out.println("LDA Apparel");
//        runEval("HIT6Downloads\\" + "LDAApparel.csv");
//        System.out.println("LDA Book");
//        runEval("HIT6Downloads\\" + "LDABook.csv");
//        System.out.println("LDA Camera");
//        runEval("HIT6Downloads\\" + "LDACamera.csv");
//        
//        System.out.println("LLR Apparel");
//        runEval("HIT6Downloads\\" + "LLRApparel.csv");
//        System.out.println("LLR Book");
//        runEval("HIT6Downloads\\" + "LLRBook.csv");
//        System.out.println("LLR Camera");
//        runEval("HIT6Downloads\\" + "LLRCamera.csv");
//        
//        System.out.println("NCRP Apparel");
//        runEval("HIT6Downloads\\" + "NCRPApparel.csv");
//        System.out.println("NCRP Book");
//        runEval("HIT6Downloads\\" + "NCRPBook.csv");
//        System.out.println("NCRP Camera");
//        runEval("HIT6Downloads\\" + "NCRPCamera.csv");
    }
    
    public static void runEval(String inputFile) throws Exception {
        ProcessHIT6_TopicEvaluation p = new ProcessHIT6_TopicEvaluation(inputFile);
        p.process();
        p.printOutput();
    }

    public ProcessHIT6_TopicEvaluation(String inputFile) throws Exception {
        mTurkResultsReader = new BufferedReader(new FileReader(inputFile));
        groupRatings = ArrayListMultimap.create();
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
        // 27 Input.reviewType 28 Input.g1Id 29 Input.g1 30 Answer.rating

        String currentLine = null;
        currentLine = mTurkResultsReader.readLine();
        while ((currentLine = mTurkResultsReader.readLine()) != null) {
            String[] hitResult = currentLine.split("\t");

            int groupId = Integer.parseInt(hitResult[28]);
            double rating = Double.parseDouble(hitResult[30]);
            groupRatings.put(groupId, rating);
        }

        mTurkResultsReader.close();
    }
    
    public void printOutput() {
        double ratingSum = 0;
        double ratingCount = 0;
        List<Integer> groupIds = Lists.newArrayList(groupRatings.keySet());
        Collections.sort(groupIds);
        for (int groupId : groupIds) {
            double groupRatingSum = 0;
            double groupRatingCount = 0;
            for (Double rating : groupRatings.get(groupId)) {
                ratingSum += rating;
                groupRatingSum += rating;
                ratingCount++;
                groupRatingCount++;
            }
            double groupAverage = groupRatingSum / groupRatingCount;
            System.out.println("Group " + groupId + ": " + groupAverage);
        }
        System.out.println("Total Average : " + ratingSum / ratingCount);
    }
}
