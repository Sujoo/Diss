package sujoo.test;

import java.util.Map;

import sujoo.nlp.llr.LogLikelihoodRatio;

import com.google.common.collect.TreeMultiset;
import com.google.common.collect.Multiset;

public class TopicTest {

    public static void main(String[] args) {
        Multiset<String> rel = TreeMultiset.create();
        Multiset<String> non = TreeMultiset.create();

        addTerm("fit", 100, rel);
        addTerm("notfit", 1000, rel);
        addTerm("notfit2", 2000, non);
        addTerm("notfit", 2000, non);

        System.out.println(rel);

        LogLikelihoodRatio topics = new LogLikelihoodRatio(10.83);
        topics.addForegroundText("fit");
        topics.addForegroundText("no no no no no no no no no no no no no");
        topics.addForegroundText("ha ha ha ha ha ha ha ha ha ha ha ha ha ha ha ha ha ha h a ha ha ha ha ha ha ha hah hah ha");
        topics.addBackgroundText("right wrong never back front side to side");
        topics.addBackgroundText("na na na na na na na na na na na na na na na na na na na na na na na na na na na na na");
        topics.addBackgroundText("no ha yes winner fun times with weapons, but never a bot, ok?");

        topics.calculateLLR();

        Map<String, Double> llrs = topics.getSortedForegroundLLRMap();

        System.out.println(llrs);
    }

    public static void addTerm(String term, int num, Multiset<String> set) {
        for (int i = 0; i < num; i++) {
            set.add(term);
        }
    }

}
