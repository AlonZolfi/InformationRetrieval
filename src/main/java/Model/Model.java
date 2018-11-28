package Model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;

import javafx.scene.control.Alert;
import org.apache.commons.io.FileUtils;

public class Model extends Observable implements IModel {
    private InvertedIndex invertedIndex;
    private LinkedList<DocDictionaryNode> documentDictionary;

    public void Parse(String pathOfDocs,String pathOfStopWords, String destinationPath, boolean stm) {
        Manager man = new Manager();
        invertedIndex = new InvertedIndex();
        documentDictionary = new LinkedList<>();
        man.Manage(documentDictionary,invertedIndex,pathOfDocs,pathOfStopWords,"",stm);
    }


    public void onStartClick(String pathOfDocs, String pathOfStopWords,String destinationPath, boolean stm){
        Parse(pathOfDocs,pathOfStopWords,destinationPath,stm);
    }

    public void onStartOverClick(String path) {
        File dir = new File(path);
        if(dir.isDirectory()){
            try {
                FileUtils.cleanDirectory(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            String[] update = new String[2];
            update[0] = "RaiseAlert";
            update[1] = "Path give is not a directory or could not be reached";
            setChanged();
            notifyObservers(update);
        }
    }

}
