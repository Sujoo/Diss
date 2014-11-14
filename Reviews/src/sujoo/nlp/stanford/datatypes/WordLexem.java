package sujoo.nlp.stanford.datatypes;

public class WordLexem {
    private String word;
    private String lexem;
    private String pos;
    
    public WordLexem(String word, String lexem, String pos) {
        this.word = word.toLowerCase();
        this.lexem = lexem.toLowerCase();
        this.pos = pos;
    }
    
    public String getWord() {
        return word;
    }
    
    public String getLexem() {
        return lexem;
    }
    
    public String getPos() {
        return pos;
    }
    
    public String toString() {
        // {"word":"loves","index":"2"}
        return word + ":" + lexem + ":" + pos;
    }
    
    public String shortString() {
        return lexem + ":" + pos;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lexem == null) ? 0 : lexem.hashCode());
        result = prime * result + ((pos == null) ? 0 : pos.hashCode());
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
        WordLexem other = (WordLexem) obj;
        if (lexem == null) {
            if (other.lexem != null)
                return false;
        } else if (!lexem.equals(other.lexem))
            return false;
        if (pos == null) {
            if (other.pos != null)
                return false;
        } else if (!pos.equals(other.pos))
            return false;
        return true;
    }
}
