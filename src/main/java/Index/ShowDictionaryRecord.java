package Index;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ShowDictionaryRecord {
    private String term;
    private String count;
    private StringProperty termProperty;
    private StringProperty countProperty;

    public ShowDictionaryRecord(String term, String count) {
        this.term = term;
        this.count = count;
        this.termProperty = new SimpleStringProperty(term);
        this.countProperty = new SimpleStringProperty(count);
    }

    public StringProperty getTermProperty() {
        return termProperty;
    }


    public StringProperty getCountProperty() {
        return countProperty;
    }
}
