package Queries;

/**
 * represents a query
 */
public class Query {
    private String m_num;//query num
    private String m_title; //actual query
    private String m_description;//description of query
    private String m_narrative;//narrative of the query

    public Query(String num, String title, String description, String narrative) {
        this.m_num = num;
        this.m_title = title;
        this.m_description = description;
        this.m_narrative = narrative;
    }

    public String getNum() {
        return m_num;
    }

    String getTitle() {
        return m_title;
    }

    String getDescription() {
        return m_description;
    }

    public String getNarrative() {
        return m_narrative;
    }
}
