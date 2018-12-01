package Model;

public class InvertedIndexNode {
    private String m_word;
    private int m_numOfAppearances;
    private String m_postingLink;
    private int m_postingLine;


    public InvertedIndexNode(String m_word, int m_numOfAppearances, String m_postingLink, int m_postingLine) {
        this.m_word = m_word;
        this.m_numOfAppearances = m_numOfAppearances;
        this.m_postingLink = m_postingLink;
        this.m_postingLine = m_postingLine;
    }

    public void increaseAppearances(){
        m_numOfAppearances++;
    }

    public int getNumOfAppearances() {
        return m_numOfAppearances;
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
