package sujoo.mturk;

public class CleanMTurkFile {

    public static void main(String[] args) throws Exception {
        String folder = "HIT4Downloads\\";
        String file = "ApparelGroups4a";
        MTurkUtils.cleanMTurkOutputFile(folder + file);
    }

}
