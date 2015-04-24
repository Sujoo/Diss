package sujoo.mturk;

public class ApparelMains {
    public static void main(String[] args) throws Exception {
        //cleanMTurkFile("HIT4Downloads\\", "FinalApparel3");
        //String wordsToAssign = "15,18,35,47,54,59,61,65,66,73,81,83,84,90,96,99,105,109,111,115,117,155,158,159,163,168,169,175,179,182,194,203,207,212,215,216,220,223,224,225,229,232,235,240,245,248,249,251,253,254,258,259,260,261,263,267,268,273,275,279,284,289,296,297,298,301,304,306,310,314,316,317,318,319,320,323,327,328,336,339,348,349,351,352,356,357,358,359,361,363,371,373,375,376,377,380,381,383,385,388,391,395,406,410,417,421,422,428,432,437,439,440,442,445,446,453,454,455,457,458,465,466,468,470,478,481,482,483,489,491,495,500,502,504,506,508,512,515,516,517,521,522,524,527,530,531,538,545,548,549,550,551,555,556,557,558,559,560,565,566,568,569,570,571,578,583,589,595,596,598,601,603,606,607,608,609,613,615,616,617,618,621,623,631,632,633,639,640,642,648,650,653,656,658,661,662,664,667,669,674";
        //String finalPush = "15,35,47,54,59,65,66,73,81,83,84,90,96,105,111,117,155,158,159,168,169,175,179,194,207,212,216,220,223,224,225,229,235,240,245,248,251,253,254";
        //String wordsToAssignNew = "15,18,22,24,26,27,28,29,30,31,34,35,39,40,43,45,47,48,49,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126,127,128,129,130,131,132,133,134,135,136,137,138,139,140,141,142,143,144,145,146,147,148,149,150,151,152,153,154,155,156,157,158,159,160,161,162,163,164,165,166,167,168,169,170,171,172,173,174,175,176,177,178,179,180,181,182,183,184,185,186,187,188,189,190,191,192,193,194,195,196,197,198,199,200,201,202,203,204,205,206,207,208,209,210,211,212,213,214,215,216,217,218,219,220,221,222,223,224,225,226,227,228,229,230,231";
        //prepareHIT2_Groups("ReferenceFiles\\ApparelWordList.csv", "HIT2Uploads\\NewApparel2.csv",10,12,wordsToAssignNew);
        //processHIT2_Groups("HIT2Downloads\\ApparelGroups5.csv", "ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\TempApparelGroups.csv");
        //prepareHIT3_Validate("ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\ApparelGroups.csv", "HIT3Uploads\\FinalApparel1.csv");
        //processHIT3_Validate("HIT3Downloads\\FinalApparel1.csv", "ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\ApparelGroups.csv");
        //prepareHIT4_Placement("ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\ApparelGroupsDisplay1.csv", "HIT4Uploads\\FinalApparel4.csv", finalPush);
        //processHIT4_Placement("HIT4Downloads\\FinalApparel3.csv", "ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\ApparelGroupsDisplay1.csv");
        prepareHIT5_Validate("ReferenceFiles\\ApparelWordList.csv", "ReferenceFiles\\ApparelGroups.csv", "HIT5Uploads\\Apparel4.csv");
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
        //p.output16WordGroups();
    }
    
    public static void processHIT2_Groups(String mturkFile, String wordListFile, String outputGroupFile) throws Exception {
        ProcessHIT2_Groups p = new ProcessHIT2_Groups(mturkFile, wordListFile, outputGroupFile);
        p.prepareIdFile();
        //p.process();
        p.processSingleGroupHIT();
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
        PrepareHIT4_Placement p = new PrepareHIT4_Placement(wordListFile, groupFile, uploadFile, wordsToAssign, 10);
        p.prepare();
        p.outputForWordAssignment();
    }
    
    public static void processHIT4_Placement(String mturkFile, String wordListFile, String groupFile) throws Exception {
        ProcessHIT4_Placement p = new ProcessHIT4_Placement(mturkFile, wordListFile, groupFile);
        p.prepare();
        p.processFrom5();
        p.printOutput();
    }
    
    public static void prepareHIT5_Validate(String wordListFile, String groupFile, String uploadFile) throws Exception {
        PrepareHIT5_FinalValidate p = new PrepareHIT5_FinalValidate(wordListFile, groupFile, uploadFile);
        p.createOutput();
    }
}
