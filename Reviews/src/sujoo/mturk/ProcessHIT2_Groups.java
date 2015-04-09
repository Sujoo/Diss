package sujoo.mturk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

public class ProcessHIT2_Groups {
    private BufferedReader mTurkResultsReader;
    private BufferedReader wordListReader;
    private PrintWriter groupWriter;
    private Map<Integer, String> wordListMap;

    private Map<String, Set<Integer>> allWordsInHIT;
    private Map<String, Multiset<List<Integer>>> selectedTuplesInHIT;

    private Set<List<Integer>> selectedTuples;
    private Set<List<Integer>> nonSelectedTuples;

    public static void main(String[] args) throws Exception {
        ProcessHIT2_Groups p = new ProcessHIT2_Groups("HIT2Downloads\\ApparelGroups4.csv", "ReferenceFiles\\ApparelWordList.csv",
                "ReferenceFiles\\TestGroups.csv");
        p.prepareIdFile();
        p.processSingleGroupHIT();
        p.outputGroups();
    }

    public ProcessHIT2_Groups(String inputFile, String wordListFile, String outputFile) throws Exception {
        mTurkResultsReader = new BufferedReader(new FileReader(inputFile));
        wordListReader = new BufferedReader(new FileReader(wordListFile));
        groupWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "UTF-8")));

        wordListMap = Maps.newHashMap();
        selectedTuplesInHIT = Maps.newHashMap();
        allWordsInHIT = Maps.newHashMap();
        selectedTuples = Sets.newHashSet();
        nonSelectedTuples = Sets.newHashSet();
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
            if (!allWordsInHIT.containsKey(HITId)) {
                allWordsInHIT.put(HITId, new HashSet<Integer>());
                Multiset<List<Integer>> ms = HashMultiset.create();
                selectedTuplesInHIT.put(HITId, ms);
                Matcher m = Pattern.compile("id=\\d{1,3}").matcher(inputTable);
                // Put all of the words from a HIT into this set, if we haven't
                // done so yet
                while (m.find()) {
                    allWordsInHIT.get(HITId).add(Integer.parseInt(m.group(0).split("=")[1]));
                }
            }

            if (hitResult.length < 29) {
                System.out.println("Turker did not provide any inputs: " + HITId + " : " + hitResult[15]);
            } else if (!hitResult[28].equals("1") && !hitResult[29].equals("{}")) {
                Set<Integer> selectedWords = Sets.newHashSet();
                String jsonString = hitResult[29];
                JSONArray arr = new JSONArray(jsonString);
                for (int j = 0; j < arr.length(); j++) {
                    selectedWords.add(arr.getJSONObject(j).getInt("index"));
                }
                
                Set<List<Integer>> currentTask = Sets.newHashSet();
                if (selectedWords.size() > 1) {
                    for (int i : selectedWords) {
                        for (int j : selectedWords) {
                            if (i != j) {
                                List<Integer> tuple = Lists.newArrayList();
                                tuple.add(i);
                                tuple.add(j);
                                Collections.sort(tuple);
                                currentTask.add(tuple);
                            }
                        }
                    }
                }
                
                for (List<Integer> tuple : currentTask) {
                    selectedTuplesInHIT.get(HITId).add(tuple);
                }
            }

        }

        mTurkResultsReader.close();
    }

    public void outputGroups() throws Exception {
        // For each group
        for (String hitId : allWordsInHIT.keySet()) {
            Set<List<Integer>> allTuples = Sets.newHashSet();
            Multiset<List<Integer>> selectedHITTuples = selectedTuplesInHIT.get(hitId);
            // Generate all possible tuples from this cluster
            for (int i : allWordsInHIT.get(hitId)) {
                for (int j : allWordsInHIT.get(hitId)) {
                    if (i != j) {
                        List<Integer> tuple = Lists.newArrayList();
                        tuple.add(i);
                        tuple.add(j);
                        Collections.sort(tuple);
                        allTuples.add(tuple);
                    }
                }
            }
            
            // Find all selected Tuples
            Set<Integer> selectedWords = Sets.newHashSet();
            for (List<Integer> tuple : selectedHITTuples) {
                if (selectedHITTuples.count(tuple) >= 3) {
                    selectedTuples.add(tuple);
                    if (tuple.contains(8) && tuple.contains(14)) {
                        System.out.println(wordListMap.get(tuple.get(0)) + ", " + wordListMap.get(tuple.get(1)));
                    }
                    selectedWords.add(tuple.get(0));
                    selectedWords.add(tuple.get(1));
                    allTuples.remove(tuple);
                }
            }
            
            for (List<Integer> tuple : allTuples) {
                if (selectedWords.contains(tuple.get(0)) || selectedWords.contains(tuple.get(1))) {
                    if (tuple.contains(8) && tuple.contains(14)) {
                        System.out.println(wordListMap.get(tuple.get(0)) + ", " + wordListMap.get(tuple.get(1)));
                    }
                    nonSelectedTuples.add(tuple);
                }
            }
            
        }
        
        for (List<Integer> tuple : selectedTuples) {
            if (nonSelectedTuples.contains(tuple)) {
                nonSelectedTuples.remove(tuple);
            }
        }

        List<Set<Integer>> groups = Lists.newArrayList();
        for (List<Integer> selectedTuple : selectedTuples) {
            if (!nonSelectedTuples.contains(selectedTuple)) {
                addSetToList(Sets.newHashSet(selectedTuple), groups);
            } else {
                System.out.println("wtf" + selectedTuple);
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

        //Set<Integer> allWords = wordsInHIT.;
        //System.out.println("All Words Used in HIT");
        //System.out.println(allWords.size());
        //System.out.println(allWords);
        System.out.println("# Words placed into groups: " + wordsInGroup.size());
        System.out.println("Words not placed into groups:");
//        for (Integer word : allWords) {
//            if (!wordsInGroup.contains(word)) {
//                System.out.print(word + ",");
//            }
//        }
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
                if (nonSelectedTuples.contains(tuple)) {
                    System.out.println("G1:" + group1 + "\tG2:" + group2 + "\tBadTuple: " + wordListMap.get(tuple.get(0)) + ", " + wordListMap.get(tuple.get(1)));
                    return false;
                }
            }
        }
        return result;
    }

}
