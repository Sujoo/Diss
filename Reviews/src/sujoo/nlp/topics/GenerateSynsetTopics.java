package sujoo.nlp.topics;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.smu.tspell.wordnet.Synset;
import sujoo.nlp.topics.datatypes.SynsetNode;
import sujoo.nlp.topics.datatypes.TermLLR;
import sujoo.nlp.wordnet.QueryWordNet;
import sujoo.util.FileReaderUtil;
import sujoo.util.Timer;

public class GenerateSynsetTopics {
    private QueryWordNet query;
    private List<String> termsNotFoundInWordNetList;
    private List<SynsetNode> nounSynsetNodes;
    private List<SynsetNode> verbSynsetNodes;
    private List<SynsetNode> adjectiveSynsetNodes;
    private List<SynsetNode> adverbSynsetNodes;
    private List<SynsetNode> nounHypernymHierarchyRootSynsets;
    private List<SynsetNode> nounHolonymHierarchyRootSynsets;
    private List<SynsetNode> verbHierarchyRootSynsets;
    private List<SynsetNode> adjectiveHierarchyRootSynsets;
    private List<SynsetNode> adverbHierarchyRootSynsets;
    private List<TermLLR> nounTerms;
    private List<TermLLR> verbTerms;
    private List<TermLLR> adjTerms;
    private String fileEnding;

    public static void main(String[] args) {
        GenerateSynsetTopics topics = new GenerateSynsetTopics();
        Timer timer = new Timer();
        try {
            timer.start();
            topics.generateTopics();
            timer.stop();
            timer.print();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public GenerateSynsetTopics() {
        query = new QueryWordNet();
        termsNotFoundInWordNetList = Lists.newArrayList();
        nounSynsetNodes = Lists.newArrayList();
        verbSynsetNodes = Lists.newArrayList();
        adjectiveSynsetNodes = Lists.newArrayList();
        adverbSynsetNodes = Lists.newArrayList();

        nounHypernymHierarchyRootSynsets = Lists.newArrayList();
        nounHolonymHierarchyRootSynsets = Lists.newArrayList();
        verbHierarchyRootSynsets = Lists.newArrayList();
        adjectiveHierarchyRootSynsets = Lists.newArrayList();
        adverbHierarchyRootSynsets = Lists.newArrayList();

        nounTerms = Lists.newArrayList();
        verbTerms = Lists.newArrayList();
        adjTerms = Lists.newArrayList();
    }

    public void generateTopics() throws FileNotFoundException, IOException {
        querySynsets();
        constructHierarchies();
        mergeHierarchies();
        pruneHierarchies();
        printHierarchies();
        //printResults(fileEnding);
    }

    public void querySynsets() throws FileNotFoundException, IOException {
        FileReaderUtil lexemLLRReader = new FileReaderUtil();
        fileEnding = lexemLLRReader.getFileName();
        // skip first line, it is a header
        lexemLLRReader.skipLine();

        while (lexemLLRReader.hasNext()) {
            String[] line = lexemLLRReader.next();
            String lexem = line[0];
            double llr = Math.log(Double.parseDouble(line[1]));

            String term = lexem.substring(0, lexem.length() - 2);
            TermLLR termLLR = new TermLLR(term, llr);

            if (lexem.endsWith("_N")) {
                mergeSynsets(query.getNounSynsets(term), nounSynsetNodes, termLLR);
                nounTerms.add(termLLR);
            } else if (lexem.endsWith("_V")) {
                mergeSynsets(query.getVerbSynsets(term), verbSynsetNodes, termLLR);
                verbTerms.add(termLLR);
            } else if (lexem.endsWith("_J")) {
                mergeSynsets(query.getAllAdjectiveSynsets(term), adjectiveSynsetNodes, termLLR);
                adjTerms.add(termLLR);
            } else if (lexem.endsWith("_D")) {
                mergeSynsets(query.getAdverbSynsets(term), adverbSynsetNodes, termLLR);
                adjTerms.add(termLLR);
            } else {
                // This should never happen
                // wordNetSynsets = Lists.newArrayList();
                // selectedSynset = Lists.newArrayList();
                // termList = Lists.newArrayList();
            }
            // if (wordNetSynsets.size() > 0) {
            // termList.add(termLLR);
            // } else {
            // termsNotFoundInWordNetList.add(lexem);
            // }
        }
        lexemLLRReader.close();
    }

    private void mergeSynsets(List<Synset> querySynsetList, List<SynsetNode> masterList, TermLLR termLLR) {
        boolean mergeFound = false;
        for (Synset synset : querySynsetList) {
            for (SynsetNode node : masterList) {
                if (synset.equals(node.getSynset())) {
                    node.addKeyTerm(termLLR);
                    mergeFound = true;
                    break;
                }
            }
            if (!mergeFound) {
                SynsetNode newNode = new SynsetNode(synset);
                newNode.addKeyTerm(termLLR);
                masterList.add(newNode);
            }
        }
    }

    public void constructHierarchies() {
        for (SynsetNode node : nounSynsetNodes) {
            nounHypernymHierarchyRootSynsets.addAll(HierarchyUtility.createNounHypernymHierarchy(node));
        }
        for (SynsetNode node : nounSynsetNodes) {
            nounHolonymHierarchyRootSynsets.addAll(HierarchyUtility.createNounHolonymHierarchy(node));
        }
        for (SynsetNode node : verbSynsetNodes) {
            verbHierarchyRootSynsets.addAll(HierarchyUtility.createVerbHypernymHierarchy(node));
        }
        for (SynsetNode node : adjectiveSynsetNodes) {
            adjectiveHierarchyRootSynsets.addAll(HierarchyUtility.createAdjectiveHierarchy(node));
        }
        for (SynsetNode node : adverbSynsetNodes) {
            adverbHierarchyRootSynsets.addAll(HierarchyUtility.createAdverbHierarchy(node));
        }
    }

    public void mergeHierarchies() throws IOException {
        HierarchyUtility.mergeHierarchies(nounHypernymHierarchyRootSynsets);
        HierarchyUtility.mergeHierarchies(nounHolonymHierarchyRootSynsets);
        HierarchyUtility.mergeHierarchies(verbHierarchyRootSynsets);
        HierarchyUtility.mergeHierarchies(adjectiveHierarchyRootSynsets);
        HierarchyUtility.mergeHierarchies(adverbHierarchyRootSynsets);        
        HierarchyUtility.mergeHierarchies(adjectiveHierarchyRootSynsets, adverbHierarchyRootSynsets);
        //disambiguate(adjectiveHierarchyRootSynsets);
        
        List<SynsetNode> adjWNouns = Lists.newArrayList();
        for (SynsetNode node : adjectiveHierarchyRootSynsets) {
            adjWNouns.addAll(HierarchyUtility.createNounHierarchiesForAdjectives(node));
        }
        adjectiveHierarchyRootSynsets = adjWNouns;
        HierarchyUtility.mergeHierarchies(nounHypernymHierarchyRootSynsets, adjectiveHierarchyRootSynsets);
        disambiguate(nounHypernymHierarchyRootSynsets);
        disambiguate(verbHierarchyRootSynsets);
    }
    
    public void disambiguate(List<SynsetNode> list) {
        Map<TermLLR,List<SynsetNode>> termLLRSynsetNodeMap = Maps.newHashMap();
        for (SynsetNode node : list) {
            addTermsToMap(termLLRSynsetNodeMap, node);
        }
        
        wordSenseDisambiguate(termLLRSynsetNodeMap, list);
    }
    
    private void addTermsToMap(Map<TermLLR,List<SynsetNode>> map, SynsetNode node) {
        for (TermLLR termLLR : node.getKeyTerms()) {
            if (!map.containsKey(termLLR)) {
                map.put(termLLR, Lists.newArrayList(node));
            } else {
                map.get(termLLR).add(node);
            }
        }
        
        if (node.hasChildren()) {
            for (SynsetNode child : node.getChildren()) {
                addTermsToMap(map, child);
            }
        }
    }
    
    private void wordSenseDisambiguate(Map<TermLLR,List<SynsetNode>> termLLRSynsetNodeMap, List<SynsetNode> rootNodes) {
        List<TermLLR> termLLRs = Lists.newArrayList(termLLRSynsetNodeMap.keySet());
        Collections.sort(termLLRs);
        for (TermLLR termLLR : termLLRs) {
            double totalNodeLLR = 0;
            for (SynsetNode node : termLLRSynsetNodeMap.get(termLLR)) {
                double nodeLLR = node.getLLROfChildrenAndSiblingHierarchies();
                totalNodeLLR += nodeLLR;
            }

            double threshold = totalNodeLLR * .3;
            for (SynsetNode node : termLLRSynsetNodeMap.get(termLLR)) {
                if (node.getLLROfChildrenAndSiblingHierarchies() < threshold) {
                    node.removeKeyTerm(termLLR);
                }
            }
        }
    }
    
    public void pruneHierarchies() throws IOException {
        for (SynsetNode node : nounHypernymHierarchyRootSynsets) {
            if (node.hasChildren()) {
                pruneDeadLeaves(node);
            }
        }
        
        nounHypernymHierarchyRootSynsets = pruneTopThreeTiers(nounHypernymHierarchyRootSynsets);
        
        for (SynsetNode node : nounHypernymHierarchyRootSynsets) {
            if (node.hasChildren()) {
                pruneSinglePaths(node);
            }
        }
    }
    
    private List<SynsetNode> pruneTopThreeTiers(List<SynsetNode> roots) {
        List<SynsetNode> result = Lists.newArrayList();
        for (SynsetNode node : nounHypernymHierarchyRootSynsets) {
            if (node.hasChildren()) {
                for (SynsetNode child1 : node.getChildren()) {
                    for (SynsetNode child2 : child1.getChildren()) {
                        for (SynsetNode child3 : child2.getChildren()) {
                            if (child3.hasChildren()) {
                                result.add(child3);
                            }
                        }
                    }
                }
            }
        }
        
        return result;
    }
    
    private boolean pruneDeadLeaves(SynsetNode node) {
        Iterator<SynsetNode> childIter = node.getChildren().iterator();
        while (childIter.hasNext()) {
            SynsetNode child = childIter.next();
            if (pruneDeadLeaves(child)) {
                child.removeParent(node);
                childIter.remove();
            }
        }
        
        // If you have no children, you are a leaf node
        if (!node.hasChildren() && !node.hasKeyTerms()) {
            return true;
        }
        return false;
    }
    
    private List<SynsetNode> pruneDeadRoots(List<SynsetNode> roots) {
        List<SynsetNode> result = Lists.newArrayList();
        for (SynsetNode root : roots) {
            if (root.hasChildren() || root.hasKeyTerms()) {
                result.add(root);
            }
        }
        return result;
    }
    
    private boolean pruneSinglePaths(SynsetNode node) {
        List<SynsetNode> grandchildrenToAdd = Lists.newArrayList();
        Iterator<SynsetNode> childIter = node.getChildren().iterator();
        while (childIter.hasNext()) {
            SynsetNode child = childIter.next();
            if (pruneSinglePaths(child)) {
                Iterator<SynsetNode> grandchildIter = child.getChildren().iterator();
                while (grandchildIter.hasNext()) {
                    SynsetNode grandchild = grandchildIter.next();
                    grandchild.removeParent(child);
                    grandchild.addParent(node);
                    grandchildrenToAdd.add(grandchild);
                }
                
                for (TermLLR termLLR : child.getKeyTerms()) {
                    if (!node.getKeyTerms().contains(termLLR)) {
                        node.addKeyTerm(termLLR);
                    }
                }
                child.removeParent(node);
                childIter.remove();
            }
        }
        
        node.addChildren(grandchildrenToAdd);
        
        //If this node has only one child, it can be removed and the children attached to it's parent
        if (node.getChildren().size() == 1) {
            return true;
        }
        return false;
    }
    
    public void printHierarchies() throws IOException {
        Collections.sort(nounHypernymHierarchyRootSynsets);
        Collections.reverse(nounHypernymHierarchyRootSynsets);
        printHierarchy(nounHypernymHierarchyRootSynsets, "C:\\Users\\mbcusick\\Documents\\Results\\test\\NounHyperHierarchies.txt");
        printHierarchy(nounHolonymHierarchyRootSynsets, "C:\\Users\\mbcusick\\Documents\\Results\\test\\NounHoloHierarchies.txt");
        printHierarchy(verbHierarchyRootSynsets, "C:\\Users\\mbcusick\\Documents\\Results\\test\\VerbHierarchies.txt");
        printHierarchy(adjectiveHierarchyRootSynsets, "C:\\Users\\mbcusick\\Documents\\Results\\test\\AdjectiveHierarchies.txt");
        printHierarchy(adverbHierarchyRootSynsets, "C:\\Users\\mbcusick\\Documents\\Results\\test\\AdverbHierarchies.txt");
        printTopicList(nounHypernymHierarchyRootSynsets, verbHierarchyRootSynsets, "C:\\Users\\mbcusick\\Documents\\Results\\test\\Topics.txt");
    }
    
    private void printTopicList(List<SynsetNode> list1, List<SynsetNode> list2, String filePathName) throws IOException {
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePathName)));
        for (SynsetNode rootNode : list1) {
            if (rootNode.getKeyTermHierarchyWords().size() >= 3) {
                writer.println(HierarchyUtility.hierarchyToTopicList(rootNode));
            }
        }
        for (SynsetNode rootNode : list2) {
            if (rootNode.getKeyTermHierarchyWords().size() >= 3) {
                writer.println(HierarchyUtility.hierarchyToTopicList(rootNode));
            }
        }
        writer.close();
    }
    
    private void printHierarchy(List<SynsetNode> list, String filePathName) throws IOException {
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(filePathName)));
        writer.println("Root Nodes: " + list.size());
        for (SynsetNode rootNode : list) {
            writer.println(HierarchyUtility.hierarchyToString(rootNode));
        }
        writer.close();
    }

    public void printResults(String fileEnding) throws IOException {
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\mbcusick\\Documents\\Results\\topics\\NounSynsets_"
                + fileEnding + ".txt")));
        writer.println("Synsets: " + nounHypernymHierarchyRootSynsets.size());
        for (SynsetNode s : nounHypernymHierarchyRootSynsets) {
            writer.println(s);
        }
        writer.close();

        writer = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\mbcusick\\Documents\\Results\\topics\\VerbSynsets_" + fileEnding
                + ".txt")));
        writer.println("Synsets: " + verbHierarchyRootSynsets.size());
        for (SynsetNode s : verbHierarchyRootSynsets) {
            writer.println(s);
        }
        writer.close();

        writer = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\mbcusick\\Documents\\Results\\topics\\AdjSynsets_" + fileEnding
                + ".txt")));
        writer.println("Synsets: " + adjectiveHierarchyRootSynsets.size());
        for (SynsetNode s : adjectiveHierarchyRootSynsets) {
            writer.println(s);
        }
        writer.close();

        writer = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\mbcusick\\Documents\\Results\\topics\\termsNotInWordNet_" + fileEnding
                + ".txt")));
        writer.println("Number of terms not in WordNet: " + termsNotFoundInWordNetList.size());
        writer.println(termsNotFoundInWordNetList);
        writer.close();
    }
}
