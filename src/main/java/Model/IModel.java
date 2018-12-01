package Model;

import java.io.IOException;

public interface IModel {

    void onStartClick(String pathOfDocs, String pathOfStopWords, String destinationPath, boolean stm) throws IOException;

    void onStartOverClick(String path);

    void showDictionary();
}
