package sujoo.mturk;

public class BookMains {

    public static void main(String[] args) throws Exception {
        cleanMTurkFile("HIT3Downloads\\", "FinalBooks2");
        //String wordsToAssign = "21,7,57,28,44,3,15,46,65,82,103,112,140,154,170,173,162,187,190,199,219,201,248,277,258,313,316,298,299,337,380,412,394,436,425,430,476,472,563,568,518,66,122,351,521,831,842,868,961,899";
        //prepareHIT2_Groups("ReferenceFiles\\BookWordList.csv", "HIT2Uploads\\BookGroupsBreakG7.csv",20,12,wordsToAssign);
        //processHIT2_Groups("HIT2Downloads\\BookGroupsBreakG7.csv", "ReferenceFiles\\BookWordList.csv", "ReferenceFiles\\BreakG7BookGroups.csv");
        //prepareHIT3_Validate("ReferenceFiles\\BookWordList.csv", "ReferenceFiles\\BookGroups.csv", "HIT3Uploads\\FinalBookGroups2.csv");
        processHIT3_Validate("HIT3Downloads\\FinalBooks2.csv", "ReferenceFiles\\BookWordList.csv", "ReferenceFiles\\BookGroups.csv");
        //prepareHIT4_Placement("ReferenceFiles\\BookWordList.csv", "ReferenceFiles\\BookGroups.csv", "HIT4Uploads\\FinalBook1.csv", wordsToAssign);
        //processHIT4_Placement("HIT4Downloads\\BookGroups3.csv", "ReferenceFiles\\BookWordList.csv", "ReferenceFiles\\TempBookGroups.csv");
    }
    
    public static void cleanMTurkFile(String folder, String file) throws Exception {
        MTurkUtils.cleanMTurkOutputFile(folder + file);
    }
    
    public static void prepareHIT2_Groups(String wordListFile, String uploadFile, int hitsToCreate, int wordsPerHIT, String wordsToAssign) throws Exception {
        PrepareHIT2_Groups p = new PrepareHIT2_Groups(wordListFile, uploadFile, hitsToCreate, wordsPerHIT);
        p.setupExtraWordList(wordsToAssign);
        p.prepare(false);
        //p.output4WordGroups();
        p.output16WordGroups();
    }
    
    public static void processHIT2_Groups(String mturkFile, String wordListFile, String outputGroupFile) throws Exception {
        ProcessHIT2_Groups p = new ProcessHIT2_Groups(mturkFile, wordListFile, outputGroupFile);
        p.prepareIdFile();
        p.processSingleGroupHIT();
        p.outputGroups();
    }
    
    public static void prepareHIT3_Validate(String wordListFile, String groupFile, String uploadFile) throws Exception {
        PrepareHIT3_Validate p = new PrepareHIT3_Validate(wordListFile, groupFile, uploadFile);
        p.prepare();
        p.outputForInitialGroupValidation();
    }
    
    public static void processHIT3_Validate(String mturkFile, String wordListFile, String groupFile) throws Exception {
        ProcessHIT3_Validate p = new ProcessHIT3_Validate(mturkFile, wordListFile, groupFile);
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
