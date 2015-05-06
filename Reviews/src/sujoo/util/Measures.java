package sujoo.util;

import java.text.DecimalFormat;

public class Measures {
    private final double recall;
    private final double precision;
    private final double fscore;
    private final DecimalFormat df = new DecimalFormat("0.000");
    
    public Measures(double precision, double recall, double fscore) {
        this.precision = precision;
        this.recall = recall;
        this.fscore = fscore;
    }

    public double getRecall() {
        return recall;
    }

    public double getPrecision() {
        return precision;
    }

    public double getFscore() {
        return fscore;
    }
    
    public String toString() {
        return "Precision: " + df.format(precision) + "\n" + "Recall   : " + df.format(recall) + "\n" + "F-Score  : " + df.format(fscore);
    }
}
