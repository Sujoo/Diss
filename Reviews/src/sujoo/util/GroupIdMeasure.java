package sujoo.util;

public class GroupIdMeasure implements Comparable<GroupIdMeasure> {
    private final int groupId;
    private final Measures measures;
    
    public GroupIdMeasure(int groupId, Measures measures) {
        this.groupId = groupId;
        this.measures = measures;
    }

    public int getGroupId() {
        return groupId;
    }

    public Measures getMeasures() {
        return measures;
    }

    @Override
    public int compareTo(GroupIdMeasure arg0) {
        return Double.compare(arg0.getMeasures().getFscore(), measures.getFscore());
    }
    
    public String toString() {
        return groupId + " : " + measures.getFscore();
    }
}
