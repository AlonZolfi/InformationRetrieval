package Model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;

import View.MyAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.apache.commons.io.FileUtils;

public class Model extends Observable implements IModel {
    private InvertedIndex invertedIndex;
    private LinkedList<DocDictionaryNode> documentDictionary;
    private HashMap<String,CityInfoNode> cityDictionary;
    @Override
    /*public void onStartClick(String pathOfDocs, String stopWordsPath,String destinationPath, boolean stm){
        Manager man = new Manager();
        invertedIndex = new InvertedIndex();
        documentDictionary = new LinkedList<>();
        double[] results = man.Manage(documentDictionary, invertedIndex, pathOfDocs, pathOfStopWords, "", stm);
        setChanged();
        notifyObservers(results);
    }*/

    public void onStartClick(String pathOfDocs,String destinationPath, boolean stm){
        String [] paths = pathsAreValid(pathOfDocs,destinationPath);
        if(paths!=null) {
            Manager man = new Manager();
            invertedIndex = new InvertedIndex();
            documentDictionary = new LinkedList<>();
            ReadFile.initStopWords(paths[2]);
            double[] results = man.Manage(documentDictionary, invertedIndex, paths[0], paths[1], destinationPath, stm);
            setChanged();
            notifyObservers(results);
        }
    }

    private String[] pathsAreValid(String pathOfDocs,String destinationPath) {
        String pathOfStopWords ="", corpusPath = "";
        File dirSource = new File(pathOfDocs);
        File[] directoryListing = dirSource.listFiles();
        if (directoryListing != null && dirSource.isDirectory()) {
            for (File file : directoryListing) {
                if (file.isDirectory())
                    corpusPath = file.getAbsolutePath();
                else
                    pathOfStopWords = file.getAbsolutePath();
            }
            if(corpusPath.equals("") || pathOfStopWords.equals("")){
                String[] update = {"Fail","contents of source path do not contain corpus folder or stop words file"};
                setChanged();
                notifyObservers(update);
                return null;
            }
        }
        else {
            String[] update = {"Fail","Source path is illegal or unreachable"};
            setChanged();
            notifyObservers(update);
            return null;
        }
        File dirDest = new File(destinationPath);
        if(!dirDest.isDirectory()){
            String[] update = {"Fail","Destination path is illegal or unreachable"};
            setChanged();
            notifyObservers(update);
            return null;
        }
        return new String[]{corpusPath,destinationPath,pathOfStopWords};
    }


    @Override
    public void onStartOverClick(String path) {
        File dir = new File(path);
        if(dir.isDirectory()){
            try {
                FileUtils.cleanDirectory(dir);
                setChanged();
                notifyObservers(new String[]{"Successful","The folder is clean now"});
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else {
            String[] update = {"Fail","Path given is not a directory or could not be reached"};
            setChanged();
            notifyObservers(update);
        }
    }

    @Override
    public void showDictionary() {
        if(invertedIndex==null){
            String[] update = {"Fail","Please load the dictionary first"};
            setChanged();
            notifyObservers(update);
        }
        else {
            ObservableList records = invertedIndex.getRecords();
            setChanged();
            notifyObservers(records);
        }
    }
}
