package sujoo.mturk;

public class BookMains {

    public static void main(String[] args) throws Exception {
        //cleanMTurkFile("HIT2Downloads\\", "BookGroups4");
        String wordsToAssign = "13,18,19,22,24,25,33,34,36,38,50,52,61,62,64,66,67,68,71,72,73,74,75,77,80,81,85,86,87,93,95,96,97,101,102,107,115,117,118,120,121,122,126,128,139,142,143,144,147,148,149,150,151,153,155,157,158,160,161,163,164,165,166,167,168,169,171,172,175,176,177,178,179,180,181,182,184,185,186,189,191,193,194,195,196,197,200,202,203,204,205,207,208,211,212,215,216,220,222,223,224,225,226,227,229,230,231,232,233,234,235,236,237,239,240,241,244,246,247,249,250,251,252,253,254,255,256,257,259,261,262,264,265,266,267,268,269,270,271,272,273,274,275,276,278,279,280,281,282,283,284,286,287,288,289,292,293,294,295,300,301,302,303,304,305,306,308,309,311,312,314,315,317,318,319,320,321,322,323,324,325,326,327,328,329,330,331,332,333,334,335,336,338,339,340,342,343,344,345,346,347,348,349,350,351,352,353,354,355,357,359,360,362,363,364,366,368,370,371,372,373,376,377,379,382,383,384,385,386,387,388,390,391,393,395,396,397,398,399,400,401,402,403,404,405,408,410,411,413,414,415,416,417,418,421,422,423,424,426,428,429,431,432,433,434,435,437,438,442,445,446,448,449,451,452,453,454,456,457,459,460,461,462,464,465,466,468,469,470,471,473,474,475,477,479,480,482,483,484,485,487,488,489,491,494,495,496,497,498,499,500,501,502,503,504,505,506,507,508,509,510,511,512,513,514,515,516,517,519,520,521,522,523,524,525,526,527,528,530,531,532,533,534,535,536,538,540,542,543,545,546,547,549,551,552,553,554,555,556,558,559,560,561,562,565,566,567,569,570,571,572,573,574,575,577,579,581,582,584,585,586,587,588,589,590,591,592,593,594,596,598,599,600,601,603,604,605,606,607,608,609,610,611,612,613,614,615,616,617,618,619,620,621,622,623,624,625,626,627,628,629,630,631,632,633,634,635,636,637,638,639,640,641,642,643,644,645,646,647,648,649,650,651,652,653,654,655,656,657,658,659,660,661,662,664,665,666,667,668,669,670,671,672,673,674,675,676,677,678,679,680,681,682,683,684,685,686,687,688,689,690,691,692,693,694,695,696,697,698,699,700,701,702,703,704,705,706,707,708,709,710,711,712,713,714,715,716,717,718,719,720,721,722,723,724,725,726,727,728,729,730,732,733,734,735,736,737,738,739,740,741,742,743,744,745,746,747,748,749,750,752,753,754,755,756,757,758,759,760,761,762,763,764,765,766,767,768,769,770,771,772,773,774,775,776,777,778,779,780,781,782,783,784,785,786,787,788,789,790,791,792,793,794,796,797,798,799,800,801,802,803,804,805,806,807,808,809,810,811,812,813,814,815,816,817,818,819,820,821,822,823,824,825,826,827,828,829,830,831,832,833,834,835,836,837,838,839,841,842,843,844,845,846,847,848,849,850,851,852,853,854,855,856,857,858,859,860,861,862,863,864,865,866,868,869,870,871,872,873,874,876,877,878,879,880,881,882,883,884,885,886,887,888,889,890,891,892,893,894,895,896,897,898,899,900,901,902,903,904,905,906,908,909,910,911,912,913,914,915,916,917,918,919,920,921,922,923,924,925,926,927,928,929,931,932,933,934,935,936,937,938,939,940,941,942,943,944,945,946,947,948,949,950,951,952,953,954,955,956,957,958,959,960,961,962,963,964,965,966,967,968,969,970,971,972,973,974,975,976,977,978,979,980,981,982,983,984,985,986,987,988,989,990,991,992,993,994,995,996,997,998";
        //prepareHIT2_Groups("ReferenceFiles\\BookWordList.csv", "HIT2Uploads\\BookGroups3.csv",40,12,wordsToAssign);
        //processHIT2_Groups("HIT2Downloads\\BookGroups3.csv", "ReferenceFiles\\BookWordList.csv", "ReferenceFiles\\TempBookGroups.csv");
        //prepareHIT3_Validate("ReferenceFiles\\BookWordList.csv", "ReferenceFiles\\BookGroups.csv", "HIT3Uploads\\BookGroups2a.csv");
        //processHIT3_Validate("HIT3Downloads\\BookGroups2a.csv", "ReferenceFiles\\BookWordList.csv", "ReferenceFiles\\BookGroups.csv");
        prepareHIT4_Placement("ReferenceFiles\\BookWordList.csv", "ReferenceFiles\\TempBookGroups.csv", "HIT4Uploads\\BookGroups3.csv", wordsToAssign);
        //processHIT4_Placement("HIT4Downloads\\BookGroups2.csv", "ReferenceFiles\\BookWordList.csv", "ReferenceFiles\\BookGroups.csv");
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
