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
import com.google.common.collect.Multiset;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

public class ProcessHIT2_Groups {
    private BufferedReader mTurkResultsReader;
    private BufferedReader wordListReader;
    private PrintWriter groupWriter;
    private Map<Integer, String> wordListMap;

    private ListMultimap<String, String> selectedWords;

    private Set<List<Integer>> selectedWordPairs;
    private SetMultimap<String, Integer> wordsInHIT;
    private Set<List<Integer>> allNonSelectedTuples;

    public static void main(String[] args) throws Exception {
        ProcessHIT2_Groups p = new ProcessHIT2_Groups("HIT2Downloads\\ApparelGroups4.csv", "ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\TestGroups.csv");
        p.prepareIdFile();
        // p.processSingleGroupHIT();
        p.process();
        // p.processPairs();
        p.outputGroups();
    }

    public ProcessHIT2_Groups(String inputFile, String wordListFile, String outputFile) throws Exception {
        mTurkResultsReader = new BufferedReader(new FileReader(inputFile));
        wordListReader = new BufferedReader(new FileReader(wordListFile));
        groupWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8")));

        wordListMap = Maps.newHashMap();
        selectedWords = ArrayListMultimap.create();
        wordsInHIT = HashMultimap.create();
        selectedWordPairs = Sets.newHashSet();
        allNonSelectedTuples = Sets.newHashSet();
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
        // 27 Input.g1  28 Input.g2    29 Input.g3    30 Input.g4    31 Input.g5
        // 32 Answer.G1   33 Answer.G2   34 Answer.G3   35 Answer.G4   36 Answer.G5
        // 37 Answer.keyphrases1  38 Answer.keyphrases2  39 Answer.keyphrases3  40 Answer.keyphrases4  41 Answer.keyphrases5
        int startingIndex = 37;
        int endingIndex = 41;

        String currentLine = null;
        currentLine = mTurkResultsReader.readLine();
        while ((currentLine = mTurkResultsReader.readLine()) != null) {
            String[] hitResult = currentLine.split("\t");
            String HITId = hitResult[0];
            addToWordsInHIT(HITId, hitResult[27], 1);
            addToWordsInHIT(HITId, hitResult[28], 2);
            addToWordsInHIT(HITId, hitResult[29], 3);
            addToWordsInHIT(HITId, hitResult[30], 4);
            addToWordsInHIT(HITId, hitResult[31], 5);
            
            if (hitResult.length < 42) {
                System.out.println("Turker did not provide any inputs: " + HITId + " : " + hitResult[15]);
            } else {
                int counter = 1;
                for (int i = startingIndex; i <= endingIndex; i++) {
                    if (!hitResult[i-5].equals("1")) {
                        String jsonString = hitResult[i];
                        JSONArray arr = new JSONArray(jsonString);
                        String selectedIndexes = "";
                        for (int j = 0; j < arr.length(); j++) {
                            selectedIndexes += arr.getJSONObject(j).getString("index") + ",";
                        }
                        if (!selectedIndexes.equals("")) {
                            selectedWords.put(HITId + "_" + counter, selectedIndexes.substring(0, selectedIndexes.length() - 1));
                        }
                        counter++;
                    }
                }
            }
        }

        mTurkResultsReader.close();
    }
    
    public void addToWordsInHIT(String HITId, String inputTable, int groupNumber) {
        Matcher m = Pattern.compile("id=\\d{1,3}").matcher(inputTable);
        while (m.find()) {
            wordsInHIT.put(HITId + "_" + groupNumber, Integer.parseInt(m.group(0).split("=")[1]));
        }
    }

    public void processSingleGroupHIT() throws Exception {
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
            String inputTable = hitResult[27];
            Matcher m = Pattern.compile("id=\\d{1,3}").matcher(inputTable);
            while (m.find()) {
                wordsInHIT.put(HITId + "_" + 1, Integer.parseInt(m.group(0).split("=")[1]));
            }
            
            if (hitResult.length < 29) {
                System.out.println("Turker did not provide any inputs: " + HITId + " : " + hitResult[15]);
            } else if (!hitResult[28].equals("1")) {
                String jsonString = hitResult[29];
                JSONArray arr = new JSONArray(jsonString);
                String selectedIndexes = "";
                for (int j = 0; j < arr.length(); j++) {
                    selectedIndexes += arr.getJSONObject(j).getString("index") + ",";
                }
                if (!selectedIndexes.equals("")) {
                    selectedWords.put(HITId + "_" + 1, selectedIndexes.substring(0, selectedIndexes.length() - 1));
                }   
            }

        }

        mTurkResultsReader.close();
    }

    public void processPairs() throws Exception {
        // 0 HITId 1 HITTypeId 2 Title 3 Description 4 Keywords 5 Reward 6
        // CreationTime 7 MaxAssignments 8 RequesterAnnotation
        // 9 AssignmentDurationInSeconds 10 AutoApprovalDelayInSeconds 11
        // Expiration 12 NumberOfSimilarHITs 13 LifetimeInSeconds 14
        // AssignmentId
        // 15 WorkerId 16 AssignmentStatus 17 AcceptTime 18 SubmitTime 19
        // AutoApprovalTime 20 ApprovalTime 21 RejectionTime
        // 22 RequesterFeedback 23 WorkTimeInSeconds 24 LifetimeApprovalRate 25
        // Last30DaysApprovalRate 26 Last7DaysApprovalRate
        // 27 Input.g11 28 Input.g12 29 Input.g21 30 Input.g22
        // 31 Input.g31 32 Input.g32 33 Input.g41 34 Input.g42
        // 35 Input.g51 36 Input.g52 37 Input.g61 38 Input.g62
        // 39 Answer.G1 40 Answer.G2 41 Answer.G3 42 Answer.G4 43 Answer.G5 44
        // Answer.G6

        String currentLine = null;
        currentLine = mTurkResultsReader.readLine();
        while ((currentLine = mTurkResultsReader.readLine()) != null) {
            int startIndex = 39;
            int endIndex = 44;
            int startWordIndex = 27;
            String[] hitResult = currentLine.split("\t");
            if (hitResult.length > 44) {
                int counter = 1;
                for (int i = startIndex; i <= endIndex; i++) {
                    int word1 = Integer.parseInt(StringUtils.substringBetween(hitResult[startWordIndex], "id=", ">"));
                    int word2 = Integer.parseInt(StringUtils.substringBetween(hitResult[startWordIndex + 1], "id=", ">"));
                    String HITId = hitResult[0];
                    wordsInHIT.put(HITId + "_" + counter, word1);
                    wordsInHIT.put(HITId + "_" + counter, word2);
                    if (hitResult[i].equals("1")) {
                        selectedWords.put(HITId + "_" + counter, word1 + "," + word2);
                    }
                    counter++;
                    startWordIndex += 2;
                }
            } else {
                System.out.println("Le Fail!");
            }
        }

        mTurkResultsReader.close();
    }

    public void outputGroups() throws Exception {
        // For each group
        for (String hitId_G : selectedWords.keySet()) {
            Multiset<List<Integer>> selectedTupleCountSet = HashMultiset.create();
            // For each response
            for (String selectedIndexes : selectedWords.get(hitId_G)) {
                String[] indexes = selectedIndexes.split(",");
                Set<List<Integer>> selectedTuples = Sets.newHashSet();
                // Generate all selected tuples
                for (int i = 0; i < indexes.length; i++) {
                    for (int j = i + 1; j < indexes.length; j++) {
                        List<Integer> tuple = Lists.newArrayList();
                        tuple.add(Integer.parseInt(indexes[i]));
                        tuple.add(Integer.parseInt(indexes[j]));
                        Collections.sort(tuple);
                        selectedTuples.add(tuple);
                    }
                }
                // Generate all tuples
                for (Integer i : wordsInHIT.get(hitId_G)) {
                    for (Integer j : wordsInHIT.get(hitId_G)) {
                        if (i != j) {
                            List<Integer> tuple = Lists.newArrayList();
                            tuple.add(i);
                            tuple.add(j);
                            Collections.sort(tuple);
                            allNonSelectedTuples.add(tuple);
                        }
                    }
                }

                // Add selected tuples to total group count
                for (List<Integer> tuple : selectedTuples) {
                    selectedTupleCountSet.add(tuple);
                }
            }
            // System.out.println("All Tuples");
            // System.out.println(allNonSelectedTuples);
            // System.out.println("Selected Tuples");
            // Identify tuples selected by 3 or more turkers per group
            for (List<Integer> tuple : selectedTupleCountSet.elementSet()) {
                // System.out.println(tuple + " : " +
                // selectedTupleCountSet.count(tuple));
                if (selectedTupleCountSet.count(tuple) >= 3) {
                    selectedWordPairs.add(tuple);
                }
            }
            // System.out.println("Final Selected");
            // System.out.println(selectedWordPairs);
            // System.out.println("Final Non Selected");
            // System.out.println(allNonSelectedTuples);
            // System.out.println();
        }

        for (List<Integer> tuple : selectedWordPairs) {
            allNonSelectedTuples.remove(tuple);
        }

        System.out.println("Final Selected: " + selectedWordPairs.size());
        System.out.println(selectedWordPairs);
        System.out.println("Final Non Selected: " + allNonSelectedTuples.size());
        System.out.println(allNonSelectedTuples);

        List<Set<Integer>> groups = Lists.newArrayList();
        for (List<Integer> selectedTuple : selectedWordPairs) {
            if (!allNonSelectedTuples.contains(selectedTuple)) {
                addSetToList(Sets.newHashSet(selectedTuple), groups);
            } else {
                System.out.println(selectedTuple);
            }
        }

        Set<Integer> wordsInGroup = Sets.newHashSet();

        groupWriter.println("GroupId\tWords");
        int groupId = 0;
        for (Set<Integer> group : groups) {
            groupId++;
            groupWriter.print(groupId + "\t");
            System.out.print(group.size() + "\t");
            String wordsCsv = "";
            for (Integer i : group) {
                wordsInGroup.add(i);
                wordsCsv += i + ",";
                System.out.print(wordListMap.get(i) + ":" + i + " , ");
            }
            groupWriter.println(wordsCsv.subSequence(0, wordsCsv.length() - 1));
            System.out.println();
        }
        groupWriter.close();

        Set<Integer> allWords = Sets.newHashSet(wordsInHIT.values());
        System.out.println("All Words Used in HIT");
        System.out.println(allWords.size());
        System.out.println(allWords);
        System.out.println("# Words placed into groups: " + wordsInGroup.size());
        System.out.println("Words not placed into groups:");
        for (Integer word : allWords) {
            if (!wordsInGroup.contains(word)) {
                System.out.print(word + ",");
            }
        }
    }

    public void addSetToList(Set<Integer> newGroup, List<Set<Integer>> groups) {
        for (Set<Integer> group : groups) {
            for (Integer i : newGroup) {
                if (group.contains(i) && canGroupsBeCombined(newGroup, group)) {
                    groups.remove(group);
                    group.addAll(newGroup);
                    addSetToList(group, groups);
                    return;
                }
            }
        }

        groups.add(newGroup);
    }

    public boolean canGroupsBeCombined(Set<Integer> group1, Set<Integer> group2) {
        boolean result = true;
        for (Integer i : group1) {
            for (Integer j : group2) {
                List<Integer> tuple = Lists.newArrayList();
                tuple.add(i);
                tuple.add(j);
                Collections.sort(tuple);
                if (allNonSelectedTuples.contains(tuple)) {
                    System.out.println("G1:" + group1 + "\tG2:" + group2 + "\tBadTuple:" + tuple);
                    return false;
                }
            }
        }
        return result;
    }

}
