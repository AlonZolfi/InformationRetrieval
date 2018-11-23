package Model;

import java.util.Observable;

public class Model extends Observable implements IModel {

    public void Parse(String pathOfDocs,String pathOfStopWords, boolean stm) {
        Manager man = new Manager();
        man.Manage(pathOfDocs,pathOfStopWords,"",stm);
    }


    public void onStartClick(String pathOfDocs, String pathOfStopWords, boolean stm){
        Parse(pathOfDocs,pathOfStopWords,stm);
    }

    public void onStartOverClick(String path) {

    }

}
