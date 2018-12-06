package Index;

public class DocDictionaryNode {
    private String m_docName;
    private int m_maxFreq;
    private String m_maxFreqWord;
    private int m_numOfUniWords;
    private String m_city;

    public DocDictionaryNode(String m_docName, int m_maxFreq, int m_numOfUniWords, String m_city, String maxFreqWord) {
        this.m_docName = m_docName;
        this.m_maxFreq = m_maxFreq;
        this.m_maxFreqWord = maxFreqWord;
        this.m_numOfUniWords = m_numOfUniWords;
        this.m_city = m_city;
    }

    public String getDocName() {
        return m_docName;
    }

    public int getMaxFreq() {
        return m_maxFreq;
    }

    public int getNumOfUniWords() {
        return m_numOfUniWords;
    }

    public String getCity() {
        return m_city;
    }

    @Override
    public int hashCode() {
        return m_docName.hashCode();
    }

    @Override
    public String toString() {
        return m_docName+"\t"+m_numOfUniWords+"\t"+m_maxFreq+"\t"+m_maxFreqWord+'\t'+m_city+"\n";
    }
}


