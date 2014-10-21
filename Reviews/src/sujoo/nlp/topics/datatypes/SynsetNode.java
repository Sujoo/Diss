package sujoo.nlp.topics.datatypes;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.smu.tspell.wordnet.Synset;

public class SynsetNode implements Comparable<SynsetNode> {
    private final Synset synset;
    private List<TermLLR> keyTerms;
    private List<SynsetNode> parents;
    private List<SynsetNode> children;
    private DecimalFormat df;

    public SynsetNode(Synset synset) {
        this.synset = synset;
        keyTerms = Lists.newArrayList();
        parents = Lists.newArrayList();
        children = Lists.newArrayList();
        df = new DecimalFormat("#.00");
        df.setGroupingUsed(true);
        df.setGroupingSize(3);
    }
    
    public List<TermLLR> getKeyTerms() { 
        return keyTerms;
    }

    public void addKeyTerm(TermLLR term) {
        keyTerms.add(term);
    }

    public void removeKeyTerm(TermLLR term) {
        if (keyTerms.contains(term)) {
            keyTerms.remove(term);
        }
    }
    
    public boolean hasKeyTerms() {
        if (keyTerms.size() > 0) {
            return true;
        } else {
            return false;
        }
    }
    
    public List<String> getKeyTermWords() {
        List<String> termWords = Lists.newArrayList();
        for (TermLLR term : keyTerms) {
            termWords.add(term.getTerm());
        }
        return termWords;
    }
    
    public List<String> getKeyTermHierarchyWords() {
        Set<String> words = Sets.newHashSet();
        for (SynsetNode child : children) {
            words.addAll(child.getKeyTermHierarchyWords());
        }
        
        for (TermLLR term : keyTerms) {
            words.add(term.getTerm());
        }
        
        List<String> wordList = Lists.newArrayList(words);
        Collections.sort(wordList);        
        return wordList;
    }

    public Synset getSynset() {
        return synset;
    }

    public List<SynsetNode> getParents() {
        return parents;
    }

    public void addParent(SynsetNode parent) {
        parents.add(parent);
    }
    
    public void removeParent(SynsetNode parent) {
        if (parents.contains(parent)) {
            parents.remove(parent);
        }
    }
    
    public void dropParents() {
        parents = Lists.newArrayList();
    }

    public boolean hasParents() {
        if (parents.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public List<SynsetNode> getChildren() {
        return children;
    }

    public void addChild(SynsetNode child) {
        children.add(child);
    }
    
    public void addChildren(List<SynsetNode> children) {
        this.children.addAll(children);
    }
    
    public void removeChild(SynsetNode child) {
        if (children.contains(child)) {
            children.remove(child);
        }
    }
    
    public void dropChildren() {
        children = Lists.newArrayList();
    }

    public boolean hasChildren() {
        if (children.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public double getLLR() {
        double llr = 0;
        for (TermLLR term : keyTerms) {
            llr += term.getLLR();
        }
        return llr;
    }
    
    public double getHierarchyLLR() {
        double result = getLLR();
        
        for(SynsetNode child : children) {
            result += child.getHierarchyLLR();
        }
        
        return result;
    }
    
    public double getLLROfChildrenAndSiblings() {
        double result = 0;
        
        result += getLLROfChildren();
        
        for (SynsetNode parentNode : parents) {
            result += parentNode.getLLROfChildren() - getLLR();
        }
        
        return result;
    }
    
    
    private double getLLROfChildren() {
        double result = 0;
        for (SynsetNode child : children) {
            result += child.getLLR();
        }
        return result;
    }
    
    public double getLLROfChildrenAndSiblingHierarchies() {
        double result = 0;
        
        result += getLLROfChildrenHierarchies();
        
        for (SynsetNode parentNode : parents) {
            result += parentNode.getLLROfChildrenHierarchies() - getLLR();
        }
        
        return result;
    }
    
    
    private double getLLROfChildrenHierarchies() {
        double result = 0;
        for (SynsetNode child : children) {
            result += child.getHierarchyLLR();
        }
        return result;
    }

    public String getDefinition() {
        return synset.getDefinition();
    }

    @Override
    public String toString() {
        String result = "Definition: " + synset.getDefinition() + "\n";
        result += "Key Terms:[" + df.format(getLLR()) + "]" + getKeyTermWords() + "\n";
        result += "Synset Terms:" + Lists.newArrayList(synset.getWordForms());
        return result;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((children == null) ? 0 : children.hashCode());
        result = prime * result + ((synset == null) ? 0 : synset.hashCode());
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
        SynsetNode other = (SynsetNode) obj;
        if (children == null) {
            if (other.children != null)
                return false;
        } else if (!children.equals(other.children))
            return false;
        if (synset == null) {
            if (other.synset != null)
                return false;
        } else if (!synset.equals(other.synset))
            return false;
        return true;
    }

    @Override
    public int compareTo(SynsetNode arg0) {
        return Double.compare(getHierarchyLLR(), arg0.getHierarchyLLR());
    }
}
