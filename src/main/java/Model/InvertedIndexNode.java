package Model;

public class InvertedIndexNode {
    private String m_word;
    private int m_numOfAppirnces;
    private String m_postingLink;
    private int m_postingLine;


    public InvertedIndexNode(String word, int numOfAppirnces, String postingLink, int postingLine) {
        this.m_word = m_word;
        this.m_numOfAppirnces = m_numOfAppirnces;
        this.m_postingLink = m_postingLink;
        this.m_postingLine = m_postingLine;
    }

    public void incrisAppirnces(){m_numOfAppirnces++;}

    public int getNumOfAppirnces() {
        return m_numOfAppirnces;
    }

    public String getWord() {
        return m_word;
    }

    public String getPostingLink() {
        return m_postingLink;
    }

    public int getPostingLine() {
        return m_postingLine;
    }
}
