package sujoo.nlp.stanford.datatypes;

public enum PartOfSpeech {
    // ** Open Words **
    //  - Nouns
    NOUN("NN"),
    PROPER_SINGULAR_NOUN("NNP"),
    PROPER_PLURAL_NOUN("NNPS"),
    PLURAL_NOUN("NNS"),
    //  - Verbs
    VERB("VB"),
    VERB_PAST("VBD"),
    VERB_PRESENT_PARTICIPLE("VBG"),
    VERB_PAST_PARTICIPLE("VBN"),
    VERB_NON3RD_SINGULAR_PRESENT("VBP"),
    VERB_3RD_SINGULAR_PRESENT("VBZ"),
    //  - Adjectives
    ADJECTIVE("JJ"),
    ADJECTIVE_COMPARATIVE("JJR"),
    ADJECTIVE_SUPERLATIVE("JJS"),
    //  - Adverbs
    ADVERB("RB"),
    ADVERB_COMPARATIVE("RBR"),
    ADVERB_SUPERLATIVE("RBS"),
    
    // ** Closed Words **
	CONJUNCTION("CC"),
	CARDINAL_NUMBER("CD"),
	DETERMINER("DT"),
	EXISTENTIAL_THERE("EX"),
	FOREIGN_WORD("FW"),
	PREPOSISION("IN"),
	LIST("LS"),
	MODAL("MD"),
	PREDETERMINER("PDT"),
	POSSESSIVE("POS"),
	PERSONAL_PRONOUN("PRP"),
	POSSESSIVE_PRONOUN("PRP$"),
	PARTICLE("RP"),
	SYMBOL("SYM"),
	TO("TO"),
	INTERJECTION("UH"),
	WH_DETERMINER("WDT"),
	WH_PRONOUN("WP"),
	WH_PRONOUN_POSSESSIVE("WP$"),
	WH_ADVERB("WRB"),
	
	// ** Unknown type **
	UNKNOWN("UK");
	
    private String code;

    private PartOfSpeech(String code) {
        this.code = code;
    }

    private String getCode() {
        return code;
    }

    /**
     * Attempts to match an input String with a part of speech
     * If no match is found, the Unknown type is returned
     * 
     * @param code
     * @return
     */
    public static PartOfSpeech toPOS(String code) {
        PartOfSpeech result = UNKNOWN;
        for (PartOfSpeech pos : PartOfSpeech.values()) {
            if (pos.getCode().equals(code)) {
                result = pos;
            }
        }
        return result;
    }

    /**
     * Does the entered string match a part of speech?
     * @param pos
     * @return
     */
    public static boolean isWord(String pos) {
        return isWord(toPOS(pos));
    }

    private static boolean isWord(PartOfSpeech pos) {
        if (pos != UNKNOWN) {
            return true;
        } else {
            return false;
        }
    }
    
    /**
     * Open class words are considered nouns, verbs, adjectives, and adverbs.
     * All other parts of speech are considered closed words.
     * 
     * @return
     */
    public static boolean isOpenClassWord(String pos) {
        return isOpenClassWord(toPOS(pos));
    }

    public static boolean isOpenClassWord(PartOfSpeech pos) {
        if (isNoun(pos) || isVerb(pos) || isAdjective(pos) || isAdverb(pos)) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean isNoun(PartOfSpeech pos) {
        if (pos == NOUN || pos == PROPER_SINGULAR_NOUN || pos == PROPER_PLURAL_NOUN || pos == PLURAL_NOUN) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean isVerb(PartOfSpeech pos) {
        if (pos == VERB || pos == VERB_PAST || pos == VERB_PAST_PARTICIPLE || pos == VERB_PRESENT_PARTICIPLE
                || pos == VERB_NON3RD_SINGULAR_PRESENT || pos == VERB_3RD_SINGULAR_PRESENT) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean isAdjective(PartOfSpeech pos) {
        if (pos == ADJECTIVE || pos == ADJECTIVE_COMPARATIVE || pos == ADJECTIVE_SUPERLATIVE) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean isAdverb(PartOfSpeech pos) {
        if (pos == ADVERB || pos == ADVERB_COMPARATIVE || pos == ADVERB_SUPERLATIVE) {
            return true;
        } else {
            return false;
        }
    }
    
    public static boolean isPronoun(PartOfSpeech pos) {
        if (pos == PERSONAL_PRONOUN || pos == POSSESSIVE_PRONOUN) {
            return true;
        } else {
            return false;
        }
    }
    
    public String simpleString() {
        if (isNoun(this)) {
            return "NN";
        } else if (isVerb(this)) {
            return "VB";
        } else if (isAdjective(this)) {
            return "JJ";
        } else if (isAdverb(this)) {
           return "RB";
        } else if (isPronoun(this)) {
            return "PRP";
        } else {
           return this.getCode();
        }
    }
}
