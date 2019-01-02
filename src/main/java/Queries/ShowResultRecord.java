package Queries;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.LinkedList;

public class ShowResultRecord {

    private String queryID;
    private LinkedList<ShowQueryResult> docNames;

    private StringProperty sp_queryID;
    private StringProperty sp_docNames;

    public ShowResultRecord(String queryID, LinkedList<String> docNames) {
        this.queryID = queryID;
        this.docNames = toQueryResultList(docNames);
        this.sp_queryID = new SimpleStringProperty(queryID);
        this.sp_docNames = new SimpleStringProperty(docNames.toString());
    }

    /**
     * returns list of QueryResult object
     * @param docNames document names
     * @return list of query result object
     */
    private LinkedList<ShowQueryResult> toQueryResultList(LinkedList<String> docNames) {
        LinkedList<ShowQueryResult> l = new LinkedList<>();
        for (String s:docNames) {
            l.add(new ShowQueryResult(s));
        }
        return l;
    }

    public String getSp_queryID() {
        return sp_queryID.get();
    }

    public StringProperty sp_queryIDProperty() {
        return sp_queryID;
    }

    public String getSp_docNames() {
        return sp_docNames.get();
    }

    public StringProperty sp_docNamesProperty() {
        return sp_docNames;
    }

    public LinkedList<ShowQueryResult> getDocNames() {
        return docNames;
    }
}
