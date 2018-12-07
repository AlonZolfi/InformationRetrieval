package Model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;

import IO.ReadFile;
import IO.WriteFile;
import Index.CityInfoNode;
import Index.DocDictionaryNode;
import Index.InvertedIndex;
import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;

public class Model extends Observable implements IModel {
    private InvertedIndex invertedIndex;
    private LinkedList<DocDictionaryNode> documentDictionary;
    private HashMap<String, CityInfoNode> cityDictionary;

    /**
     * starts the index process
     * @param pathOfDocs - path of the corpus and stop words
     * @param destinationPath - path where the posting and other data should be written
     * @param stm - if stemming should be done
     */
    @Override
    public void startIndexing(String pathOfDocs, String destinationPath, boolean stm){
        String [] paths = pathsAreValid(pathOfDocs,destinationPath); // checks if the paths entered are valid
        if(paths!=null) {
            double start = System.currentTimeMillis();
            Manager man = new Manager();
            ReadFile.initStopWords(paths[2]);
            invertedIndex = new InvertedIndex();
            documentDictionary = new LinkedList<>();
            cityDictionary = new HashMap<>();
            double[] results = new double[0];
            try {
                results = man.manage(cityDictionary,documentDictionary, invertedIndex, paths[0], paths[1], stm);
                writeDictionariesToDisk(destinationPath,stm);
            } catch (Exception e) {
                String[] update = {"Fail","Indexing failed"};
                setChanged();
                notifyObservers(update);
            }
            double[] totalResults = new double[]{results[0],results[1],(System.currentTimeMillis()-start)/60000};
            setChanged();
            notifyObservers(totalResults);
        }
    }

    /**
     * deletes all the contents of the path given
     * @param path the path where all the details should be deleted
     */
    @Override
    public void startOver(String path) {
        File dir = new File(path);
        String[] update;
        if(dir.isDirectory()){
            try {
                FileUtils.cleanDirectory(dir);//delete all the files in the directory
                update = new String[]{"Successful","The folder is clean now"};
            } catch (IOException e) {
                e.printStackTrace();
                update = new String[]{"Fail","Cleaning the folder was unsuccessful"};
            }
        }
        else {
            update = new String[]{"Fail","Path given is not a directory or could not be reached"};
        }
        setChanged();
        notifyObservers(update);
    }

    /**
     * loads the inverted index and notifies the view model
     */
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

    /**
     * loads the dictionary that is present in the path given according to the stem sign
     * @param path - the path to load from
     * @param stem - dictionary with stem or without
     */
    @Override
    public void loadDictionary(String path, boolean stem) {
        boolean found = false;
        File dirSource = new File(path);
        File[] directoryListing = dirSource.listFiles();
        String[] update=new String[0];
        if (directoryListing != null && dirSource.isDirectory()) {
            for (File file : directoryListing) { // search for the relevant file
                if ((file.getName().equals("StemInvertedFile.txt") && stem)||(file.getName().equals("InvertedFile.txt"))&&!stem) {
                    invertedIndex = new InvertedIndex(file);
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

    /**
     * this function checks if all the paths are valid
     * @param pathOfDocs - path of the corpus and stop words
     * @param destinationPath - path where the postings and other data should be written
     * @return returns true if all the paths are valid or raises a flag that something is wrong
     */
    private String[] pathsAreValid(String pathOfDocs,String destinationPath) {
        String pathOfStopWords ="", corpusPath = pathOfDocs;
        File dirSource = new File(pathOfDocs);
        File[] directoryListing = dirSource.listFiles();
        if (directoryListing != null && dirSource.isDirectory()) {
            for (File file : directoryListing) {
                if (file.isFile() && file.getName().equalsIgnoreCase("stop_words.txt"))
                    pathOfStopWords = file.getAbsolutePath();
            }
            if(pathOfStopWords.equals("")){
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

    /**
     * writes all the dictionaries to the disk
     * @param destinationPath - the path of the files to be written
     * @param stem - if should be stemmed
     */
    private void writeDictionariesToDisk(String destinationPath, boolean stem) {
        //write the inverted file to the disk
        Thread tInvertedFile = new Thread(()->WriteFile.writeInvertedFile(destinationPath, invertedIndex, stem));
        tInvertedFile.start();
        //write the doc dictionary to the disk
        Thread tCity = new Thread(() -> WriteFile.writeDocDictionary(destinationPath, documentDictionary, stem));
        tCity.start();
        //write the city dictionary to the disk
        Thread tDocs = new Thread(() -> WriteFile.writeCityDictionary(destinationPath, cityDictionary));
        tDocs.start();
        //wait for them to end
        try {
            tInvertedFile.join();
            tCity.join();
            tDocs.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
