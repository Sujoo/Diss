package sujoo.simulation;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;
import com.google.common.collect.Sets;

public class RandomClusterSim {
    
    public static final DecimalFormat df = new DecimalFormat("0.##");

    public static void main(String[] args) {
        int wordsPer = 12;
        int simCount = 1000;
        int words = 50;
        int clusters = 20;
        int[] numberOfGroupsList = new int[]{20,10,5};
        for (int i = 1; i < words+1; i++) {
            System.out.print(i + ",");
        }
        System.out.println();
        // # words, # groups, # clusters, words per cluster
        for (int i = 0; i < numberOfGroupsList.length; i++) {            
            multiSimulations(new RandomClusterSim(words, numberOfGroupsList[i], clusters, wordsPer), false, simCount);
        }
    }

    public static void multiSimulations(RandomClusterSim sim, boolean even, int simulationCount) {
        System.out.print(sim.numberOfWords + "," + sim.numberOfGroups + "," + sim.numberOfClusters + "," + sim.wordsPerCluster + ",");
        int correctCounter = 0;
        for (int i = 0; i < simulationCount; i++) {
            if (simulate(sim, even)) {
                correctCounter++;
            }
        }

        System.out.println(df.format(((double) correctCounter / (double) simulationCount) * 100));
    }

    public static boolean simulate(RandomClusterSim sim, boolean even) {
        sim.init();
        if (even) {
            sim.createEvenGroups();
        } else {
            sim.createUnevenGroups();
        }
        sim.createClusters();
        return sim.checkClusters();
    }

    private int[] words;
    private int[][] clusters;
    private Random random;

    private int numberOfWords;
    private int numberOfGroups;
    private int numberOfClusters;
    private int wordsPerCluster;

    public RandomClusterSim(int numberOfWords, int numberOfGroups, int numberOfClusters, int wordsPerCluster) {
        this.numberOfWords = numberOfWords;
        this.numberOfGroups = numberOfGroups;
        this.numberOfClusters = numberOfClusters;
        this.wordsPerCluster = wordsPerCluster;
    }

    public void init() {
        words = new int[numberOfWords];
        clusters = new int[numberOfClusters][wordsPerCluster];
        random = new Random();
    }

    public void createEvenGroups() {
        int wordsPerGroup = words.length / numberOfGroups;

        for (int i = 1; i <= numberOfGroups; i++) {
            int wordsPlaced = 0;
            while (wordsPlaced < wordsPerGroup) {
                int wordIndex = random.nextInt(words.length);
                if (words[wordIndex] == 0 && words[wordIndex] != i) {
                    words[wordIndex] = i;
                    wordsPlaced++;
                }
            }
        }

        for (int i = 0; i < words.length; i++) {
            if (words[i] == 0) {
                words[i] = numberOfGroups;
            }
        }

        // printArray(words);
    }

    public void createUnevenGroups() {
        int[] groupSizes = new int[numberOfGroups - 1];
        int nextRandomMin = 2;
        for (int i = 0; i < numberOfGroups - 1; i++) {
            int bit = (numberOfGroups - (i + 1)) * 2;
            int nextRandomMax = numberOfWords - bit;
            int nextIntThing = (nextRandomMax - nextRandomMin) + 1;
            int groupIndex = random.nextInt(nextIntThing) + nextRandomMin;
            nextRandomMin = groupIndex + 2;
            groupSizes[i] = groupIndex;
        }

        int prev = 0;
        for (int i = 0; i < groupSizes.length; i++) {
            int wordsInGroup = groupSizes[i] - prev;
            int wordsPlaced = 0;
            while (wordsPlaced < wordsInGroup) {
                int wordIndex = random.nextInt(words.length);
                if (words[wordIndex] == 0 && words[wordIndex] != i + 1) {
                    words[wordIndex] = i + 1;
                    wordsPlaced++;
                }
            }
            prev = groupSizes[i];
        }
        int wordsInGroup = numberOfWords - prev;
        int wordsPlaced = 0;
        while (wordsPlaced < wordsInGroup) {
            int wordIndex = random.nextInt(words.length);
            if (words[wordIndex] == 0 && words[wordIndex] != numberOfGroups) {
                words[wordIndex] = numberOfGroups;
                wordsPlaced++;
            }
        }

        // printArray(groupSizes);
        // printArray(words);
    }

    public void createClusters() {
        for (int i = 0; i < clusters.length; i++) {
            int[] check = new int[words.length];
            for (int j = 0; j < clusters[i].length; j++) {
                boolean uniqueWordSelected = false;
                while (!uniqueWordSelected) {
                    int wordIndex = random.nextInt(words.length);
                    if (check[wordIndex] == 0) {
                        check[wordIndex] = 1;
                        uniqueWordSelected = true;
                        clusters[i][j] = wordIndex;
                    }
                }
            }
        }

        // printDoubleArray(clusters);
    }

    public boolean checkClusters() {
        Set<List<Integer>> selectedTuples = Sets.newHashSet();
        Set<List<Integer>> nonSelectedTuples = Sets.newHashSet();

        for (int i = 0; i < clusters.length; i++) {
            List<Integer> wordsInCluster = Lists.newArrayList();
            Set<List<Integer>> allTuples = Sets.newHashSet();
            // Count groups and determine words in cluster
            Multiset<Integer> groupCounter = HashMultiset.create();
            for (int j = 0; j < clusters[i].length; j++) {
                groupCounter.add(words[clusters[i][j]]);
                wordsInCluster.add(clusters[i][j]);
            }

            // Create all possible not-tuples
            for (int word1 : wordsInCluster) {
                for (int word2 : wordsInCluster) {
                    if (word1 != word2) {
                        List<Integer> tuple = Lists.newArrayList();
                        tuple.add(word1);
                        tuple.add(word2);
                        Collections.sort(tuple);
                        allTuples.add(tuple);
                    }
                }
            }

            // Create list of selected words from largest group
            List<Integer> largestGroups = Lists.newArrayList();
            for (int large : Multisets.copyHighestCountFirst(groupCounter).elementSet()) {
                if (largestGroups.isEmpty()) {
                    largestGroups.add(large);
                } else if (groupCounter.count(largestGroups.get(0)) == groupCounter.count(large)) {
                    largestGroups.add(large);
                }
            }
            int largestGroup = largestGroups.get(random.nextInt(largestGroups.size()));
            // System.out.println("Cluster " + i + ": Group " + largestGroup);
            List<Integer> selectedWords = Lists.newArrayList();
            if (groupCounter.count(largestGroup) > 1) {
                for (int j = 0; j < clusters[i].length; j++) {
                    if (words[clusters[i][j]] == largestGroup) {
                        selectedWords.add(clusters[i][j]);
                        // System.out.print(clusters[i][j] + " ");
                    }
                }
            }
            // System.out.println();

            // Create selected tuples
            for (int word1 : selectedWords) {
                for (int word2 : selectedWords) {
                    if (word1 != word2) {
                        List<Integer> tuple = Lists.newArrayList();
                        tuple.add(word1);
                        tuple.add(word2);
                        Collections.sort(tuple);
                        selectedTuples.add(tuple);
                        allTuples.remove(tuple);
                    }
                }
            }

            for (List<Integer> tuple : allTuples) {
                if (selectedWords.contains(tuple.get(0)) || selectedWords.contains(tuple.get(1))) {
                    nonSelectedTuples.add(tuple);
                }
            }
        }

        // for (List<Integer> tuple : selectedTuples) {
        // nonSelectedTuples.remove(tuple);
        // }

        List<Set<Integer>> groups = Lists.newArrayList();
        for (List<Integer> selectedTuple : selectedTuples) {
            if (!nonSelectedTuples.contains(selectedTuple)) {
                addSetToList(Sets.newHashSet(selectedTuple), groups, nonSelectedTuples);
            }
        }

        // System.out.println("True Groups:");
        // printTrueGroups();
        //
        // System.out.println("Groups Found:");
        // System.out.println(groups);

        boolean[] partOfGroupFound = new boolean[numberOfGroups];
        for (Set<Integer> group : groups) {
            int adjustedGroupId = words[group.iterator().next()] - 1;
            if (!partOfGroupFound[adjustedGroupId]) {
                partOfGroupFound[adjustedGroupId] = true;
            } else {
                return false;
            }
        }

        return true;
    }

    private void addSetToList(Set<Integer> newGroup, List<Set<Integer>> groups, Set<List<Integer>> nonSelectedTuples) {
        for (Set<Integer> group : groups) {
            for (Integer i : newGroup) {
                if (group.contains(i) && canGroupsBeCombined(newGroup, group, nonSelectedTuples)) {
                    groups.remove(group);
                    group.addAll(newGroup);
                    addSetToList(group, groups, nonSelectedTuples);
                    return;
                }
            }
        }

        groups.add(newGroup);
    }

    private boolean canGroupsBeCombined(Set<Integer> group1, Set<Integer> group2, Set<List<Integer>> nonSelectedTuples) {
        boolean result = true;
        for (Integer i : group1) {
            for (Integer j : group2) {
                List<Integer> tuple = Lists.newArrayList();
                tuple.add(i);
                tuple.add(j);
                Collections.sort(tuple);
                if (nonSelectedTuples.contains(tuple)) {
                    System.out.println("G1:" + group1 + "\tG2:" + group2 + "\tBadTuple:" + tuple);
                    return false;
                }
            }
        }
        return result;
    }

    private void printDoubleArray(int[][] doubleArray) {
        for (int i = 0; i < doubleArray.length; i++) {
            printArray(doubleArray[i]);
        }
    }

    private void printArray(int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (i < array.length - 1) {
                System.out.print(array[i] + ",");
            } else {
                System.out.print(array[i]);
            }
        }
        System.out.println();
    }

    private void printTrueGroups() {
        List<Set<Integer>> trueGroups = Lists.newArrayList();
        for (int i = 0; i < numberOfGroups; i++) {
            Set<Integer> newGroup = Sets.newHashSet();
            trueGroups.add(newGroup);
        }

        for (int i = 0; i < words.length; i++) {
            int adjustedGroupId = words[i] - 1;
            trueGroups.get(adjustedGroupId).add(i);
        }

        System.out.println(trueGroups);
    }
}
