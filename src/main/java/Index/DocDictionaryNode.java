package Index;

import javafx.util.Pair;

public class DocDictionaryNode {
    private String m_docName; //doc name
    private int m_maxFreq; //the max freqenecy of a word existing in the doc
    private String m_docLang;
    private String m_maxFreqWord; // the word that exists most of the times
    private int m_numOfUniWords; //number of unique words in the document
    private String m_city; //city that represents the country the doc came from
    private int m_docLength;//length of the doc in words
    private String m_title;
    private Pair<String,Integer>[] m_primaryWords;

    public DocDictionaryNode(String docName, int maxFreq, String docLang, int numOfUniWords, String city, String maxFreqWord, int docLength, String title, Pair<String,Integer>[] primaryWords) {
        this.m_docName = docName;
        this.m_maxFreq = maxFreq;
        this.m_docLang = docLang;
        this.m_numOfUniWords = numOfUniWords;
        this.m_city = city;
        this.m_maxFreqWord = maxFreqWord;
        this.m_docLength = docLength;
        this.m_title = title;
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

    public String getTitle() {
        return m_title;
    }

    public int getDocLength() {
        return m_docLength;
    }

    public String getDocLang(){return m_docLang;}

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
        return m_docName+"\t"+m_maxFreq+"\t"+m_docLang+"\t"+m_numOfUniWords+'\t'+m_city+"\t"+m_maxFreqWord+"\t"+m_title+"\t"+m_docLength+"\t"+pw+"\n";

    }

    public String get5words() {
        StringBuilder s =new StringBuilder();
        for (int i = 0; i <m_primaryWords.length ; i++) {
            if(m_primaryWords[i]==null)
                break;
            s.append(m_primaryWords[i].getValue()).append("\t").append(m_primaryWords[i].getKey()).append("\n");
        }
        return s.toString();
    }
}


