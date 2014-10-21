package sujoo.nlp.topics.datatypes;

public class TermLLR implements Comparable<TermLLR>{
    private final String term;
    private final double LLR;
    
    public TermLLR(String term, double LLR) {
        this.term = term;
        this.LLR = LLR;
    }

    public String getTerm() {
        return term;
    }

    public double getLLR() {
        return LLR;
    }
    
    @Override
    public int compareTo(TermLLR other) {
        if (LLR > other.getLLR()) {
            return 1;
        } else if (LLR < other.getLLR()) {
            return -1;
        } else {
            return term.compareTo(other.term);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(LLR);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((term == null) ? 0 : term.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TermLLR other = (TermLLR) obj;
        if (Double.doubleToLongBits(LLR) != Double.doubleToLongBits(other.LLR))
            return false;
        if (term == null) {
            if (other.term != null)
                return false;
        } else if (!term.equals(other.term))
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return term + ":" + LLR;
    }
}
