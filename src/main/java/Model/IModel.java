package Model;

public interface IModel {

    void onStartClick(String pathOfDocs, String destinationPath, boolean stm);

    void onStartOverClick(String path);

    void showDictionary();
}
