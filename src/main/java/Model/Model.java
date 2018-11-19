package Model;

import java.util.Observable;

public class Model extends Observable implements IModel {

    public void Parse(String path) {
        ReadFile.readFiles(path);
    }


    public void onStartClick(String pathOfDocs,String pathOfStopWords,boolean stm){
        Parse(pathOfDocs);

    }

    public void onStartOverClick(String path) {

    }

}
