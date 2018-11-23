package Model;

public class CorpusDocument {
    private String m_fileName;
    private String m_docNum;
    private String m_docDate;
    private String m_docTitle;
    private String m_docText;
    private String m_docCity;

    public CorpusDocument(String m_fileName, String m_docNum, String m_docDate, String m_docTitle, String m_docText, String m_docCity) {
        this.m_fileName = m_fileName;
        this.m_docNum = m_docNum;
        this.m_docDate = m_docDate;
        this.m_docTitle = m_docTitle;
        this.m_docText = m_docText;
        this.m_docCity = m_docCity;
    }

    public String getM_fileName() {
        return m_fileName;
    }

    public String getM_docNum() {
        return m_docNum;
    }

    public String getM_docDate() {
        return m_docDate;
    }

    public String getM_docTitle() {
        return m_docTitle;
    }

    public String getM_docText() {
        return m_docText;
    }

    public String getM_docCity() {
        return m_docCity;
    }
}
