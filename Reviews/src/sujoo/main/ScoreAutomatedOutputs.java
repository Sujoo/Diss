package sujoo.main;

/**
 * Identify group for term
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import sujoo.util.GroupIdMeasure;
import sujoo.util.Measures;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.collect.TreeMultimap;

public class ScoreAutomatedOutputs {
    private BufferedReader wordListReader;
    private BufferedReader groupReader;
    private BufferedReader automatedGroupReader;

    private Map<Integer, String> wordListMap;
    private TreeMultimap<Integer, String> groupWordMap;
    private TreeMultimap<Integer, String> automatedGroupWordMap;
    private Set<String> allGroupWords;
    private Set<String> allAutoGroupWords;

    private final DecimalFormat df = new DecimalFormat("0.000");

    public static void main(String[] args) throws Exception {
        runApparel();
        runBook();
        runCamera();
    }

    public static void runApparel() throws Exception {
        String wordList = "ReferenceFiles\\ApparelWordList.csv";
        String groups = "ReferenceFiles\\ApparelGroups.csv";
        System.out.println("LDA Apparel:");
        runIt(new ScoreAutomatedOutputs(wordList, groups, "LDAOutput\\apparel_topics.csv"));
        System.out.println();

        System.out.println("LLR Apparel:");
        runIt(new ScoreAutomatedOutputs(wordList, groups, "LLROutput\\apparel_topics.csv"));
        System.out.println();

        System.out.println("NCRP Apparel:");
        runIt(new ScoreAutomatedOutputs(wordList, groups, "NCRPOutput\\apparel_topics.csv"));
        System.out.println();
    }

    public static void runBook() throws Exception {
        String wordList = "ReferenceFiles\\BookWordList.csv";
        String groups = "ReferenceFiles\\BookGroups.csv";
        System.out.println("LDA Book:");
        runIt(new ScoreAutomatedOutputs(wordList, groups, "LDAOutput\\book_topics.csv"));
        System.out.println();

        System.out.println("LLR Book:");
        runIt(new ScoreAutomatedOutputs(wordList, groups, "LLROutput\\book_topics.csv"));
        System.out.println();

        System.out.println("NCRP Book:");
        runIt(new ScoreAutomatedOutputs(wordList, groups, "NCRPOutput\\book_topics.csv"));
        System.out.println();
    }

    public static void runCamera() throws Exception {
        String wordList = "ReferenceFiles\\CameraWordList.csv";
        String groups = "ReferenceFiles\\CameraGroups.csv";
        System.out.println("LDA Camera:");
        runIt(new ScoreAutomatedOutputs(wordList, groups, "LDAOutput\\camera_topics.csv"));
        System.out.println();

        System.out.println("LLR Camera:");
        runIt(new ScoreAutomatedOutputs(wordList, groups, "LLROutput\\camera_topics.csv"));
        System.out.println();

        System.out.println("NCRP Camera:");
        runIt(new ScoreAutomatedOutputs(wordList, groups, "NCRPOutput\\camera_topics.csv"));
        System.out.println();
    }

    public static void runIt(ScoreAutomatedOutputs p) throws Exception {
        p.prepare();
        p.process();
    }

    public ScoreAutomatedOutputs(String inputWordListFile, String inputGroupFile, String inputAutomatedGroupFile) throws Exception {
        wordListReader = new BufferedReader(new FileReader(inputWordListFile));
        groupReader = new BufferedReader(new FileReader(inputGroupFile));
        automatedGroupReader = new BufferedReader(new FileReader(inputAutomatedGroupFile));

        wordListMap = Maps.newHashMap();
        groupWordMap = TreeMultimap.create();
        automatedGroupWordMap = TreeMultimap.create();
        allGroupWords = Sets.newTreeSet();
        allAutoGroupWords = Sets.newTreeSet();
    }

    public void prepare() throws Exception {
        String currentLine = null;
        currentLine = wordListReader.readLine();
        while ((currentLine = wordListReader.readLine()) != null) {
            // id phrase reviewIds Sample Sentence Fragments
            String[] fields = currentLine.split("\t");
            int id = Integer.parseInt(fields[0]);
            String phrase = fields[1];
            // String reviewIds = fields[2];
            // String frags = fields[3];

            wordListMap.put(id, phrase);
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
                String wordPhrase = wordListMap.get(Integer.parseInt(wordIds[i]));
                String[] words = wordPhrase.split(" ");
                for (int j = 0; j < words.length; j++) {
                    groupWordMap.put(id, words[j]);
                    allGroupWords.add(words[j]);
                }
            }
        }
        groupReader.close();

        currentLine = null;
        int groupCounter = 1;
        while ((currentLine = automatedGroupReader.readLine()) != null) {
            // GroupId Words
            String[] words = currentLine.split(",");
            for (int i = 0; i < words.length; i++) {
                automatedGroupWordMap.put(groupCounter, words[i].toLowerCase());
                allAutoGroupWords.add(words[i].toLowerCase());
            }
            groupCounter++;
        }
        automatedGroupReader.close();
    }

    public void process() {
        Map<Integer, List<GroupIdMeasure>> autoGroupList = Maps.newHashMap();

        Measures overallScore = calculateOverallScore();
        for (int autoId : automatedGroupWordMap.keySet()) {
            List<GroupIdMeasure> list = Lists.newArrayList();
            for (int groupId : groupWordMap.keySet()) {
                Measures measures = calculateScore(automatedGroupWordMap.get(autoId), groupWordMap.get(groupId));
                list.add(new GroupIdMeasure(groupId, measures));
            }

            Collections.sort(list);
            autoGroupList.put(autoId, list);
        }

        while (hasConflict(autoGroupList)) {
            optimizeScores(autoGroupList);
        }

        // Result
        System.out.println("Overall Word Pool Score");
        System.out.println(overallScore);

        System.out.println();
        System.out.println("Average Group Score");
        double precisionSum = 0;
        double recallSum = 0;
        double fscoreSum = 0;
        double total = 0;
        for (int autoId1 : autoGroupList.keySet()) {
            List<GroupIdMeasure> list = autoGroupList.get(autoId1);
            Measures measures = list.get(0).getMeasures();
            precisionSum += measures.getPrecision();
            recallSum += measures.getRecall();
            fscoreSum += measures.getFscore();
            total++;
        }
        System.out.println("Precision: " + df.format(precisionSum / total));
        System.out.println("Recall   : " + df.format(recallSum / total));
        System.out.println("FScore   : " + df.format(fscoreSum / total));
    }

    private void optimizeScores(Map<Integer, List<GroupIdMeasure>> autoGroupList) {
        for (int autoId1 : autoGroupList.keySet()) {
            List<GroupIdMeasure> list1 = autoGroupList.get(autoId1);
            if (list1.get(0).getMeasures().getFscore() != 0.0) {
                List<Integer> conflictingGroups = Lists.newArrayList();
                conflictingGroups.add(autoId1);
                for (int autoId2 : autoGroupList.keySet()) {
                    if (autoId1 != autoId2) {
                        List<GroupIdMeasure> list2 = autoGroupList.get(autoId2);
                        if (list1.get(0).getGroupId() == list2.get(0).getGroupId()) {
                            conflictingGroups.add(autoId2);
                        }
                    }
                }

                if (conflictingGroups.size() > 1) {
                    double highestScoreDiff = -1;
                    int highestDiffGroupId = -1;
                    for (int id : conflictingGroups) {
                        List<GroupIdMeasure> list = autoGroupList.get(id);
                        double scoreDiff;
                        if (list.size() > 1) {
                            scoreDiff = list.get(0).getMeasures().getFscore() - list.get(1).getMeasures().getFscore();
                        } else {
                            scoreDiff = 0;
                        }
                        if (scoreDiff > highestScoreDiff) {
                            highestScoreDiff = scoreDiff;
                            highestDiffGroupId = id;
                        }
                    }

                    if (highestDiffGroupId == -1) {
                        System.out.println("wtf");
                    } else {
                        for (int id : conflictingGroups) {
                            if (id != highestDiffGroupId) {
                                List<GroupIdMeasure> list = autoGroupList.get(id);
                                if (list.size() > 1) {
                                    list.remove(0);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean hasConflict(Map<Integer, List<GroupIdMeasure>> autoGroupList) {
        boolean result = false;

        for (int autoId1 : autoGroupList.keySet()) {
            List<GroupIdMeasure> list1 = autoGroupList.get(autoId1);
            if (list1.get(0).getMeasures().getFscore() != 0.0) {
                for (int autoId2 : autoGroupList.keySet()) {
                    if (autoId1 != autoId2) {
                        List<GroupIdMeasure> list2 = autoGroupList.get(autoId2);
                        if (list1.get(0).getGroupId() == list2.get(0).getGroupId()) {
                            if (list1.size() == 1 || list2.size() == 1) {

                            } else {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    private Measures calculateOverallScore() {
        return calculateScore(allAutoGroupWords, allGroupWords);
    }

    private Measures calculateScore(Collection<String> autoGroups, Collection<String> groups) {
        double tp = 0;
        double fp = 0;
        for (String autoWord : autoGroups) {
            if (groups.contains(autoWord)) {
                tp++;
            } else {
                fp++;
            }
        }
        double fn = groups.size() - tp;
        double precision = tp / (tp + fp);
        double recall = tp / (tp + fn);
        double fscore = 0.0;
        if (precision != 0.0 || recall != 0.0) {
            fscore = 2 * ((precision * recall) / (precision + recall));
        }

        return new Measures(precision, recall, fscore);
    }
}
