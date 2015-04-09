package sujoo.mturk;

public class CameraMains {

    public static void main(String[] args) throws Exception {
        cleanMTurkFile("HIT3Downloads\\", "FinalCamera3");
        //String wordsToAssign = "7,10,14,15,26,27,28,29,33,41,42,44,46,47,48,49,52,55,58,60,61,67,68,69,71,72,78,83,86,87,88,89,91,93,94,95,96,98,99,101,102,103,104,107,109,113,114,116,119,120,122,125,126,128,130,133,134,135,137,138,140,141,143,144,145,146,151,152,155,156,158,159,164,514,519,711,478,761,640,675,537,385,322,756,645,200,350,349,880,584,182,191,659,617,432,907,179,494,227,701";
        //String wordsToAssign = "30,43,165,3,27,31,57,62,514,32,25,519,711,478,761,640,675,537,385,322,756,645,200,350,100,349,880,584,182,191,659,617,432,907,20,179,33,494,227,701";
        //prepareHIT2_Groups("ReferenceFiles\\CameraWordList.csv", "HIT2Uploads\\FinalCamera2.csv",40,12,wordsToAssign);
        //processHIT2_Groups("HIT2Downloads\\FinalCamera2.csv", "ReferenceFiles\\CameraWordList.csv", "ReferenceFiles\\TempCameraGroups.csv");
        //prepareHIT3_Validate("ReferenceFiles\\CameraWordList.csv", "ReferenceFiles\\TempCameraGroups.csv", "HIT3Uploads\\FinalCamera3.csv");
        processHIT3_Validate("HIT3Downloads\\FinalCamera3.csv", "ReferenceFiles\\CameraWordList.csv", "ReferenceFiles\\TempCameraGroups.csv");
        //prepareHIT4_Placement("ReferenceFiles\\CameraWordList.csv", "ReferenceFiles\\CameraGroupsDisplay1.csv", "HIT4Uploads\\FinalCamera2d1.csv", wordsToAssign);
        //processHIT4_Placement("HIT4Downloads\\FinalCamera2d1.csv", "ReferenceFiles\\CameraWordList.csv", "ReferenceFiles\\CameraGroupsDisplay1.csv");
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
        //p.process();
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
