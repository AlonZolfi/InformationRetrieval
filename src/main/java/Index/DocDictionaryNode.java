package Index;

import javafx.util.Pair;

public class DocDictionaryNode {
    private String m_docName; //doc name
    private int m_maxFreq; //the max freqenecy of a word existing in the doc
    private String m_maxFreqWord; // the word that exists most of the times
    private int m_numOfUniWords; //number of unique words in the document
    private String m_city; //city that represents the country the doc came from
    private int m_docLength;//length of the doc in words
    private Pair<String,Integer>[] m_primaryWords;

    public DocDictionaryNode(String m_docName, int m_maxFreq, int m_numOfUniWords, String m_city, String maxFreqWord, int docLength, Pair<String,Integer>[] primaryWords) {
        this.m_docName = m_docName;
        this.m_maxFreq = m_maxFreq;
        this.m_maxFreqWord = maxFreqWord;
        this.m_numOfUniWords = m_numOfUniWords;
        this.m_city = m_city;
        this.m_docLength = docLength;
        this.m_primaryWords = primaryWords;
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

    public int getDocLength() {
        return m_docLength;
    }

    @Override
    public int hashCode() {
        return m_docName.hashCode();
    }

    @Override
    public String toString() {
        String pw = "";
        if (m_primaryWords!=null) {
            for (int i = 0; i < m_primaryWords.length - 1; i++) {
                if (m_primaryWords[i] != null)
                    pw += m_primaryWords[i].getKey() + "~" + m_primaryWords[i].getValue() + "#";
            }
            if (m_primaryWords[m_primaryWords.length - 1] != null)
                pw += m_primaryWords[m_primaryWords.length - 1].getKey() + "~" + m_primaryWords[m_primaryWords.length - 1].getValue();
        }
        return m_docName+"\t"+m_numOfUniWords+"\t"+m_maxFreq+"\t"+m_maxFreqWord+'\t'+m_city+"\t"+m_docLength+"\t"+pw+"\n";

    }

    public String get5words() {
        String s ="";
        for (int i = 0; i <m_primaryWords.length ; i++) {
            s += m_primaryWords[i].getValue()+"\t"+m_primaryWords[i].getKey()+"\n";
        }
        return s;
    }
}


