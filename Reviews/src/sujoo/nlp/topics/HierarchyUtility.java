package sujoo.nlp.topics;

import java.text.DecimalFormat;
import java.util.List;

import sujoo.nlp.topics.datatypes.SynsetNode;
import sujoo.nlp.topics.datatypes.TermLLR;

import com.google.common.collect.Lists;

import edu.smu.tspell.wordnet.AdjectiveSatelliteSynset;
import edu.smu.tspell.wordnet.AdjectiveSynset;
import edu.smu.tspell.wordnet.AdverbSynset;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordSense;

public class HierarchyUtility {

    // ***********
    // Noun Hierarchy Construction
    // ***********
    public static List<SynsetNode> createNounHypernymHierarchy(SynsetNode node) {
        NounSynset nounSynset = (NounSynset) node.getSynset();
        // Stop recursion on root node
        if (nounSynset.getHypernyms().length == 0) {
            return Lists.newArrayList(node);
        } else {
            List<SynsetNode> hypernymNodes = createNounSynsetNodeList(Lists.newArrayList(nounSynset.getHypernyms()));
            List<SynsetNode> parentPaths = Lists.newArrayList();
            for (SynsetNode parentNode : hypernymNodes) {
                node.addParent(parentNode);
                parentNode.addChild(node);

                parentPaths.addAll(createNounHypernymHierarchy(parentNode));
            }
            return parentPaths;
        }
    }

    public static List<SynsetNode> createNounHolonymHierarchy(SynsetNode node) {
        NounSynset nounSynset = (NounSynset) node.getSynset();
        // Stop recursion on root node
        if (nounSynset.getMemberHolonyms().length == 0 && nounSynset.getPartHolonyms().length == 0 && nounSynset.getSubstanceHolonyms().length == 0) {
            return Lists.newArrayList(node);
        } else {
            List<SynsetNode> parentNodes = createNounSynsetNodeList(Lists.newArrayList(nounSynset.getMemberHolonyms()));
            parentNodes.addAll(createNounSynsetNodeList(Lists.newArrayList(nounSynset.getPartHolonyms())));
            parentNodes.addAll(createNounSynsetNodeList(Lists.newArrayList(nounSynset.getSubstanceHolonyms())));

            List<SynsetNode> parentPaths = Lists.newArrayList();
            for (SynsetNode parentNode : parentNodes) {
                node.addParent(parentNode);
                parentNode.addChild(node);

                parentPaths.addAll(createNounHolonymHierarchy(parentNode));
            }
            return parentPaths;
        }
    }

    private static List<SynsetNode> createNounSynsetNodeList(List<NounSynset> synsets) {
        List<SynsetNode> list = Lists.newArrayList();
        for (Synset synset : synsets) {
            SynsetNode node = new SynsetNode(synset);
            list.add(node);
        }
        return list;
    }

    // ***********
    // Verb Hierarchy Construction
    // ***********
    public static List<SynsetNode> createVerbHypernymHierarchy(SynsetNode node) {
        VerbSynset verbSynset = (VerbSynset) node.getSynset();
        // Stop recursion on root node
        if (verbSynset.getHypernyms().length == 0) {
            return Lists.newArrayList(node);
        } else {
            List<SynsetNode> hypernymNodes = createVerbSynsetNodeList(Lists.newArrayList(verbSynset.getHypernyms()));
            List<SynsetNode> parentPaths = Lists.newArrayList();
            for (SynsetNode parentNode : hypernymNodes) {
                node.addParent(parentNode);
                parentNode.addChild(node);

                parentPaths.addAll(createVerbHypernymHierarchy(parentNode));
            }
            return parentPaths;
        }
    }

    private static List<SynsetNode> createVerbSynsetNodeList(List<VerbSynset> synsets) {
        List<SynsetNode> list = Lists.newArrayList();
        for (Synset synset : synsets) {
            SynsetNode node = new SynsetNode(synset);
            list.add(node);
        }
        return list;
    }

    // ***********
    // Adjective Hierarchy Construction
    // ***********
    public static List<SynsetNode> createAdjectiveHierarchy(SynsetNode node) {

        AdjectiveSynset adjectiveSynset = (AdjectiveSynset) node.getSynset();
        // Stop recursion on root node
        if (adjectiveSynset.isHeadSynset()) {
            return Lists.newArrayList(node);
        } else {
            AdjectiveSatelliteSynset adjectiveSatelliteSynset = (AdjectiveSatelliteSynset) node.getSynset();
            SynsetNode headSynset = new SynsetNode(adjectiveSatelliteSynset.getHeadSynset());

            node.addParent(headSynset);
            headSynset.addChild(node);

            return Lists.newArrayList(headSynset);
        }
    }

    public static List<SynsetNode> createNounHierarchiesForAdjectives(SynsetNode node) {
        List<SynsetNode> results = Lists.newArrayList();

        AdjectiveSynset adjectiveSynset = (AdjectiveSynset) node.getSynset();
        List<NounSynset> nounAttributes = Lists.newArrayList(adjectiveSynset.getAttributes());
        for (NounSynset synset : nounAttributes) {
            SynsetNode nounNode = new SynsetNode(synset);
            nounNode.addChild(node);
            node.addParent(nounNode);
            results.addAll(createNounHypernymHierarchy(nounNode));
        }
        return results;
    }

    // ***********
    // Adverb Hierarchy Construction
    // ***********
    public static List<SynsetNode> createAdverbHierarchy(SynsetNode node) {

        AdverbSynset adverbSynset = (AdverbSynset) node.getSynset();
        // Stop recursion on root node
        if (!hasPertainyms(adverbSynset)) {
            return Lists.newArrayList();
        } else {
            List<SynsetNode> resultList = Lists.newArrayList();
            List<String> wordForms = Lists.newArrayList(node.getSynset().getWordForms());
            List<Synset> synsetsAdded = Lists.newArrayList();

            for (String wordForm : wordForms) {
                if (adverbSynset.getPertainyms(wordForm).length > 0) {
                    List<WordSense> senses = Lists.newArrayList(adverbSynset.getPertainyms(wordForm));
                    for (WordSense sense : senses) {
                        Synset synset = sense.getSynset();
                        if (!synsetsAdded.contains(synset)) {
                            synsetsAdded.add(synset);
                            SynsetNode adjectivePertainymNode = new SynsetNode(synset);
                            node.addParent(adjectivePertainymNode);
                            adjectivePertainymNode.addChild(node);
                            resultList.addAll(createAdjectiveHierarchy(adjectivePertainymNode));
                        }
                    }
                }
            }
            return resultList;
        }
    }

    private static boolean hasPertainyms(AdverbSynset synset) {
        boolean result = false;
        List<String> wordForms = Lists.newArrayList(synset.getWordForms());

        for (String wordForm : wordForms) {
            if (synset.getPertainyms(wordForm).length > 0) {
                result = true;
            }
        }
        return result;
    }

    // ***********
    // Merge Hierarchies
    // ***********
    public static void mergeHierarchies(List<SynsetNode> rootHierarchyNodes) {
        for (SynsetNode node1 : rootHierarchyNodes) {
            for (SynsetNode node2 : rootHierarchyNodes) {
                if (!node1.equals(node2) && node1.getSynset().equals(node2.getSynset())) {
                    mergeChildren(node1.getChildren(), node2.getChildren(), node1, node2);
                    mergeKeyTerms(node1, node2);
                }
            }
        }

        List<SynsetNode> nodesToRemove = Lists.newArrayList();
        for (SynsetNode node1 : rootHierarchyNodes) {
            if (hasSynsetMatch(rootHierarchyNodes, node1) && !node1.hasChildren()) {
                nodesToRemove.add(node1);
            }
        }

        rootHierarchyNodes.removeAll(nodesToRemove);
    }

    public static void mergeHierarchies(List<SynsetNode> hierarchyNodes1, List<SynsetNode> hierarchyNodes2) {
        for (SynsetNode node1 : hierarchyNodes1) {
            for (SynsetNode node2 : hierarchyNodes2) {
                if (node1.getSynset().equals(node2.getSynset())) {
                    mergeChildren(node1.getChildren(), node2.getChildren(), node1, node2);
                    mergeKeyTerms(node1, node2);
                }
            }
        }

        for (SynsetNode node : hierarchyNodes2) {
            if (!hasSynsetMatch(hierarchyNodes1, node) && node.hasChildren()) {
                hierarchyNodes1.add(node);
            }
        }
    }

    private static void mergeChildren(List<SynsetNode> list1, List<SynsetNode> list2, SynsetNode previousParent1, SynsetNode previousParent2) {
        List<SynsetNode> childrenToRemove = Lists.newArrayList();
        for (SynsetNode node2 : list2) {
            if (hasSynsetMatch(list1, node2)) {
                SynsetNode node1 = getSynsetMatch(list1, node2);
                mergeChildren(node1.getChildren(), node2.getChildren(), node1, node2);
                mergeKeyTerms(node1, node2);
            } else {
                node2.addParent(previousParent1);
                previousParent1.addChild(node2);
            }
            childrenToRemove.add(node2);
        }

        for (SynsetNode child : childrenToRemove) {
            child.removeParent(previousParent2);
            previousParent2.removeChild(child);
        }
    }

    private static boolean hasSynsetMatch(List<SynsetNode> list, SynsetNode node) {
        boolean match = false;
        for (SynsetNode potentialMatch : list) {
            if (potentialMatch.getSynset().equals(node.getSynset()) && !potentialMatch.equals(node)) {
                match = true;
            }
        }
        return match;
    }

    private static SynsetNode getSynsetMatch(List<SynsetNode> list, SynsetNode node) {
        for (SynsetNode potentialMatch : list) {
            if (potentialMatch.getSynset().equals(node.getSynset()) && !potentialMatch.equals(node)) {
                return potentialMatch;
            }
        }
        return null;
    }

    private static void mergeKeyTerms(SynsetNode node1, SynsetNode node2) {
        for (TermLLR termLLR : node2.getKeyTerms()) {
            if (!node1.getKeyTerms().contains(termLLR)) {
                node1.addKeyTerm(termLLR);
            }
        }
    }

    // ***********
    // Hierarchy String Outputs
    // ***********
    public static String hierarchyToString(SynsetNode rootNode) {
        return rootNode.getKeyTermHierarchyWords().toString() + "\n" + getHierarchyToString(rootNode, "");
    }
    
    public static String hierarchyToTopicList(SynsetNode rootNode) {
        String wordList = "";
        for (String word : rootNode.getKeyTermHierarchyWords()) {
            wordList += word + ",";
        }
        return wordList.substring(0, wordList.length() - 1);
    }

    private static String getHierarchyToString(SynsetNode node, String frontBit) {
        DecimalFormat df = new DecimalFormat("#.00");
        df.setGroupingUsed(true);
        df.setGroupingSize(3);
        String result = frontBit + getSynsetFormattedTerms(node) + "[" + df.format(node.getLLR()) + "]" + "[" + df.format(node.getHierarchyLLR())
                + "]" + " : " + node.getDefinition() + "\n";
        if (node.hasChildren()) {
            for (SynsetNode child : node.getChildren()) {
                result += getHierarchyToString(child, frontBit + "---*");
            }
        }
        return result;
    }

    private static String getSynsetFormattedTerms(SynsetNode node) {
        String result = "[";
        for (String term : Lists.newArrayList(node.getSynset().getWordForms())) {
            if (node.hasKeyTerms()) {
                for (String keyTerm : node.getKeyTermWords()) {
                    if (term.equals(keyTerm)) {
                        result += "#" + term + "#, ";
                    } else {
                        result += term + ", ";
                    }
                }
            } else {
                result += term + ", ";

            }
        }
        return result.substring(0, result.length() - 2) + "]";
    }
}
