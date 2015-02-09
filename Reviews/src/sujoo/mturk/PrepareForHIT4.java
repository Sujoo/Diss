package sujoo.mturk;

/**
 * Identify group for term
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.TreeMultimap;

public class PrepareForHIT4 {
    private BufferedReader wordListReader;
    private BufferedReader groupReader;
    private PrintWriter hit4Writer;

    private ListMultimap<Integer, String> phraseExamplesMap;
    private Map<Integer, String> wordListMap;
    private TreeMultimap<Integer, Integer> groupWordMap;
    private Random random;
    private String wordsToAssign;
    private List<List<Integer>> wordsPerHIT;
    
    private static final int assignPerPage = 10;

    public static void main(String[] args) throws Exception {
        PrepareForHIT4 p = new PrepareForHIT4("ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\ApparelGroups.csv", "HIT4Uploads\\ApparelGroups4.csv");
        p.prepare();
        p.outputForWordAssignment();
    }

    public PrepareForHIT4(String inputWordListFile, String inputGroupFile, String hit4Output) throws Exception {
        //wordsToAssign = "15,26,30,34,35,43,47,45,54,59,57,56,65,66,73,85,84,81,83,93,90,99,96,110,111,106,105,119,117,116,124,138,131,153,158,159,144,146,151,168,163";
        wordsToAssign = "170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231,232,233,234,235,236,237,238,239,240,241,242,243,244,245,246,247,248,249,250,251,252,253,254,255,256,257,258,259,260,261,262,263,264,265,266,267,268,269,270,271,272,273,274,275,276,278,279,280,281,282,283,284,285,286,287,288,289,291,292,293,294,295,296,297,298,299,300,301,302,304,305,306,307,308,309,310,311,312,313,314,315,316,317,318,319,320,321,322,323,324,325,326,327,328,329,330,331,332,333,334,335,336,337,339,340,342,343,344,345,346,347,348,349,350,351,352,353,354,355,356,357,358,359,360,361,362,363,364,365,366,367,368,369,371,372,373,374,375,376,377,378,379,380,381,382,383,384,385,386,387,388,389,390,391,392,393,394,395,396,397,398,399,400,401,402,403,404,405,406,407,408,409,410,411,412,413,414,415,416,417,418,419,420,421,422,423,424,425,427,428,429,430,431,432,433,434,435,436,437,438,439,440,441,442,443,445,446,447,448,449,450,451,452,453,454,455,456,457,458,459,460,461,462,463,464,465,466,467,468,469,470,471,472,473,474,476,477,478,479,480,481,482,483,484,485,486,487,488,489,490,491,492,493,494,495,496,497,498,499,500,501,502,504,505,506,507,508,509,510,511,512,513,514,515,516,517,518,519,520,521,522,523,524,525,526,527,528,529,530,531,532,533,534,535,536,537,538,539,540,541,542,543,544,545,546,547,548,549,550,551,553,554,555,556,557,558,559,560,561,562,563,564,565,566,567,568,569,570,571,572,573,574,575,576,577,578,579,580,581,582,583,584,585,586,587,588,589,590,591,592,593,594,595,596,597,598,599,600,601,602,603,604,605,606,607,608,609,610,611,612,613,614,615,616,617,618,621,622,623,624,625,626,627,628,629,630,631,632,633,634,635,636,637,638,639,640,641,642,643,644,645,646,647,648,649,650,651,652,653,654,655,656,657,658,659,661,662,663,664,665,666,667,668,669,670,671,672,673,674,675,676,677,678,679,680,681,682,683,684,685,686,687,688,689,690,691,692,693,694,695,696,697,698,699,700,701,702,703,704,705,706,707,708,709,710,711,712,713,714,715,716,717,718,719,720,721,722,723,724,725,726,727,728,729,730,731,732,733,734,735,736,737,738,739,740,741,742,743,744,745,746,747,748,749,750,751,752,753,754,755,756,757,758,759,760,761,762,763,764,765,766,767,768,769,770,771,772,773,774,775,776,777,778,779,780,781,782,783,784,785,786,787,788,789,790,791,792,793,794,795,796,797,798,799,800,802,803,804,805,806,807,808,809,810,811,812,813,814,815,816,817,818,819,820,821,822,823,824,825,826,827,828,829,830,831,832,833,834,835,836,837,838,839,840,841,842,843,844,845,846,847,848,849,850,851,852,853,854,855,856,857,858,859,860,861,862,863,864,865,866,867,868,869,870,871,872,873,874,875,876,877,878,879,880,881,882,883,884,885,886,887,888,889,890,891,892,893,894,895,896,897,898,899,900,901,902,903,904,905,906,907,908,909,911,912,913,914,916,917,918,919,920,921,922,923,924,925,926,927,928,929,930,931,932,933,934,935,936,937,938,939,940,941,942,943,944,945,946,947,948,949,950,951,952,953,954,955,956,957,958,959,960,961,962,963,964,965,966,967,968,969";

        wordListReader = new BufferedReader(new FileReader(inputWordListFile));
        groupReader = new BufferedReader(new FileReader(inputGroupFile));
        hit4Writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(hit4Output), "UTF-8")));
        
        phraseExamplesMap = ArrayListMultimap.create();
        wordListMap = Maps.newHashMap();
        groupWordMap = TreeMultimap.create();
        random = new Random();
        wordsPerHIT = Lists.newArrayList();
    }

    public void prepare() throws Exception {
        String currentLine = null;
        currentLine = wordListReader.readLine();
        while ((currentLine = wordListReader.readLine()) != null) {
            // id phrase reviewIds Sample Sentence Fragments
            String[] fields = currentLine.split("\t");
            int id = Integer.parseInt(fields[0]);
            String phrase = fields[1];
            // String reviewIds = fields[2];
            String frags = fields[3];

            wordListMap.put(id, phrase);

            String[] examples = frags.split(",");
            if (examples.length > 3) {
                int one = random.nextInt(examples.length);
                int two = random.nextInt(examples.length);
                int three = random.nextInt(examples.length);

                while (two == one) {
                    two = random.nextInt(examples.length);
                }
                while (three == two || three == one) {
                    three = random.nextInt(examples.length);
                }

                phraseExamplesMap.put(id, examples[one]);
                phraseExamplesMap.put(id, examples[two]);
                phraseExamplesMap.put(id, examples[three]);
            } else {
                for (int i = 0; i < examples.length; i++) {
                    phraseExamplesMap.put(id, examples[i]);
                }
            }
        }
        wordListReader.close();
        
        currentLine = null;
        currentLine = groupReader.readLine();
        while ((currentLine = groupReader.readLine()) != null) {
            // GroupId  Words
            String[] fields = currentLine.split("\t");
            int id = Integer.parseInt(fields[0]);
            String[] wordIds = fields[1].split(",");
            for (int i = 0; i < wordIds.length; i++) {
                groupWordMap.put(id, Integer.parseInt(wordIds[i]));            }
        }
        
        String[] words = wordsToAssign.split(",");
        int counter = 0;
        List<Integer> currentList = Lists.newArrayList();
        for (int i = 0; i < words.length; i++) {
            currentList.add(Integer.parseInt(words[i]));
            counter++;
            if (counter == assignPerPage) {
                wordsPerHIT.add(currentList);
                counter = 0;
                currentList = Lists.newArrayList();
            }
        }
        if (counter != 0) {
            wordsPerHIT.add(currentList);
        }
    }

    public void outputForWordAssignment() {        
        //hit4Writer.println("tables,w1,w2");
        hit4Writer.println("tables,w1,w2,w3,w4,w5,w6,w7,w8,w9,w10");
        for (List<Integer> wordIds : wordsPerHIT) {
            printGroupTables();
            printWords(wordIds);
            hit4Writer.println();
        }
        hit4Writer.close();
    }
    
    public void printWords(List<Integer> wordIds) {
        for (Integer wordId : wordIds) {
            // #1 : fits : dropdown
            hit4Writer.print("#" + wordId + " : " + getCellHTML(wordId) + " : " + getGroupDropdownHTML() + ",");
        }
        
        if (wordIds.size() < assignPerPage) {
            for (int i = 0; i < assignPerPage - wordIds.size(); i++) {
                hit4Writer.print(" ,");
            }
        }
    }
    
    public String getGroupDropdownHTML() {
     // <select name="selectWord1">
        // <option value="err" selected>--select group--</option>
        // <option value="g1">Group 1</option>
        // <option value="g2">Group 2</option>
        // <option value="g3">Group 3</option>
        // <option value="g4">Group 4</option>
        // <option value="ng">No Similar Group</option>
        // </select>
        String result = "<select name=word#><option value=err selected>--select group--</option>";
        for (Integer groupId : groupWordMap.keySet()) {
            result += "<option value=" + groupId + ">Group " + groupId + "</option>";
        }
        result += "<option value=ng>No Similar Group</option></select>";
        return result;
    }
    
    public void printGroupTables() {
        hit4Writer.print("<table><tbody>");
        int counter = 0;
        for (Integer groupId : groupWordMap.keySet()) {
            if (counter == 0) {
                hit4Writer.print("<tr>");
            }
            hit4Writer.print("<td>");
            hit4Writer.print("<h4 style=color: gray;><strong>Group #" + groupId + "</strong></h4>");
            printGroupTable(groupId);
            hit4Writer.print("</td>");
            counter++;
            if (counter == 4) {
                hit4Writer.print("</tr>");
                counter = 0;
            }
        }
        if (counter != 0) {
            hit4Writer.print("</tr>");
        }
        hit4Writer.print("</tbody></table>,");
    }

    public void printGroupTable(int groupId) {
        hit4Writer.print("<table class=noselect><tbody>");
        int counter = 0;
        for (Integer wordId : groupWordMap.get(groupId)) {
            if (counter == 0) {
                hit4Writer.print("<tr>");
            }
            hit4Writer.print("<td>" + getCellHTML(wordId) + "</td>");
            counter++;
            if (counter == 4) {
                hit4Writer.print("</tr>");
                counter = 0;
            }
        }
        if (counter != 0) {
            hit4Writer.print("</tr>");
        }
        hit4Writer.print("</tbody></table>");
    }

    public String getCellHTML(int id) {
        // <span class="g1" id="0">soft<div class="ex">So very soft
        // fabric<br/>Really soft</div></span>

        List<String> examples = phraseExamplesMap.get(id);
        String phrase = wordListMap.get(id);
        String htmlExs = "";
        for (String ex : examples) {
            htmlExs += "..." + ex + "...<br/>";
        }
        htmlExs = htmlExs.substring(0, htmlExs.length() - 5);
        return "<span><span id=" + id + ">" + phrase + "</span><div class=ex>" + htmlExs + "</div></span>";
    }
}
