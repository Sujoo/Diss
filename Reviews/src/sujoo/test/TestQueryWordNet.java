package sujoo.test;

import java.util.List;

import sujoo.nlp.topics.HierarchyUtility;
import sujoo.nlp.topics.datatypes.SynsetNode;
import sujoo.nlp.topics.datatypes.TermLLR;
import sujoo.nlp.wordnet.QueryWordNet;

import com.google.common.collect.Lists;

import edu.smu.tspell.wordnet.AdjectiveSatelliteSynset;
import edu.smu.tspell.wordnet.AdjectiveSynset;
import edu.smu.tspell.wordnet.AdverbSynset;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.VerbSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.WordSense;

public class TestQueryWordNet {
    WordNetDatabase database;

    public static void main(String[] args) {
        TestQueryWordNet wordNet = new TestQueryWordNet();
        QueryWordNet test = new QueryWordNet();

        String term = "cozily";
        String type = "adv";
        TermLLR termLLR = new TermLLR(term, 11);
        if (type.equals("noun")) {
            wordNet.queryNounSynsets(term);

            List<Synset> list = test.getNounSynsets(term);
            System.out.println("Hypernyms:");
            for (Synset synset : list) {
                SynsetNode node = new SynsetNode(synset);
                node.addKeyTerm(termLLR);
                List<SynsetNode> nodes = HierarchyUtility.createNounHypernymHierarchy(node);
                for (SynsetNode root : nodes) {
                    System.out.println(HierarchyUtility.hierarchyToString(root));
                }
            }

            System.out.println("Holonyms");
            for (Synset synset : list) {
                SynsetNode node = new SynsetNode(synset);
                node.addKeyTerm(termLLR);
                List<SynsetNode> nodes = HierarchyUtility.createNounHolonymHierarchy(node);
                for (SynsetNode root : nodes) {
                    System.out.println(HierarchyUtility.hierarchyToString(root));
                }
            }
        } else if (type.equals("verb")) {
            wordNet.queryVerbSynsets(term);

            List<Synset> list = test.getVerbSynsets(term);
            for (Synset synset : list) {
                SynsetNode node = new SynsetNode(synset);
                node.addKeyTerm(termLLR);
                List<SynsetNode> nodes = HierarchyUtility.createVerbHypernymHierarchy(node);
                for (SynsetNode root : nodes) {
                    System.out.println(HierarchyUtility.hierarchyToString(root));
                }
            }
        } else if (type.equals("adj")) {
            wordNet.queryAdjectiveSynsets(term);

            List<Synset> list = test.getAllAdjectiveSynsets(term);
            for (Synset synset : list) {
                SynsetNode node = new SynsetNode(synset);
                node.addKeyTerm(termLLR);
                List<SynsetNode> nodes = HierarchyUtility.createAdjectiveHierarchy(node);
                for (SynsetNode root : nodes) {
                    System.out.println(HierarchyUtility.hierarchyToString(root));
                }
            }
        } else if (type.equals("adv")) {
            wordNet.queryAdverbSynsets(term);

            List<Synset> list = test.getAdverbSynsets(term);
            for (Synset synset : list) {
                SynsetNode node = new SynsetNode(synset);
                node.addKeyTerm(termLLR);
                List<SynsetNode> nodes = HierarchyUtility.createAdverbHierarchy(node);
                for (SynsetNode root : nodes) {
                    System.out.println(HierarchyUtility.hierarchyToString(root));
                }
            }
        }

    }

    public TestQueryWordNet() {
        System.setProperty("wordnet.database.dir", "C:\\Users\\mbcusick\\Documents\\WordNet\\dict\\");
        database = WordNetDatabase.getFileInstance();
    }

    public void queryNounSynsets(String term) {
        Synset[] synsets = database.getSynsets(term, SynsetType.NOUN);
        if (synsets.length > 0) {
            System.out.println("Query Term: " + term);
            for (int i = 0; i < synsets.length; i++) {
                NounSynset nounSynset = (NounSynset) synsets[i];
                System.out.println("Synset: " + Lists.newArrayList(nounSynset.getWordForms()) + " : " + nounSynset.getDefinition());

                printSynset(nounSynset.getHypernyms(), "Hypernyms");
                printSynset(nounSynset.getHyponyms(), "Hyponyms");
                printSynset(nounSynset.getMemberHolonyms(), "Member Holoynms");
                printSynset(nounSynset.getPartHolonyms(), "Part Holoynms");
                printSynset(nounSynset.getSubstanceHolonyms(), "Substance Holoynms");
                System.out.println();
            }
        } else {
            System.out.println("No synsets exist that contain the word form '" + term + "'");
        }
    }

    public void queryVerbSynsets(String term) {
        Synset[] synsets = database.getSynsets(term, SynsetType.VERB);
        if (synsets.length > 0) {
            System.out.println("Query Term: " + term);
            for (int i = 0; i < synsets.length; i++) {
                VerbSynset verbSynset = (VerbSynset) synsets[i];
                System.out.println("Synset: " + Lists.newArrayList(verbSynset.getWordForms()) + " : " + verbSynset.getDefinition());

                printSynset(verbSynset.getHypernyms(), "Hypernyms");
                printSynset(verbSynset.getTroponyms(), "Troponyms");
                printSynset(verbSynset.getEntailments(), "Entailments");
                printSynset(verbSynset.getOutcomes(), "Outcomes");
                System.out.println();
            }
        } else {
            System.out.println("No synsets exist that contain the word form '" + term + "'");
        }
    }

    public void queryAdjectiveSynsets(String term) {
        Synset[] synsets = database.getSynsets(term, SynsetType.ADJECTIVE);
        if (synsets.length > 0) {
            System.out.println("Query Term: " + term);
            for (int i = 0; i < synsets.length; i++) {
                AdjectiveSynset adjSynset = (AdjectiveSynset) synsets[i];
                System.out.println("Head Synset: " + Lists.newArrayList(adjSynset.getWordForms()) + " : " + adjSynset.getDefinition());
                System.out.println("Is Head Synset: " + adjSynset.isHeadSynset());

                printSynset(adjSynset.getSimilar(), "Children");
                printSynset(adjSynset.getAttributes(), "Noun Attributes");
                System.out.println();
            }
        } else {
            System.out.println("No synsets exist that contain the word form '" + term + "'");
        }

        Synset[] synsets2 = database.getSynsets(term, SynsetType.ADJECTIVE_SATELLITE);
        if (synsets2.length > 0) {
            System.out.println("Sat Query Term: " + term);
            for (int i = 0; i < synsets2.length; i++) {
                AdjectiveSatelliteSynset adjSatSynset = (AdjectiveSatelliteSynset) synsets2[i];
                System.out.println("Satellite Synset: " + Lists.newArrayList(adjSatSynset.getWordForms()) + " : " + adjSatSynset.getDefinition());

                AdjectiveSynset adjSynset = adjSatSynset.getHeadSynset();
                System.out.println("Parent Head Synset: " + Lists.newArrayList(adjSynset.getWordForms()) + " : " + adjSynset.getDefinition());
                printSynset(adjSynset.getAttributes(), "Head Noun Attributes");
                printSynset(adjSynset.getSimilar(), "Head Children");
                System.out.println();
            }
        } else {
            System.out.println("No synsets exist that contain the word form '" + term + "'");
        }
    }

    public void queryAdverbSynsets(String term) {
        Synset[] synsets = database.getSynsets(term, SynsetType.ADVERB);
        if (synsets.length > 0) {
            System.out.println("Query Term: " + term);
            for (int i = 0; i < synsets.length; i++) {
                AdverbSynset advSynset = (AdverbSynset) synsets[i];
                List<String> wordForms = Lists.newArrayList(advSynset.getWordForms());
                System.out.println("Synset: " + wordForms + " : " + advSynset.getDefinition());

                WordSense[] senses = advSynset.getPertainyms(term);
                if (senses.length > 0) {
                    System.out.println("Term Pertainym: " + term + " : " + Lists.newArrayList(senses[0].getSynset().getWordForms()));
                    // for (String word : wordForms) {
                    // WordSense sense = advSynset.getPertainyms(word)[0];
                    // System.out.println("Pertainym of " + word + ": " +
                    // sense.getWordForm() + " : "
                    // + Lists.newArrayList(sense.getSynset().getWordForms()));
                    // }
                } else {
                    System.out.println("Not associated with an Adjective");
                }

                System.out.println();
            }
        } else {
            System.out.println("No synsets exist that contain the word form '" + term + "'");
        }
    }

    private void printSynset(Synset[] synset, String type) {
        System.out.print(type + " : ");
        for (int i = 0; i < synset.length; i++) {
            System.out.print(Lists.newArrayList(synset[i].getWordForms()));
        }
        System.out.println();
    }

}
