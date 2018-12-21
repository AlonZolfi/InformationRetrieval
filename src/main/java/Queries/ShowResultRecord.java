package Queries;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.LinkedList;

public class ShowResultRecord {
    private String queryID;
    private LinkedList<String> docNames;
    private StringProperty sp_queryID;
    private StringProperty sp_docNames;

    public ShowResultRecord(String queryID, LinkedList<String> docNames) {
        this.queryID = queryID;
        this.docNames = docNames;
        this.sp_queryID = new SimpleStringProperty(queryID);
        this.sp_docNames = new SimpleStringProperty(docNames.toString());
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
}
