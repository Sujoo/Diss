package sujoo.test;

import java.util.List;

import com.google.common.collect.Multiset;

import sujoo.nlp.clean.CleanText;
import sujoo.nlp.stanford.StanfordNLP;

public class CleanTextTest {

    public static void main(String[] args) {
        StanfordNLP nlp = StanfordNLP.createLemmaTagger();
        
        //String text = " *NOTE* *** :-) ;o] :{mr. bob small/large___\\*\\* best<p />boy.pretty<br />girl?! ??? !! !!!!!!! !!! !?!?!?!?!?!? ?!? !?! .. ";
        String text = "*NOTE* Whoever thought that putting stup*d sh** f*** pockets inside of pockets needs a swift kick in the ****** .";
        //String text = "   In the meantime, I guess I’ll try B18’s next, small- large hopefully  they   are still the “traditional”";
        System.out.println("Orig: " + text);
        //Multiset<String> lemmas = nlp.getLemmas(text);
        //System.out.println("Lemmas: " + lemmas);
        System.out.println("Cleaned: " + CleanText.cleanText(text));
        Multiset<String> cleanLemmas = nlp.getLemmas(CleanText.cleanText(text));
        System.out.println("Lemmas: " + cleanLemmas);
        
        String t = "7-1\\/2";
        //t = CleanText.cleanTextForMTurk(t);
        if (t.matches("[0-9-/\\.\\\\]+")) {
            System.out.println("match");
        } else {
            System.out.println(t);            
        }
        
        t = "I'm feeling I can't do this with 11's on my feet.";
        List<String> l = nlp.getWords(t);
        System.out.println(l);
    }

}
