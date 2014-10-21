package sujoo.nlp.stanford.datatypes;

public class StanfordNLPProperties {
	
	private boolean cleanXML;
	private boolean ssplit;
	private boolean pos;
	private boolean lemma;
	private boolean ner;
	private boolean regexner;
	private boolean truecase;
	private boolean parse;
	private boolean dcoref;
	
	public StanfordNLPProperties() {
		cleanXML = false;
		ssplit = false;
		pos = false;
		lemma = false;
		ner = false;
		regexner = false;
		truecase = false;
		parse = false;
		dcoref = false;
	}
	
	public StanfordNLPProperties cleanXML() {
		cleanXML = true;
		return this;
	}
	
	public StanfordNLPProperties ssplit() {
		ssplit = true;
		return this;
	}
	
	public StanfordNLPProperties pos() {
		pos = true;
		return this;
	}
	
	public StanfordNLPProperties lemma() {
		lemma = true;
		return this;
	}
	
	public StanfordNLPProperties ner() {
		ner = true;
		return this;
	}
	
	public StanfordNLPProperties regexner() {
		regexner = true;
		return this;
	}
	
	public StanfordNLPProperties truecase() {
		truecase = true;
		return this;
	}
	
	public StanfordNLPProperties parse() {
		parse = true;
		return this;
	}
	
	public StanfordNLPProperties dcoref() {
		dcoref = true;
		return this;
	}

	public boolean isCleanXML() {
		return cleanXML;
	}

	public boolean isSsplit() {
		return ssplit;
	}

	public boolean isPos() {
		return pos;
	}

	public boolean isLemma() {
		return lemma;
	}

	public boolean isNer() {
		return ner;
	}

	public boolean isRegexner() {
		return regexner;
	}

	public boolean isTruecase() {
		return truecase;
	}

	public boolean isParse() {
		return parse;
	}

	public boolean isDcoref() {
		return dcoref;
	}
}
