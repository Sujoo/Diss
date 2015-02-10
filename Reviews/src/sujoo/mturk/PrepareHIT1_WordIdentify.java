package sujoo.mturk;

/**
 * Select important words from review text
 */

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


public class PrepareHIT1_WordIdentify {
    private StanfordNLP nlp;
    private BufferedReader reviewInputReader;
    private PrintWriter reviewOuputFileWriter;
    private PrintWriter reviewIdOutputFileWriter;
    

    public static void main(String[] args) throws Exception {
        //PrepareReviewsForHIT1 p = new PrepareReviewsForHIT1("FinalizedReviews\\apparelReviews", "apparel.csv", "ApparelReviewIds.csv");
        //PrepareReviewsForHIT1 p = new PrepareReviewsForHIT1("FinalizedReviews\\bookReviews", "book.csv", "BookReviewIds.csv");
        //PrepareReviewsForHIT1 p = new PrepareReviewsForHIT1("FinalizedReviews\\cameraReviews", "camera.csv", "CameraReviewIds.csv");
        PrepareHIT1_WordIdentify p = new PrepareHIT1_WordIdentify("FinalizedReviews\\shoesReviews", "shoes2.csv", "ShoesReviewIds2.csv");

        p.prepare();
    }

    public PrepareHIT1_WordIdentify(String reviewFile, String reviewOutputFile, String reviewIdOutputFile) throws Exception {
        nlp = StanfordNLP.createLemmaTagger();
        reviewInputReader = new BufferedReader(new FileReader(reviewFile));
        reviewOuputFileWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(reviewOutputFile), "UTF-8")));
        reviewOuputFileWriter.println("review1Id,review2Id,review1,review2");
        
        
        reviewIdOutputFileWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(reviewIdOutputFile), "UTF-8")));
        reviewIdOutputFileWriter.println("reviewId\torigReviewText\twordLexemList");
    }

    public void prepare() throws Exception {
        int reviewId = 1;
        String currentLine = null;
        while ((currentLine = reviewInputReader.readLine()) != null) {
            String text = currentLine.split("\t")[4];
            reviewIdOutputFileWriter.print(reviewId + "\t" + text + "\t");
            String rev1 = prepareReview(text, "rev1");
            reviewIdOutputFileWriter.println();
            reviewId++;

            currentLine = reviewInputReader.readLine();
            text = currentLine.split("\t")[4];
            reviewIdOutputFileWriter.print(reviewId + "\t" + text + "\t");
            String rev2 = prepareReview(text, "rev2");
            reviewIdOutputFileWriter.println();
            reviewOuputFileWriter.println(reviewId-1 + "," + reviewId + "," + rev1 + "," + rev2);
            reviewId++;
        }

        reviewInputReader.close();
        reviewOuputFileWriter.close();
        reviewIdOutputFileWriter.close();
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
                reviewIdOutputFileWriter.print(index + ":" + wordLexem.toString() + ";");
                result += "<span class=" + reviewClass + " id=" + index + ">" + word + "</span>";
                index++;
            } else {
                result += " " + "<span class=" + reviewClass + " id=" + index + ">" + word + "</span>";
                reviewIdOutputFileWriter.print(index + ":" + wordLexem.toString() + ";");
                index++;
            }
        }
        result += "\"";
        return result.trim();
    }
}
