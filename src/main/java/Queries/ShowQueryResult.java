package Queries;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ShowQueryResult {
    private StringProperty sp_docName;

    ShowQueryResult(String docName) {
        sp_docName = new SimpleStringProperty(docName);
    }

    public String getSp_docName() {
        return sp_docName.get();
    }

    public StringProperty sp_docNameProperty() {
        return sp_docName;
    }
}
