package sujoo.mturk;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Maps;

public class MTurkUtils {

    private static BufferedReader groupReader;
    private static BufferedReader wordListReader;

    private static Map<Integer, String> wordListMap;
    private static ListMultimap<Integer, Integer> groupWordMap;
    
    public static void main(String[] args) throws Exception {
        printApparelGroups();
    }
    
    public static void printApparelGroups() throws Exception {
        readGroups("ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\ApparelGroups.csv");
        printGroups();
    }

    public static void readGroups(String wordListFile, String groupFile) throws Exception {
        wordListMap = Maps.newHashMap();
        groupWordMap = ArrayListMultimap.create();
        wordListReader = new BufferedReader(new FileReader(wordListFile));
        groupReader = new BufferedReader(new FileReader(groupFile));

        String currentLine = null;
        currentLine = wordListReader.readLine();
        while ((currentLine = wordListReader.readLine()) != null) {
            String[] fields = currentLine.split("\t");
            String wordId = fields[0];
            String phrase = fields[1];
            wordListMap.put(Integer.parseInt(wordId), phrase);
        }
        wordListReader.close();

        currentLine = null;
        currentLine = groupReader.readLine();
        while ((currentLine = groupReader.readLine()) != null) {
            // GroupId Words
            String[] fields = currentLine.split("\t");
            int id = Integer.parseInt(fields[0]);
            String[] wordIds = fields[1].split(",");
            for (int i = 0; i < wordIds.length; i++) {
                groupWordMap.put(id, Integer.parseInt(wordIds[i]));
            }
        }
        groupReader.close();
    }
    
    public static void printGroups() {
        for (int groupId : groupWordMap.keySet()) {
            System.out.println("Group: " + groupId);
            for (int wordId : groupWordMap.get(groupId)) {
                System.out.print(wordListMap.get(wordId) + ":" + wordId + ", ");
            }
            System.out.println();
            System.out.println();
        }
    }

}
