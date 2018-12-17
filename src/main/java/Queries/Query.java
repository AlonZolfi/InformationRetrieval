package Queries;

public class Query {
    private String m_num;
    private String m_title;
    private String m_description;
    private String m_narrative;

    public Query(String num, String title, String description, String narrative) {
        this.m_num = num;
        this.m_title = title;
        this.m_description = description;
        this.m_narrative = narrative;
    }

    public String getNum() {
        return m_num;
    }

    public String getTitle() {
        return m_title;
    }

    public String getDescription() {
        return m_description;
    }

    public String getNarrative() {
        return m_narrative;
    }
}
