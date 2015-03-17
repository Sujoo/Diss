package sujoo.mturk;

public class CameraMains {

    public static void main(String[] args) throws Exception {
        //cleanMTurkFile("HIT2Downloads\\", "CameraGroups3");
        //String wordsToAssign = "2,7,9,10,12,14,15,25,26,28,29,32,33,35,38,42,44,46,47,48,49,50,51,52,53,55,56,58,60,61,65,66,67,68,69,71,72,74,75,77,78,83,85,86,87,88,89,91,93,94,95,98,99,101,102,103,104,105,106,107,109,110,111,112,114,116,119,120,121,122,125,126,128,130,133,134,135,136,137,139,141,142,143,144,145,146,149,151,152,154,155,156,158,159,162,163,164,168,170,172,174,178,180,182,183,184,185,186,187,190,191,194,195,196,197,198,199,200,201,202,204,207,209,211,212,213,214,215,217,218,219,220,222,223,225,226,227,228,230,231,232,233,234,235,236,237,238,241,243,244,245,246,247,248,249,250,251,252,253,254,255,256,257,258,259,261,262,263,264,265,266,268,269,270,271,272,273,275,276,277,278,279,282,284,285,286,287,288,290,291,295,296,297,298,299,301,302,304,305,306,307,308,309,311,312,314,315,317,318,319,320,321,322,324,325,326,327,329,330,331,332,333,334,335,336,338,339,340,341,342,343,345,347,348,350,351,352,353,354,355,356,357,358,359,361,363,364,365,366,367,368,369,370,371,372,373,374,375,376,378,379,380,381,382,383,385,386,388,389,390,392,393,394,396,397,398,400,402,403,405,406,408,410,411,412,413,415,418,419,422,423,424,425,426,427,428,429,430,432,433,434,435,437,438,439,441,442,443,444,445,446,447,448,449,450,451,453,454,455,456,457,458,459,460,461,464,465,466,467,468,469,471,473,474,476,478,479,480,482,484,485,486,487,489,490,491,494,495,496,498,499,500,501,502,503,504,505,506,507,510,513,515,516,517,518,519,520,521,522,524,525,527,528,529,530,531,532,533,535,536,537,538,539,540,541,542,543,544,545,546,547,548,549,552,554,555,556,557,558,559,560,561,562,563,565,566,567,568,569,570,571,572,573,574,575,576,577,578,579,580,581,582,583,584,585,586,587,588,589,590,591,592,593,594,596,597,598,599,600,601,602,603,604,605,606,607,608,610,611,612,613,614,615,616,617,618,619,620,621,622,623,624,625,626,627,628,629,630,631,632,633,634,635,636,637,638,639,640,641,642,643,644,645,646,647,648,649,650,651,652,653,654,655,656,657,658,659,660,661,662,663,664,665,666,667,668,669,670,671,672,673,674,675,676,677,678,679,680,681,682,683,684,685,686,687,688,689,690,691,692,693,694,695,696,697,698,699,700,701,702,703,704,705,706,707,708,709,710,711,712,713,714,715,716,717,718,719,720,721,722,723,724,725,726,727,728,729,730,731,732,733,734,735,736,737,738,739,740,741,742,743,744,745,746,747,748,749,750,751,753,754,755,756,757,758,759,760,761,762,763,764,765,766,767,768,769,770,771,772,773,775,776,777,778,779,780,782,783,784,785,786,787,788,789,790,791,792,793,794,795,796,797,798,799,800,801,802,803,804,805,806,807,808,809,810,811,812,813,814,815,816,817,818,819,820,821,822,823,824,825,826,827,828,829,830,831,832,833,834,835,836,837,838,839,840,841,842,843,844,845,846,847,848,850,851,852,853,854,855,856,857,858,859,860,861,862,863,864,865,866,867,868,869,870,871,872,873,874,875,876,877,878,879,880,881,882,883,884,885,886,887,888,889,891,892,893,894,895,896,897,898,900,901,902,903,904,905,906,907,908,909,910,911,912,913,914,915,916,917,918,919,920,921,922,923,924,925,926,927,928,929,930,931";
        //prepareHIT2_Groups("ReferenceFiles\\CameraWordList.csv", "HIT2Uploads\\CameraGroups2.csv",40,12,wordsToAssign);
        //processHIT2_Groups("HIT2Downloads\\CameraGroups3.csv", "ReferenceFiles\\CameraWordList.csv", "ReferenceFiles\\TempCameraGroups.csv");
        prepareHIT3_Validate("ReferenceFiles\\CameraWordList.csv", "ReferenceFiles\\TempCameraGroups.csv", "HIT3Uploads\\CameraGroups2.csv");
        //processHIT3_Validate("HIT3Downloads\\CameraGroups1b.csv", "ReferenceFiles\\CameraWordList.csv", "ReferenceFiles\\CameraGroups.csv");
        //prepareHIT4_Placement("ReferenceFiles\\CameraWordList.csv", "ReferenceFiles\\CameraGroups.csv", "HIT4Uploads\\CameraGroups1a.csv", wordsToAssign);
        //processHIT4_Placement("HIT4Downloads\\CameraGroups1a.csv", "ReferenceFiles\\CameraWordList.csv", "ReferenceFiles\\CameraGroups.csv");
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
