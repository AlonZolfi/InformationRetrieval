package Model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;

public class Model extends Observable implements IModel {
    private InvertedIndex invertedIndex;
    private LinkedList<DocDictionaryNode> documentDictionary;

    public void Parse(String pathOfDocs,String pathOfStopWords, boolean stm) {
        Manager man = new Manager();
        invertedIndex = new InvertedIndex();
        documentDictionary = new LinkedList<>();
        man.Manage(documentDictionary,invertedIndex,pathOfDocs,pathOfStopWords,"",stm);
    }


    public void onStartClick(String pathOfDocs, String pathOfStopWords, boolean stm){
        Parse(pathOfDocs,pathOfStopWords,stm);
    }

    public void onStartOverClick(String path) {

    }

}
