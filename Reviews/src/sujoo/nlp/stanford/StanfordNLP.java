package sujoo.nlp.stanford;

import java.util.List;
import java.util.Properties;

import sujoo.nlp.clean.CleanText;
import sujoo.nlp.stanford.datatypes.PartOfSpeech;
import sujoo.nlp.stanford.datatypes.StanfordNLPProperties;
import sujoo.nlp.stanford.datatypes.WordLexem;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class StanfordNLP {
    StanfordCoreNLP stanfordCoreNLP;
    StanfordNLPProperties properties;

    private StanfordNLP(StanfordNLPProperties props) {
        properties = props;
        stanfordCoreNLP = new StanfordCoreNLP(convertStanfordNLPPropertiesToProperties(props));
    }

    private Properties convertStanfordNLPPropertiesToProperties(StanfordNLPProperties nlpProps) {
        String propertyNames = "tokenize";
        if (nlpProps.isCleanXML()) {
            propertyNames += ", cleanxml";
        }
        if (nlpProps.isSsplit()) {
            propertyNames += ", ssplit";
        }
        if (nlpProps.isPos()) {
            propertyNames += ", pos";
        }
        if (nlpProps.isLemma()) {
            propertyNames += ", lemma";
        }
        if (nlpProps.isNer()) {
            propertyNames += ", ner";
        }
        if (nlpProps.isRegexner()) {
            propertyNames += ", regexner";
        }
        if (nlpProps.isTruecase()) {
            propertyNames += ", truecase";
        }
        if (nlpProps.isParse()) {
            propertyNames += ", parse";
        }
        if (nlpProps.isDcoref()) {
            propertyNames += ", dcoref";
        }

        Properties props = new Properties();
        props.put("annotators", propertyNames);
        return props;
    }

    private Annotation annotateText(String text) {
        Annotation document = new Annotation(text);
        stanfordCoreNLP.annotate(document);
        return document;
    }

    public int getWordCount(String text) {
        return annotateText(text).get(TokensAnnotation.class).size();
    }
    
    public List<String> getWords(String text) {
        List<String> list = Lists.newArrayList();
        
        Annotation document = annotateText(text);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                list.add(token.get(TextAnnotation.class));
            }
        }
        
        return list;
    }
    
    public List<WordLexem> getWordLexems(String text) {
        List<WordLexem> list = Lists.newArrayList();
        
        Annotation document = annotateText(text);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                String word = token.get(TextAnnotation.class);
                String lexem = token.get(LemmaAnnotation.class);
                String pos = PartOfSpeech.toPOS(token.get(PartOfSpeechAnnotation.class)).simpleString();
                list.add(new WordLexem(word, lexem, pos));
            }
        }
        
        return list;
    }

    public Multiset<String> getLemmas(String text) {
        text = CleanText.cleanText(" " + text + " ");
        Multiset<String> lemmas = HashMultiset.create();

        Annotation document = annotateText(text);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                // String word = token.get(TextAnnotation.class);
                PartOfSpeech pos = PartOfSpeech.toPOS(token.get(PartOfSpeechAnnotation.class));
                if (PartOfSpeech.isNoun(pos)) {
                    lemmas.add(token.get(LemmaAnnotation.class).toLowerCase() + "_N");
                } else if (PartOfSpeech.isVerb(pos)) {
                    lemmas.add(token.get(LemmaAnnotation.class).toLowerCase() + "_V");
                } else if (PartOfSpeech.isAdjective(pos)) {
                    lemmas.add(token.get(LemmaAnnotation.class).toLowerCase() + "_J");
                } else if (PartOfSpeech.isAdverb(pos)) {
                    lemmas.add(token.get(LemmaAnnotation.class).toLowerCase() + "_D");
                }
            }
        }

        return lemmas;
    }
    
    public Multiset<String> getNounLemmas(String text) {
        text = CleanText.cleanText(" " + text + " ");
        Multiset<String> lemmas = HashMultiset.create();

        Annotation document = annotateText(text);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                // String word = token.get(TextAnnotation.class);
                PartOfSpeech pos = PartOfSpeech.toPOS(token.get(PartOfSpeechAnnotation.class));
                if (PartOfSpeech.isNoun(pos)) {
                    lemmas.add(token.get(LemmaAnnotation.class).toLowerCase() + "_N");
                }
            }
        }

        return lemmas;
    }
    
    public Multiset<String> getVerbLemmas(String text) {
        text = CleanText.cleanText(" " + text + " ");
        Multiset<String> lemmas = HashMultiset.create();

        Annotation document = annotateText(text);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                // String word = token.get(TextAnnotation.class);
                PartOfSpeech pos = PartOfSpeech.toPOS(token.get(PartOfSpeechAnnotation.class));
                if (PartOfSpeech.isVerb(pos)) {
                    lemmas.add(token.get(LemmaAnnotation.class).toLowerCase() + "_V");
                }
            }
        }

        return lemmas;
    }
    
    public Multiset<String> getAdjLemmas(String text) {
        text = CleanText.cleanText(" " + text + " ");
        Multiset<String> lemmas = HashMultiset.create();

        Annotation document = annotateText(text);
        List<CoreMap> sentences = document.get(SentencesAnnotation.class);

        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(TokensAnnotation.class)) {
                // String word = token.get(TextAnnotation.class);
                PartOfSpeech pos = PartOfSpeech.toPOS(token.get(PartOfSpeechAnnotation.class));
                if (PartOfSpeech.isAdjective(pos)) {
                    lemmas.add(token.get(LemmaAnnotation.class).toLowerCase() + "_J");
                } else if (PartOfSpeech.isAdverb(pos)) {
                    lemmas.add(token.get(LemmaAnnotation.class).toLowerCase() + "_D");
                }
            }
        }

        return lemmas;
    }

    public static StanfordNLP createBasicWordCounter() {
        return new StanfordNLP(new StanfordNLPProperties());
    }

    public static StanfordNLP createLemmaTagger() {
        return new StanfordNLP(new StanfordNLPProperties().ssplit().pos().lemma());
    }
}
