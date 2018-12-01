package Model;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Observable;

import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;

public class Model extends Observable implements IModel {
    private InvertedIndex invertedIndex;
    private LinkedList<DocDictionaryNode> documentDictionary;

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
        String[] update;
        if(dir.isDirectory()){
            try {
                FileUtils.cleanDirectory(dir);
                update = new String[]{"Successful","The folder is clean now"};
            } catch (IOException e) {
                update = new String[]{"Fail","Cleaning the folder was unsuccessful"};
            }
        }
        else {
            update = new String[]{"Fail","Path given is not a directory or could not be reached"};
        }
        setChanged();
        notifyObservers(update);
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

    @Override
    public void loadDictionary(String path, boolean stem) {
        boolean found = false;
        File dirSource = new File(path);
        File[] directoryListing = dirSource.listFiles();
        String[] update=new String[0];
        if (directoryListing != null && dirSource.isDirectory()) {
            for (File file : directoryListing) {
                if ((file.getName().equals("invertedIndexWithStem") && stem)||(file.getName().equals("invertedIndexWithoutStem"))&&!stem) {
                    invertedIndex.loadDictionary(file);
                    update = new String[]{"Successful","Dictionary was loaded successfully"};
                    found = true;
                }
            }
            if(!found)
                update =new String[] {"Fail","could not find dictionary"};
        }
        else
            update =new String[] {"Fail","destination path is illegal or unreachable"};

        setChanged();
        notifyObservers(update);
    }
}
