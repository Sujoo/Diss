//package sujoo.mturk;
//
///**
// * Identify similar words in groups
// */
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.FileOutputStream;
//import java.io.FileReader;
//import java.io.OutputStreamWriter;
//import java.io.PrintWriter;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import java.util.Set;
//
//import com.google.common.collect.ArrayListMultimap;
//import com.google.common.collect.ListMultimap;
//import com.google.common.collect.Sets;
//
//public class PrepareHIT4a_InterPlacement {
//
//    private PrintWriter hit2Writer;
//
//    private ListMultimap<Integer, String> phraseExamplesMap;
//    private Map<Integer, String> wordListMap;
//    private ListMultimap<Integer, Integer> groupWordMap;
//
//    public static void main(String[] args) throws Exception {
//        PrepareHIT2a_InterGroups p = new PrepareHIT2a_InterGroups("ReferenceFiles\\ApparelGroups.csv", "HIT2Uploads\\ApparelGroups4a.csv", 25, 1);
//        p.outputWordGroups();
//    }
//
//    public PrepareHIT4a_InterPlacement(String inputGroupFile, String hit2Output, int hitsToCreate, int groupsPerHIT) throws Exception {
//        MTurkUtils.readGroups("ReferenceFiles\\BookWordList.csv", "ReferenceFiles\\BookGroups.csv");
//        
//        phraseExamplesMap = MTurkUtils.getPhraseExamplesMap();
//        wordListMap = MTurkUtils.getWordListMap();
//        groupWordMap = MTurkUtils.getGroupWordMap();
//        
//        hit2Writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(hit2Output), "UTF-8")));
//    }
//
//    public void outputWordGroups() {
//        // Print Header
//        hit2Writer.println("g1");
//
//        for (int groupId : groupWordMap.keySet()) {
//            List<Integer> wordsInGroup = groupWordMap.get(groupId);
//            printWordGroup("g1");            
//            hit2Writer.println();
//        }
//        hit2Writer.close();
//    }
//    
//    public void printWords(List<Integer> wordIds) {
//        for (Integer wordId : wordIds) {
//            // #1 : fits : dropdown
//            hit2Writer.print("#" + wordId + " : " + getCellHTML(wordId) + " : " + getGroupDropdownHTML() + ",");
//        }
//        
//        if (wordIds.size() < assignPerPage) {
//            for (int i = 0; i < assignPerPage - wordIds.size(); i++) {
//                hit2Writer.print(" ,");
//            }
//        }
//    }
//    
//    public String getGroupDropdownHTML() {
//        // <select name="selectWord1">
//           // <option value="err" selected>--select group--</option>
//           // <option value="g1">Group 1</option>
//           // <option value="g2">Group 2</option>
//           // <option value="g3">Group 3</option>
//           // <option value="g4">Group 4</option>
//           // <option value="ng">No Similar Group</option>
//           // </select>
//           String result = "<select name=word#><option value=err selected>--select group--</option>";
//           for (Integer groupId : groupWordMap.keySet()) {
//               result += "<option value=" + groupId + ">" + groupId + "</option>";
//           }
//           result += "<option value=ng>No Similar Group</option></select>";
//           return result;
//       }
//
//    public void printWordGroup(String groupName) {
//        ArrayList<String> phrases = new ArrayList<String>(phraseExamplesMap.keySet());
//        String phrase1 = phrases.get(random.nextInt(phrases.size()));
//        String phrase2 = phrases.get(random.nextInt(phrases.size()));
//        String phrase3 = phrases.get(random.nextInt(phrases.size()));
//        String phrase4 = phrases.get(random.nextInt(phrases.size()));
//        String phrase5 = phrases.get(random.nextInt(phrases.size()));
//        String phrase6 = phrases.get(random.nextInt(phrases.size()));
//        String phrase7 = phrases.get(random.nextInt(phrases.size()));
//        String phrase8 = phrases.get(random.nextInt(phrases.size()));
//        String phrase9 = phrases.get(random.nextInt(phrases.size()));
//        String phrase10 = phrases.get(random.nextInt(phrases.size()));
//        String phrase11 = phrases.get(random.nextInt(phrases.size()));
//        String phrase12 = phrases.get(random.nextInt(phrases.size()));
//
//        hit2Writer.print("<table class=noselect><tbody>");
//        hit2Writer.print("<tr>");
//        hit2Writer.print("<td>" + getCellHTML(groupName, phrase1) + "</td>");
//        hit2Writer.print("<td>" + getCellHTML(groupName, phrase2) + "</td>");
//        hit2Writer.print("<td>" + getCellHTML(groupName, phrase3) + "</td>");
//        hit2Writer.print("<td>" + getCellHTML(groupName, phrase4) + "</td>");
//        hit2Writer.print("</tr>");
//        hit2Writer.print("<tr>");
//        hit2Writer.print("<td>" + getCellHTML(groupName, phrase5) + "</td>");
//        hit2Writer.print("<td>" + getCellHTML(groupName, phrase6) + "</td>");
//        hit2Writer.print("<td>" + getCellHTML(groupName, phrase7) + "</td>");
//        hit2Writer.print("<td>" + getCellHTML(groupName, phrase8) + "</td>");
//        hit2Writer.print("</tr>");
//        hit2Writer.print("<tr>");
//        hit2Writer.print("<td>" + getCellHTML(groupName, phrase9) + "</td>");
//        hit2Writer.print("<td>" + getCellHTML(groupName, phrase10) + "</td>");
//        hit2Writer.print("<td>" + getCellHTML(groupName, phrase11) + "</td>");
//        hit2Writer.print("<td>" + getCellHTML(groupName, phrase12) + "</td>");
//        hit2Writer.print("</tr>");
//        hit2Writer.print("</tbody></table>");
//    }
//    
//    public void printGroupTables() {
//        hit2Writer.print("<table><tbody>");
//        int counter = 0;
//        for (Integer groupId : groupWordMap.keySet()) {
//            if (counter == 0) {
//                hit2Writer.print("<tr>");
//            }
//            hit2Writer.print("<td>");
//            hit2Writer.print("<h4 style=color: gray;><strong>Group #" + groupId + "</strong></h4>");
//            printGroupTable(groupId);
//            hit2Writer.print("</td>");
//            counter++;
//            if (counter == 4) {
//                hit2Writer.print("</tr>");
//                counter = 0;
//            }
//        }
//        if (counter != 0) {
//            hit2Writer.print("</tr>");
//        }
//        hit2Writer.print("</tbody></table>,");
//    }
//
//    public void printGroupTable(int groupId) {
//        hit2Writer.print("<table class=noselect><tbody>");
//        int counter = 0;
//        for (Integer wordId : groupWordMap.get(groupId)) {
//            if (counter == 0) {
//                hit2Writer.print("<tr>");
//            }
//            hit2Writer.print("<td>" + getCellHTML(wordId) + "</td>");
//            counter++;
//            if (counter == 4) {
//                hit2Writer.print("</tr>");
//                counter = 0;
//            }
//        }
//        if (counter != 0) {
//            hit2Writer.print("</tr>");
//        }
//        hit2Writer.print("</tbody></table>");
//    }
//
//    public String getCellHTML(int id) {
//        // <span class="g1" id="0">soft<div class="ex">So very soft
//        // fabric<br/>Really soft</div></span>
//
//        List<String> examples = phraseExamplesMap.get(id);
//        String phrase = wordListMap.get(id);
//        String htmlExs = "";
//        for (String ex : examples) {
//            htmlExs += "..." + ex + "...<br/>";
//        }
//        htmlExs = htmlExs.substring(0, htmlExs.length() - 5);
//        return "<span><span id=" + id + ">" + phrase + "</span><div class=ex>" + htmlExs + "</div></span>";
//    }
//}
