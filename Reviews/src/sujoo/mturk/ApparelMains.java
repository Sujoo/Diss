package sujoo.mturk;

public class ApparelMains {
    public static void main(String[] args) throws Exception {
        //cleanMTurkFile("HIT4Downloads\\", "ApparelGroups4a");
        String wordsToAssign = "35,47,54,59,65,66,73,83,84,90,96,105,117,146,158,159,163,168,175,182,194,207,212,216,220,223,224,225,229,240,245,248,249,251,253,254,255,258,259,261,263,267,268,273,275,279,284,289,296,297,304,306,310,314,317,318,320,327,336,339,348,349,351,352,356,358,359,361,363,371,375,376,380,381,383,385,388,391,395,406,410,413,417,421,422,428,432,437,439,440,442,445,446,454,458,460,465,468,470,478,481,482,483,489,491,500,506,508,512,515,516,517,521,522,524,527,530,538,545,548,549,550,551,555,557,558,559,560,565,566,568,569,570,571,578,583,589,595,596,598,601,606,607,608,609,611,615,616,618,621,627,631,633,639,640,642,644,648,650,653,656,658,661,662,664,667,669,671,674,675,677,679,680,683,685,686,687,689,691,693,695,697,700,704,705,710,717,718,719,722,725,729,733,736,737,738,741,743,747,748,755,758,759,760,761,764,768,769,777,784,785,787,792,793,794,799,800,802,809,810,811,812,813,815,816,817,824,825,826,827,832,840,846,850,851,854,856,859,860,862,863,865,871,873,876,879,882,885,890,898,900,907,908,911,920,928,937,940,941,944,946,948,950,951,953,956,958,960,962,964,966,968";
        prepareHIT2_Groups("ReferenceFiles\\ApparelWordList.csv", "HIT2Uploads\\ApparelGroups5.csv",40,12,wordsToAssign);
        //processHIT2_Groups("HIT2Downloads\\ApparelGroups4a.csv", "ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\ApparelGroups.csv");
        //prepareHIT3_Validate("ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\ApparelGroups.csv", "HIT3Uploads\\ApparelGroups4a.csv");
        //processHIT3_Validate("HIT3Downloads\\ApparelGroups4a.csv", "ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\ApparelGroups.csv");
        //prepareHIT4_Placement("ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\ApparelGroups.csv", "HIT4Uploads\\ApparelGroups4a.csv", wordsToAssign);
        //processHIT4_Placement("HIT4Downloads\\ApparelGroups4a.csv", "ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\ApparelGroups.csv");
    }
    
    public static void cleanMTurkFile(String folder, String file) throws Exception {
        MTurkUtils.cleanMTurkOutputFile(folder + file);
    }

    public static void prepareHIT2_Groups(String wordListFile, String uploadFile, int hitsToCreate, int wordsPerHIT, String wordsToAssign) throws Exception {
        PrepareHIT2_Groups p = new PrepareHIT2_Groups(wordListFile, uploadFile, hitsToCreate, wordsPerHIT);
        p.setupExtraWordList(wordsToAssign);
        p.prepare(false);
        //p.output4WordGroups();
        p.output12WordGroups();
    }
    
    public static void processHIT2_Groups(String mturkFile, String wordListFile, String outputGroupFile) throws Exception {
        ProcessHIT2_Groups p = new ProcessHIT2_Groups(mturkFile, wordListFile, outputGroupFile);
        p.prepareIdFile();
        p.process();
        p.outputGroups();
    }
    
    public static void prepareHIT3_Validate(String wordListFile, String groupFile, String uploadFile) throws Exception {
        PrepareHIT3_Validate p = new PrepareHIT3_Validate(wordListFile, groupFile, uploadFile);
        p.prepare();
        p.outputForInitialGroupValidation();
    }
    
    public static void processHIT3_Validate(String mturkFile, String wordListFile, String groupsFile) throws Exception {
        ProcessHIT3_Validate p = new ProcessHIT3_Validate(mturkFile, wordListFile, groupsFile);
        p.prepare();
        p.processFrom1PerHIT();
        p.writeOutput();
    }
    
    public static void prepareHIT4_Placement(String wordListFile, String groupFile, String uploadFile, String wordsToAssign) throws Exception {
        PrepareHIT4_Placement p = new PrepareHIT4_Placement(wordListFile, groupFile, uploadFile, wordsToAssign);
        p.prepare();
        p.outputForWordAssignment();
    }
    
    public static void processHIT4_Placement(String mturkFile, String wordListFile, String groupFile) throws Exception {
        ProcessHIT4_Placement p = new ProcessHIT4_Placement(mturkFile, wordListFile, groupFile);
        p.prepare();
        p.processFrom10();
        p.printOutput();
    }
}
