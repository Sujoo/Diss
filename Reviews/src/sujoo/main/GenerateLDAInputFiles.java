package sujoo.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;

import sujoo.nlp.stanford.StanfordNLP;
import sujoo.util.FileChooserUtil;
import sujoo.util.FileReaderUtil;
import sujoo.util.Timer;

public class GenerateLDAInputFiles {
    private StanfordNLP nlp;

    public static void main(String[] args) {
        GenerateLDAInputFiles gen = new GenerateLDAInputFiles();
        Timer timer = new Timer();
        try {
            timer.start();
            gen.generateAllFiles();
            timer.stop();
            timer.print();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public GenerateLDAInputFiles() {
        nlp = StanfordNLP.createLemmaTagger();
    }

    public void generateAllFiles() throws FileNotFoundException, IOException {
        File file = FileChooserUtil.chooseFile();
        String splitValue = FileChooserUtil.chooseSeparator();
        generateFiles(new FileReaderUtil(file, splitValue), "noun");
        generateFiles(new FileReaderUtil(file, splitValue), "verb");
        generateFiles(new FileReaderUtil(file, splitValue), "adj");
    }

    public void generateFiles(FileReaderUtil fileReader, String type) throws FileNotFoundException, IOException {
        List<Multiset<String>> docLemmaCounts = Lists.newArrayList();
        Set<String> lemmaSet = Sets.newTreeSet();
        Map<String, Integer> wordMap = Maps.newHashMap();
        int mapCounter = 0;
        
        
        int counter = 0;
        int foregroundCount = 0;
        try {
            while (fileReader.hasNext()) {
                counter++;
                String[] line = fileReader.next();

                Multiset<String> lemmas = HashMultiset.create();
                if (type.equals("noun")) {
                    lemmas = nlp.getNounLemmas(line[6]);
                } else if (type.equals("verb")) {
                    lemmas = nlp.getVerbLemmas(line[6]);
                } else if (type.equals("adj")) {
                    lemmas = nlp.getAdjLemmas(line[6]);
                }
                foregroundCount++;

                docLemmaCounts.add(lemmas);
                lemmaSet.addAll(lemmas);

                if (counter % 10000 == 0) {
                    System.out.printf("10,000 count: %d%n", counter / 10000);
                }
            }
            fileReader.close();

        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }

        System.out.printf("Foreground Count: %d%n", foregroundCount);
        // print lda dat and vocab files here
        // print docLemmaCounts and lemmaSet for first run
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\mbcusick\\Documents\\Results\\ldainput\\"
                + fileReader.getFileName() + "_" + type + "_vocab.txt")));
        List<String> lemmaList = Lists.newArrayList(lemmaSet);
        Collections.sort(lemmaList);
        for (String word : lemmaList) {
            writer.println(word);
            wordMap.put(word, mapCounter);
            mapCounter++;
        }
        writer.close();

        writer = new PrintWriter(new BufferedWriter(new FileWriter("C:\\Users\\mbcusick\\Documents\\Results\\ldainput\\" + fileReader.getFileName()
                + "_" + type + ".dat")));
        for (Multiset<String> words : docLemmaCounts) {
            String line = words.elementSet().size() + " ";
            for (String word : words.elementSet()) {
                int currentCount = words.count(word);
                line += wordMap.get(word) + ":" + currentCount + " ";
            }
            writer.println(line);
        }
        writer.close();
    }
}
