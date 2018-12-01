package Model;

import java.io.IOException;

public interface IModel {

    void onStartClick(String pathOfDocs, String destinationPath, boolean stm);

    void onStartOverClick(String path);

    void showDictionary();

    void loadDictionary(String path, boolean stem);
}
