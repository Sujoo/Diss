package sujoo.main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
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

import sujoo.nlp.clean.CleanText;
import sujoo.nlp.stanford.StanfordNLP;
import sujoo.nlp.stanford.datatypes.PartOfSpeech;
import sujoo.nlp.stanford.datatypes.WordLexem;
import sujoo.util.FileChooserUtil;
import sujoo.util.FileReaderUtil;
import sujoo.util.Timer;

public class GenerateLDAInputFiles {
    private StanfordNLP nlp;

    public static void main(String[] args) throws Exception {
        GenerateLDAInputFiles gen = new GenerateLDAInputFiles();
        //gen.generateDissertationFiles("FinalizedReviews\\apparelReviews","LDAInput\\apparel");
        //gen.generateDissertationFiles("FinalizedReviews\\bookReviews","LDAInput\\book");
        gen.generateDissertationFiles("FinalizedReviews\\cameraReviews","LDAInput\\camera");
    }
    
    public void oldMainThing() {
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
    
    public void generateDissertationFiles(String reviewFile, String fileName) throws Exception {
        List<String> stopWords = Lists.newArrayList("a,about,above,after,again,against,all,am,an,and,any,are,aren't,as,at,be,because,been,before,being,below,between,both,but,by,can't,cannot,could,couldn't,did,didn't,do,does,doesn't,doing,don't,down,during,each,few,for,from,further,had,hadn't,has,hasn't,have,haven't,having,he,he'd,he'll,he's,her,here,here's,hers,herself,him,himself,his,how,how's,i,i'd,i'll,i'm,i've,if,in,into,is,isn't,it,it's,its,itself,let's,me,more,most,mustn't,my,myself,no,nor,not,of,off,on,once,only,or,other,ought,our,ours,ourselves,out,over,own,same,shan't,she,she'd,she'll,she's,should,shouldn't,so,some,such,than,that,that's,the,their,theirs,them,themselves,then,there,there's,these,they,they'd,they'll,they're,they've,this,those,through,to,too,under,until,up,very,was,wasn't,we,we'd,we'll,we're,we've,were,weren't,what,what's,when,when's,where,where's,which,while,who,who's,whom,why,why's,with,won't,would,wouldn't,you,you'd,you'll,you're,you've,your,yours,yourself,yourselves,n't,'d,'m,'ve,'s,'ll,'re,:-lrb-,<,>,%,&,',+,------,aa,aaa,bc,ca,co,co.,i.,ive,im,jc,n,t,u,v,w".split(","));
        List<Multiset<String>> docLemmaCounts = Lists.newArrayList();
        Set<String> lemmaSet = Sets.newTreeSet(); 

        BufferedReader reviewInputReader = new BufferedReader(new FileReader(reviewFile));
        String currentLine = null;
        while ((currentLine = reviewInputReader.readLine()) != null) {
            String text = currentLine.split("\t")[4];
            text = CleanText.cleanTextForMTurk(text);
            List<WordLexem> wordLexems = nlp.getWordLexems(text);
            List<String> words = Lists.newArrayList();
            for (WordLexem wl : wordLexems) {
                if (PartOfSpeech.isWord(wl.getPos()) && !stopWords.contains(wl.getWord())) {
                    words.add(wl.getWord());
                    //words.add(wl.getLexem());
                }
            }
            docLemmaCounts.add(HashMultiset.create(words));
            lemmaSet.addAll(words);
        }
        reviewInputReader.close();
        
        outputLDAInputFiles(fileName, lemmaSet, docLemmaCounts);
    }
    
    public void outputLDAInputFiles(String fileName, Set<String> lemmaSet, List<Multiset<String>> docLemmaCounts) throws Exception {
        Map<String, Integer> wordMap = Maps.newHashMap();
        int mapCounter = 0;
        // print lda dat and vocab files here
        // print docLemmaCounts and lemmaSet for first run
        PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName + "_vocab.txt")));
        List<String> lemmaList = Lists.newArrayList(lemmaSet);
        Collections.sort(lemmaList);
        for (String word : lemmaList) {
            writer.println(word);
            wordMap.put(word, mapCounter);
            mapCounter++;
        }
        writer.close();

        writer = new PrintWriter(new BufferedWriter(new FileWriter(fileName + ".dat")));
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
    
    public void generateAllFiles() throws Exception {
        File file = FileChooserUtil.chooseFile();
        String splitValue = FileChooserUtil.chooseSeparator();
        generateFiles(new FileReaderUtil(file, splitValue), "noun");
        generateFiles(new FileReaderUtil(file, splitValue), "verb");
        generateFiles(new FileReaderUtil(file, splitValue), "adj");
    }
    
    public void generateFiles(FileReaderUtil fileReader, String type) throws Exception {
        List<Multiset<String>> docLemmaCounts = Lists.newArrayList();
        Set<String> lemmaSet = Sets.newTreeSet();        
        
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
        
        outputLDAInputFiles(fileReader.getFileName(), lemmaSet, docLemmaCounts);

        System.out.printf("Foreground Count: %d%n", foregroundCount);
    }
}
