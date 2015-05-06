package sujoo.main;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import sujoo.nlp.llr.LogLikelihoodRatio;
import sujoo.util.FileReaderUtil;
import sujoo.util.Timer;

public class CalculateLLRMain {
    private LogLikelihoodRatio llr;

    public static void main(String[] args) {
        CalculateLLRMain lemma = new CalculateLLRMain();
        Timer timer = new Timer();
        try {
            timer.start();
            lemma.getLemmas();
            timer.stop();
            timer.print();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public CalculateLLRMain() {
        llr = new LogLikelihoodRatio(10.83);
    }

    public void getLemmas() throws FileNotFoundException, IOException {
        int counter = 0;
        int foregroundCount = 0;
        int backgroundCount = 0;
        System.out.println("Select foreground corpus");
        FileReaderUtil foregroundReader = new FileReaderUtil();
        System.out.println("Foreground: " + foregroundReader.getFileName());
        System.out.println("Select background corpus");
        FileReaderUtil backgroundReader = new FileReaderUtil();
        System.out.println("Background: " + backgroundReader.getFileName());
        try {
            while (foregroundReader.hasNext()) {
                counter++;
                String[] line = foregroundReader.next();

                llr.addForegroundText(line[4]);
                foregroundCount++;

                if (counter % 10000 == 0) {
                    System.out.printf("10,000 count: %d%n", counter / 10000);
                }
            }
            foregroundReader.close();

            while (backgroundReader.hasNext()) {
                counter++;
                String[] line = backgroundReader.next();

                llr.addBackgroundText(line[0]);
                backgroundCount++;

                if (counter % 10000 == 0) {
                    System.out.printf("10,000 count: %d%n", counter / 10000);
                }
            }
            backgroundReader.close();
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }

        System.out.printf("Foreground Count: %d%n", foregroundCount);
        System.out.printf("Background Count: %d%n", backgroundCount);

        llr.calculateLLR();

        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(
                "C:\\Users\\mbcusick\\Documents\\Results\\llr\\" + foregroundReader.getFileName() + "_vs_" + backgroundReader.getFileName() + "_llr.csv")));
        writer.println("Lexem,LLR");
        Map<String, Double> foregroundLLRMap = llr.getSortedForegroundLLRMap();
        for (String term : foregroundLLRMap.keySet()) {
            writer.printf("%s,%.2f%n", term, foregroundLLRMap.get(term));
        }
        writer.close();

        // writer = new PrintWriter(new BufferedWriter(new FileWriter(
        // "C:\\Users\\mbcusick\\Documents\\Results\\t1_b_llr.csv")));
        // writer.println("Background Lexem,LLR");
        // Map<String, Double> backgroundLLRMap =
        // topics.getSortedBackgroundLLRMap();
        // for (String term : backgroundLLRMap.keySet()) {
        // writer.printf("%s,%.2f%n", term, backgroundLLRMap.get(term));
        // }
        // writer.close();
        //
        // writer = new PrintWriter(new BufferedWriter(new FileWriter(
        // "C:\\Users\\mbcusick\\Documents\\Results\\t1_rel.csv")));
        // writer.println("Term,Count");
        // Multiset<String> relevant = topics.getSortedForegroundSet();
        // for (String term : relevant.elementSet()) {
        // writer.printf("%s,%d%n", term, relevant.count(term));
        // }
        // writer.close();
        //
        // writer = new PrintWriter(new BufferedWriter(new FileWriter(
        // "C:\\Users\\mbcusick\\Documents\\Results\\t1_non.csv")));
        // writer.println("Term,Count");
        // Multiset<String> nonrelevant = topics.getSortedBackgroundSet();
        // for (String term : nonrelevant.elementSet()) {
        // writer.printf("%s,%d%n", term, nonrelevant.count(term));
        // }
        // writer.close();
    }
}
