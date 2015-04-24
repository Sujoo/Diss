package sujoo.mturk;

public class CameraMains {

    public static void main(String[] args) throws Exception {
        //cleanMTurkFile("HIT3Downloads\\", "LastCamerab");
        //String wordsToAssign = "7,10,14,15,26,27,28,29,33,41,42,44,46,47,48,49,52,55,58,60,61,67,68,69,71,72,78,83,86,87,88,89,91,93,94,95,96,98,99,101,102,103,104,107,109,113,114,116,119,120,122,125,126,128,130,133,134,135,137,138,140,141,143,144,145,146,151,152,155,156,158,159,164,514,519,711,478,761,640,675,537,385,322,756,645,200,350,349,880,584,182,191,659,617,432,907,179,494,227,701";
        //String wordsToAssign = "30,43,165,3,27,31,57,62,514,32,25,519,711,478,761,640,675,537,385,322,756,645,200,350,100,349,880,584,182,191,659,617,432,907,20,179,33,494,227,701";
        //String finalPush = "7,10,14,15,28,43,44,46,47,48,49,52,58,60,67,68,71,72,78,83,86,88,89,93,94,95,96,98,101,107,109,113,114,116,119,120,122,125,126,128,130,133,134,135,137,140,141,143,144,145,151,152,155,158,159,164,166,168,171,172,173,174,178,179,180,183,184,186,187,188,190,194,195,198,199,201,202,204,207,209,211,212,213,214,217,219,220,222,223,225,228,230,231,232,233,236,238,240,241,243,244,245,247,248,249,250,251,252,253,254,255,256,257,259,261,263,264,265,266,268,269,270,271,272,273,275,277,278,279,282,284,285,286,287,288,290,293,294,295,296,297,298,299,301,302,305,306,308,309,311,312,313,314,315,317,318,319,321,323,324,325,326,327,329,330,331,332,333,334,335,336,337,338,339,340,341,342,343,344,345,347,351,352,353,354,355,356,357,358,359,360,361,363,364,365,366,367,368,369,370";
        //prepareHIT2_Groups("ReferenceFiles\\CameraWordList.csv", "HIT2Uploads\\FinalCamera2.csv",40,12,wordsToAssign);
        //processHIT2_Groups("HIT2Downloads\\FinalCamera2.csv", "ReferenceFiles\\CameraWordList.csv", "ReferenceFiles\\TempCameraGroups.csv");
        //prepareHIT3_Validate("ReferenceFiles\\CameraWordList.csv", "ReferenceFiles\\CameraGroups.csv", "HIT3Uploads\\LastCamera.csv");
        //processHIT3_Validate("HIT3Downloads\\LastCamerab.csv", "ReferenceFiles\\CameraWordList.csv", "ReferenceFiles\\CameraGroups.csv");
        //prepareHIT4_Placement("ReferenceFiles\\CameraWordList.csv", "ReferenceFiles\\CameraGroupsDisplay1.csv", "HIT4Uploads\\FinalCamera3.csv", finalPush);
        //processHIT4_Placement("HIT4Downloads\\FinalCamera2d1.csv", "ReferenceFiles\\CameraWordList.csv", "ReferenceFiles\\CameraGroupsDisplay1.csv");
        prepareHIT5_Validate("ReferenceFiles\\CameraWordList.csv", "ReferenceFiles\\CameraGroups.csv", "HIT5Uploads\\Camera2.csv");
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
        PrepareHIT4_Placement p = new PrepareHIT4_Placement(wordListFile, groupFile, uploadFile, wordsToAssign, 10);
        p.prepare();
        p.outputForWordAssignment();
    }
    
    public static void processHIT4_Placement(String mturkFile, String wordListFile, String groupFile) throws Exception {
        ProcessHIT4_Placement p = new ProcessHIT4_Placement(mturkFile, wordListFile, groupFile);
        p.prepare();
        p.processFrom10();
        p.printOutput();
    }
    
    public static void prepareHIT5_Validate(String wordListFile, String groupFile, String uploadFile) throws Exception {
        PrepareHIT5_FinalValidate p = new PrepareHIT5_FinalValidate(wordListFile, groupFile, uploadFile);
        p.createOutput();
    }
}
