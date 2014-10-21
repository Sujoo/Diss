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

public class PrepareReviewsForHIT1 {
    private StanfordNLP nlp;

    public static void main(String[] args) throws Exception {
        PrepareReviewsForHIT1 p = new PrepareReviewsForHIT1();
        p.prepare();
    }

    public PrepareReviewsForHIT1() {
        nlp = StanfordNLP.createLemmaTagger();
    }

    public void prepare() throws Exception {
        BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\mbcusick\\Dropbox\\MTurk\\BetaReviewDataSet.txt"));
        PrintWriter writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                "C:\\Users\\mbcusick\\Dropbox\\MTurk\\BetaMturkCSV.csv"), "UTF-8")));
        writer.println("review1,review2");

        String currentLine = null;
        while ((currentLine = reader.readLine()) != null) {
            String rev1 = prepareReview(currentLine, "rev1");

            currentLine = reader.readLine();
            String rev2 = prepareReview(currentLine, "rev2");
            writer.println(rev1 + "," + rev2);
        }

        reader.close();
        writer.close();
    }

    public String prepareReview(String text, String reviewClass) {
        text = CleanText.cleanTextForMTurk(text);
        text = formatForHIT(text, reviewClass);
        text = CleanText.cleanTextAfterFormatting(text);
        return text;
    }

    public String formatForHIT(String text, String reviewClass) {
        String result = "\"";
        List<String> words = nlp.getWords(text);
        int index = 0;
        for (String word : words) {
            if (word.matches(",|\\.|<br / >")) {
                result += word;
            } else if (word.matches("'|\"|-LRB-|-RRB-|\\\\/|\\\\")) {
                result += " " + word;
            } else if (word.contains("'")) {
                result += "<span class=" + reviewClass + " id=" + index + ">" + word + "</span>";
                index++;
            } else {
                result += " " + "<span class=" + reviewClass + " id=" + index + ">" + word + "</span>";
                index++;
            }
        }
        result += "\"";
        return result.trim();
    }
}
