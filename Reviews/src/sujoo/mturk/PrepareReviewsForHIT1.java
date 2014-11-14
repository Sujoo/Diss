package sujoo.mturk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;

import sujoo.nlp.clean.CleanText;
import sujoo.nlp.stanford.StanfordNLP;
import sujoo.nlp.stanford.datatypes.PartOfSpeech;
import sujoo.nlp.stanford.datatypes.WordLexem;

public class PrepareReviewsForHIT1 {
    private StanfordNLP nlp;
    private BufferedReader reader;
    private PrintWriter writer;
    private PrintWriter reviewIdFile;
    

    public static void main(String[] args) throws Exception {
        PrepareReviewsForHIT1 p = new PrepareReviewsForHIT1();
        p.prepare();
    }

    public PrepareReviewsForHIT1() throws Exception {
        nlp = StanfordNLP.createLemmaTagger();
        // reader = new BufferedReader(new FileReader("C:\\Users\\mbcusick\\Dropbox\\MTurk\\BetaReviewDataSet.txt"));
        reader = new BufferedReader(new FileReader("shoesReviews"));
        // writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\Users\\mbcusick\\Dropbox\\MTurk\\BetaMturkCSV.csv"), "UTF-8")));
        writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("shoes.csv"), "UTF-8")));
        writer.println("review1Id,review2Id,review1,review2");
        
        
        // reviewIdFile = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:\\Users\\mbcusick\\Dropbox\\MTurk\\ReviewIdFile.csv"), "UTF-8")));
        reviewIdFile = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("ShoeReviewIds.csv"), "UTF-8")));
        reviewIdFile.println("reviewId\torigReviewText\twordLexemList");
    }

    public void prepare() throws Exception {
        int reviewId = 1;
        String currentLine = null;
        while ((currentLine = reader.readLine()) != null) {
            String text = currentLine.split("\t")[4];
            reviewIdFile.print(reviewId + "\t" + text + "\t");
            String rev1 = prepareReview(text, "rev1");
            reviewIdFile.println();
            reviewId++;

            currentLine = reader.readLine();
            text = currentLine.split("\t")[4];
            reviewIdFile.print(reviewId + "\t" + text + "\t");
            String rev2 = prepareReview(text, "rev2");
            reviewIdFile.println();
            writer.println(reviewId-1 + "," + reviewId + "," + rev1 + "," + rev2);
            reviewId++;
        }

        reader.close();
        writer.close();
        reviewIdFile.close();
    }

    public String prepareReview(String text, String reviewClass) {
        text = CleanText.cleanTextForMTurk(text);
        text = formatForHIT(text, reviewClass);
        text = CleanText.cleanTextAfterFormatting(text);
        return text;
    }

    public String formatForHIT(String text, String reviewClass) {
        String result = "\"";
        List<WordLexem> words = nlp.getWordLexems(text);
        int index = 0;
        for (WordLexem wordLexem : words) {
            String word = wordLexem.getWord();
            if (word.matches(",|\\.|<br / >")) {
                result += word;
            } else if (word.matches("'|\"|-lrb-|-rrb-|\\\\/|\\\\") || !PartOfSpeech.isWord(wordLexem.getPos())) {
                result += " " + word;
            } else if (word.contains("'")) {
                reviewIdFile.print(index + ":" + wordLexem.toString() + ";");
                result += "<span class=" + reviewClass + " id=" + index + ">" + word + "</span>";
                index++;
            } else {
                result += " " + "<span class=" + reviewClass + " id=" + index + ">" + word + "</span>";
                reviewIdFile.print(index + ":" + wordLexem.toString() + ";");
                index++;
            }
        }
        result += "\"";
        return result.trim();
    }
}
