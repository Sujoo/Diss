package sujoo.test;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import sujoo.nlp.stanford.StanfordNLP;

public class NLPTest {

	public static void main(String[] args) {
		Multiset<String> lemmas = HashMultiset.create();
		
		StanfordNLP nlp = StanfordNLP.createLemmaTagger();
		lemmas.addAll(nlp.getLemmas("The quick brown fox jumped over the lazy dog."));
		lemmas.addAll(nlp.getLemmas("I was never able to seen what I had for the future of the world.  Can you believe it or not?"));
		
		System.out.println(lemmas);
	}

}
