/**
 * Implements the Log Likelihood Ratio formula
 */
package sujoo.nlp.llr;

import java.util.Comparator;
import java.util.Map;

import sujoo.nlp.stanford.StanfordNLP;

import com.google.common.collect.TreeMultiset;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

public class LogLikelihoodRatio {
    private Multiset<String> foreground;
    private Multiset<String> background;
    private StanfordNLP nlp;
    private double confidenceLevel;
    private Map<String, Double> sortedForegroundLLRMap;
    private Map<String, Double> sortedBackgroundLLRMap;

    public LogLikelihoodRatio(double confidenceLevel) {
        foreground = TreeMultiset.create();
        background = TreeMultiset.create();
        nlp = StanfordNLP.createLemmaTagger();
        this.confidenceLevel = confidenceLevel;
    }

    public void addForegroundText(String text) {
        foreground.addAll(nlp.getLemmas(text));
    }

    public void addBackgroundText(String text) {
        background.addAll(nlp.getLemmas(text));
    }

    public void calculateLLR() {
        // n1 = total count of all terms in foreground
        // n2 = total count of all terms in background
        // k1 = count of term in foreground
        // k2 = count of term in background
        // p1 = k1/n1
        // p2 = k2/n2
        // p = (k1 + k2) / (n1 + n2)

        // -2LLR = 2 * (logL(p1,k1,n1) + logL(p2,k2,n2)
        // - logL(p,k1,n1) - logL(p,k2,n2))
        // L(p,k,n) = p^k * (1-p)^(n-k)
        // log ( p^k * (1-p)^(n-k) )
        // = log(p^k) + log( (1-p)^(n-k) )
        // = k*log(p) + (n-k)*log(1-p)

        double n1 = foreground.size();
        double n2 = background.size();

        Map<String, Double> forgroundLLRMap = Maps.newHashMap();
        ValueComparator v1 = new ValueComparator(forgroundLLRMap);
        sortedForegroundLLRMap = Maps.newTreeMap(v1);

        Map<String, Double> backgroundLLRMap = Maps.newHashMap();
        ValueComparator v2 = new ValueComparator(backgroundLLRMap);
        sortedBackgroundLLRMap = Maps.newTreeMap(v2);

        for (String term : foreground.elementSet()) {
            double k1 = foreground.count(term);
            double k2 = background.count(term);

            double p1 = k1 / n1;
            double p2 = (k2 / n2) + Double.MIN_VALUE;
            double p = (k1 + k2) / (n1 + n2);

            // logL(p1,k1,n1)
            double p1k1n1 = k1 * Math.log(p1) + (n1 - k1) * Math.log(1 - p1);

            // logL(p2,k2,n2)
            double p2k2n2 = k2 * Math.log(p2) + (n2 - k2) * Math.log(1 - p2);

            // logL(p,k1,n1)
            double pk1n1 = k1 * Math.log(p) + (n1 - k1) * Math.log(1 - p);

            // logL(p,k2,n2)
            double pk2n2 = k2 * Math.log(p) + (n2 - k2) * Math.log(1 - p);

            double llr = 2 * (p1k1n1 + p2k2n2 - pk1n1 - pk2n2);

            if (llr >= confidenceLevel) {
                if (p1 > p2) {
                    forgroundLLRMap.put(term, llr);
                } else {
                    backgroundLLRMap.put(term, llr);
                }
            }
        }

        sortedForegroundLLRMap.putAll(forgroundLLRMap);
        sortedBackgroundLLRMap.putAll(backgroundLLRMap);
    }

    public Multiset<String> getSortedForegroundSet() {
        return Multisets.copyHighestCountFirst(foreground);
    }

    public Multiset<String> getSortedBackgroundSet() {
        return Multisets.copyHighestCountFirst(background);
    }

    public Map<String, Double> getSortedForegroundLLRMap() {
        return sortedForegroundLLRMap;
    }

    public Map<String, Double> getSortedBackgroundLLRMap() {
        return sortedBackgroundLLRMap;
    }

    private class ValueComparator implements Comparator<String> {

        Map<String, Double> base;

        public ValueComparator(Map<String, Double> base) {
            this.base = base;
        }

        public int compare(String a, String b) {
            if (base.get(a) > base.get(b)) {
                return -1;
            } else if (base.get(a) < base.get(b)) {
                return 1;
            } else {
                return a.compareTo(b);
            }
        }
    }
}
