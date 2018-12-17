package Model;

import java.io.File;

public interface IModel {

    void startIndexing(String pathOfDocs, String destinationPath, boolean stm);

    void startOver(String path);

    void showDictionary();

    void loadDictionary(String path, boolean stem);

    void getResults(String postingPath, File queries, boolean stem);

    void getResults(String postingPath, String query ,boolean stem);
}
