package Parse;
/*
this class represents a single document in the corpus containing all the data about the document
 */

public class CorpusDocument {
    private String m_fileName;
    private String m_docNum;
    private String m_docDate;
    private String m_docTitle;
    private String m_docText;
    private String m_docCity;
    private String m_docLang;

    public CorpusDocument(String fileName, String docNum, String docDate, String docTitle, String docText, String docCity, String docLang) {
        this.m_fileName = fileName;
        this.m_docNum = docNum;
        this.m_docDate = docDate;
        this.m_docTitle = docTitle;
        this.m_docText = docText;
        this.m_docCity = docCity;
        this.m_docLang = docLang;
    }

    public String getFileName() {
        return m_fileName;
    }

    public String getDocNum() {
        return m_docNum;
    }

    public String getDocDate() {
        return m_docDate;
    }

    public String getDocTitle() {
        return m_docTitle;
    }

    public String getDocText() {
        return m_docText;
    }

    public String getDocCity() {
        return m_docCity;
    }

    public String getDocLang(){
        return m_docLang;
    }
}
