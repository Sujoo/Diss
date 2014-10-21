package sujoo.nlp.clean;

import java.util.regex.Pattern;

public class CleanText {
    private static final Pattern endingPunctuation = Pattern.compile("[\\.\\?!]+");
    private static final Pattern breakTag = Pattern.compile("<(br|p)\\s?/?>");
    private static final Pattern emphasisWord = Pattern.compile("\\*([a-zA-Z]+)\\*");
    private static final Pattern explicative1 = Pattern.compile("[a-zA-Z]+[\\*]+[a-zA-Z]*");
    private static final Pattern explicative2 = Pattern.compile("[a-zA-Z]*[\\*]+[a-zA-Z]+");
    private static final Pattern explicative3 = Pattern.compile("[\\*]{2,}");
    private static final Pattern apostrophy = Pattern.compile("&#34;");
    private static final Pattern lessThan = Pattern.compile("&#60;");
    private static final Pattern greaterThan = Pattern.compile("&#62;");
    private static final Pattern underscore = Pattern.compile("_+");
    private static final Pattern forwardSlash = Pattern.compile("/");
    private static final Pattern backwardSlash = Pattern.compile("\\\\");
    private static final Pattern hyphen = Pattern.compile("-");
    private static final Pattern hyphen2 = Pattern.compile("([a-zA-Z])-([a-zA-Z])");
    private static final Pattern emoticons = Pattern.compile("[:;][o-]?[DPp\\)\\(\\]\\[\\{\\}]");
    private static final Pattern www = Pattern.compile("(http.{1,3})?(www\\.)\\S+\\s");
    private static final Pattern diffQuotes = Pattern.compile("[“”]");
    private static final Pattern diffApos = Pattern.compile("’");
    private static final Pattern diffApos2 = Pattern.compile("`");
    private static final Pattern extraSpaces = Pattern.compile("(\\s)\\s+");
    private static final Pattern half = Pattern.compile("Â½");
    
    private static final Pattern leftBrace = Pattern.compile("-LRB-");
    private static final Pattern rightBrace = Pattern.compile("-RRB-");
    private static final Pattern forwardSlash2 = Pattern.compile("\\\\/");
    private static final Pattern badPeriod = Pattern.compile("([a-zA-Z])\\.(\\w)");
    private static final Pattern wtfbr = Pattern.compile("<br / >");
    
    public static String cleanText(String text) {
        text = www.matcher(text).replaceAll("[WebAddress]");
        text = emoticons.matcher(text).replaceAll(" ");
        text = breakTag.matcher(text).replaceAll(" ");
        text = emphasisWord.matcher(text).replaceAll("$1");
        text = explicative1.matcher(text).replaceAll("explicative");
        text = explicative2.matcher(text).replaceAll("explicative");
        text = explicative3.matcher(text).replaceAll("explicative");
        text = endingPunctuation.matcher(text).replaceAll(" \\. ");
        text = underscore.matcher(text).replaceAll(" ");
        text = apostrophy.matcher(text).replaceAll("'");
        text = lessThan.matcher(text).replaceAll("<");
        text = greaterThan.matcher(text).replaceAll(">");
        text = forwardSlash.matcher(text).replaceAll(" / ");
        text = backwardSlash.matcher(text).replaceAll(" \\\\ ");
        text = hyphen.matcher(text).replaceAll(" - ");
        text = diffQuotes.matcher(text).replaceAll("\"");
        text = diffApos.matcher(text).replaceAll("'");
        text = extraSpaces.matcher(text).replaceAll("$1");
        
        return text.trim();
    }
    
    public static String cleanTextForMTurk(String text) {
        text = www.matcher(text).replaceAll("[WebAddress]");
        text = emphasisWord.matcher(text).replaceAll("$1");
        text = explicative1.matcher(text).replaceAll("explicative");
        text = explicative2.matcher(text).replaceAll("explicative");
        text = explicative3.matcher(text).replaceAll("explicative");
        text = endingPunctuation.matcher(text).replaceAll("\\.");
        text = badPeriod.matcher(text).replaceAll("$1\\. $2");
        text = underscore.matcher(text).replaceAll(" ");
        text = apostrophy.matcher(text).replaceAll("'");
        text = lessThan.matcher(text).replaceAll("<");
        text = greaterThan.matcher(text).replaceAll(">");
        text = forwardSlash.matcher(text).replaceAll(" " + "/" + " ");
        text = backwardSlash.matcher(text).replaceAll(" " + "\\\\" + " ");
        text = hyphen2.matcher(text).replaceAll("$1 - $2");
        text = diffQuotes.matcher(text).replaceAll("\"");
        text = diffApos.matcher(text).replaceAll("'");
        text = extraSpaces.matcher(text).replaceAll("$1"); 
        text = half.matcher(text).replaceAll("1/2");
        
        return text.trim();
    }
    
    public static String cleanTextAfterFormatting(String text) {
        text = diffApos2.matcher(text).replaceAll("'");
        text = leftBrace.matcher(text).replaceAll("(");
        text = rightBrace.matcher(text).replaceAll(")");
        text = forwardSlash2.matcher(text).replaceAll("/");
        text = wtfbr.matcher(text).replaceAll("<br/>");
        
        return text.trim();
    }

}
