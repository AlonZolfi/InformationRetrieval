package Model;

import java.io.*;
import java.util.*;
import java.util.HashMap;
import java.util.Observable;

import IO.ReadFile;
import IO.WriteFile;
import Index.CityInfoNode;
import Index.DocDictionaryNode;
import Index.InvertedIndex;
import Queries.ShowResultRecord;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.io.FileUtils;

public class Model extends Observable implements IModel {
    public static InvertedIndex invertedIndex;
    public static HashMap<String, DocDictionaryNode> documentDictionary;
    public static HashMap<String, CityInfoNode> cityDictionary;
    public static HashMap<String, CityInfoNode> usedCities;

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
            documentDictionary = new HashMap<>();
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
        boolean foundInvertedIndex = false, foundDocumentDictionary = false, foundCityDictionary = false;
        File dirSource = new File(path);
        File[] directoryListing = dirSource.listFiles();
        String[] update=new String[0];
        if (directoryListing != null && dirSource.isDirectory()) {
            for (File file : directoryListing) { // search for the relevant file
                if ((file.getName().equals("StemInvertedFile.txt") && stem)||(file.getName().equals("InvertedFile.txt"))&&!stem) {
                    invertedIndex = new InvertedIndex(file);
                    update = new String[]{"Successful","Dictionary was loaded successfully"};
                    foundInvertedIndex = true;
                }
                if ((file.getName().equals("StemDocumentDictionary.txt") && stem)||(file.getName().equals("DocumentDictionary.txt"))&&!stem) {
                    loadDocumentDictionary(file);
                    update = new String[]{"Successful","Dictionary was loaded successfully"};
                    foundDocumentDictionary = true;
                }
                if ((file.getName().equals("CityDictionary.txt"))) {
                    loadCityDictionary(path);
                    update = new String[]{"Successful","Dictionary was loaded successfully"};
                    foundCityDictionary = true;
                }
            }
            if(!foundInvertedIndex || !foundDocumentDictionary || !foundCityDictionary) {
                invertedIndex = null;
                documentDictionary = null;
                cityDictionary = null;
                update = new String[]{"Fail", "could not find dictionary"};
            }
        }
        else
            update =new String[] {"Fail","destination path is illegal or unreachable"};

        setChanged();
        notifyObservers(update);
    }

    public void loadDocumentDictionary(File file) {
        String line = null;
        documentDictionary = new HashMap<String, DocDictionaryNode>();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            line = bufferedReader.readLine();
            while(line != null) {
                String [] curLine = line.split("\t");
                //NEED TO SET PRIMARY WORDS FROM FILE TO DOC
                DocDictionaryNode cur = new DocDictionaryNode(curLine[0],Integer.parseInt(curLine[1]),Integer.parseInt(curLine[2]),curLine[3],curLine[4],Integer.parseInt(curLine[5]),null);
                documentDictionary.put(curLine[0],cur);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void getResults(String postingPath, String stopWordsPath, File queries, boolean stem){
        ReadFile.initStopWords(stopWordsPath+"\\stop_words.txt");
        Manager m = new Manager();
        HashMap<String, LinkedList<String>> results = m.calculateQueries(postingPath,queries,stem);
        resultsToObservableList(results);
    }

    public void getResults(String postingPath, String stopWordsPath, String query ,boolean stem){
        try {
            Random r = new Random();
            int queryNumber = Math.abs(r.nextInt(1000));
            File f = new File("tempquery.txt");
            FileWriter fw = new FileWriter(f);
            String sb = "<top>\n" +
                    "\n" +
                    "<num> Number: " + queryNumber + " \n" +
                    "<title> " + query + "  \n" +
                    "\n" +
                    "<desc> Description: \n" +
                    "\n" +
                    "\n" +
                    "<narr> Narrative: \n" +
                    "\n" +
                    ".</top>";
            fw.write(sb);
            fw.close();
            getResults(postingPath,stopWordsPath,f,stem);
            f.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resultsToObservableList(HashMap<String, LinkedList<String>> results) {
        ObservableList<ShowResultRecord> observableResult = FXCollections.observableArrayList();
        for (Map.Entry<String,LinkedList<String>> entry: results.entrySet())
            observableResult.add(new ShowResultRecord(entry.getKey(),entry.getValue()));
        setChanged();
        notifyObservers(observableResult);
    }

    public void loadCityDictionary(String destination){
        cityDictionary=new HashMap<>();
        try {
            FileReader fileReader = new FileReader(destination + "/CityDictionary.txt");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] words = line.split("\t");
                if (!words[4].equals("")) {
                    boolean isCapital = words[5].equals("true");
                    CityInfoNode i = new CityInfoNode(words[0],words[1],words[2],words[3],isCapital);
                    i.setPosting(words[4]);
                    cityDictionary.put(words[0],i);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * this function filler the resukte by the list of cities marked by the user
     * @param toFilter all the cities that are checked
     */
    public void filterCities(List<String> toFilter) {
        usedCities = new HashMap<>();
        if (toFilter.size() > 0) {
            for(String nameOfCity:cityDictionary.keySet()) {
                if(toFilter.contains(nameOfCity))
                    usedCities.put(nameOfCity,cityDictionary.get(nameOfCity));
            }
        }
    }
}
