package Index;

public class DocDictionaryNode {
    private String m_docName; //doc name
    private int m_maxFreq; //the max freqenecy of a word existing in the doc
    private String m_maxFreqWord; // the word that exists most of the times
    private int m_numOfUniWords; //number of unique words in the document
    private String m_city; //city that represents the country the doc came from

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


