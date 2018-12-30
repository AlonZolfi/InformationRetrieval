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
import javafx.util.Pair;
import org.apache.commons.io.FileUtils;

public class Model extends Observable implements IModel {
    public static InvertedIndex invertedIndex;
    public static HashMap<String, DocDictionaryNode> documentDictionary;
    public static HashMap<String, CityInfoNode> cityDictionary;
    public static HashSet<String> languages;
    public static HashSet<String> stopWords;
    public static HashSet<String> usedCities;
    public static HashSet<String> usedLanguages;
    public HashMap<String,LinkedList<String>> m_results;
    private boolean dictionaryIsStemmed = false;

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
            if(stm)
                dictionaryIsStemmed =true;
            stopWords = ReadFile.initSet(paths[2]);
            invertedIndex = new InvertedIndex();
            documentDictionary = new HashMap<>();
            cityDictionary = new HashMap<>();
            languages = new HashSet<>();
            double[] results = new double[0];
            try {
                results = man.manage(cityDictionary,documentDictionary, invertedIndex,languages, paths[0], paths[1], stm);
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
        boolean foundInvertedIndex = false, foundDocumentDictionary = false, foundCityDictionary = false, foundLanguages = false;
        File dirSource = new File(path);
        File[] directoryListing = dirSource.listFiles();
        String[] update=new String[0];
        if (directoryListing != null && dirSource.isDirectory()) {
            for (File file : directoryListing) { // search for the relevant file
                if ((file.getName().equals("StemInvertedFile.txt") && stem)||(file.getName().equals("InvertedFile.txt"))&&!stem) {
                    if(stem)
                        dictionaryIsStemmed =true;
                    invertedIndex = new InvertedIndex(file);
                    foundInvertedIndex = true;
                }
                if ((file.getName().equals("StemDocumentDictionary.txt") && stem)||(file.getName().equals("DocumentDictionary.txt"))&&!stem) {
                    loadDocumentDictionary(file);
                    foundDocumentDictionary = true;
                }
                if ((file.getName().equals("CityDictionary.txt"))) {
                    loadCityDictionary(path);
                    foundCityDictionary = true;
                }
                if ((file.getName().equals("Languages.txt"))) {
                    languages = ReadFile.initSet(path+"/Languages.txt");
                    foundLanguages = true;
                }
            }
            if(!foundInvertedIndex || !foundDocumentDictionary || !foundCityDictionary ||!foundLanguages) {
                invertedIndex = null;
                documentDictionary = null;
                cityDictionary = null;
                update = new String[]{"Fail", "could not find one or more dictionaries"};
            }
            else
                update = new String[]{"Successful","Dictionary was loaded successfully"};
        }
        else
            update =new String[] {"Fail","destination path is illegal or unreachable"};

        setChanged();
        notifyObservers(update);
    }

    /**
     * loads the document dictionary
     * @param file the file of the doc dic
     */
    private void loadDocumentDictionary(File file) {
        String line = null;
        documentDictionary = new HashMap<String, DocDictionaryNode>();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            line = bufferedReader.readLine();
            Pair[] toFill;
            while(line != null) {
                String[] curLine = line.split("\t");
                if (curLine.length == 9) {
                    String[] data = curLine[8].split("#");
                    toFill = new Pair[data.length];
                    String[] words = new String[data.length];
                    String[] numbers = new String[data.length];
                    for (int i = 0; i < data.length; i++) {
                        String[] part = data[i].split("~");
                        words[i] = part[0];
                        numbers[i] = part[1];
                        toFill[i] = new Pair<String, Integer>(words[i], Integer.parseInt(numbers[i]));
                    }
                } else
                    toFill = new Pair[0];
                DocDictionaryNode cur = new DocDictionaryNode(curLine[0], Integer.parseInt(curLine[1]),curLine[2], Integer.parseInt(curLine[3]), curLine[4], curLine[5], Integer.parseInt(curLine[7]), curLine[6], toFill);
                documentDictionary.put(curLine[0], cur);

                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * loads the city dictionary
     * @param destination the file of city dic
     */
    private void loadCityDictionary(String destination){
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
        //write the languages to the disk
        Thread tLang = new Thread(() -> WriteFile.writeLanguages(destinationPath, languages));
        tLang.start();
        //wait for them to end
        try {
            tInvertedFile.join();
            tCity.join();
            tDocs.join();
            tLang.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * returns the result for a query file
     * @param postingPath postings path
     * @param queries queries file
     * @param stem should stem or not
     * @param semantics should improve with semantic or not
     * @param relevantCities list of cities
     * @param relevantLanguages list of languages
     */
    public void getResults(String postingPath, File queries, boolean stem, boolean semantics, List<String> relevantCities, List<String> relevantLanguages){
        if((stem && !dictionaryIsStemmed) || (!stem && dictionaryIsStemmed)){
            String[] update = {"Fail","could not search because of ambiguous stemming prefrences"};
            setChanged();
            notifyObservers(update);
            return;
        }
        filterCities(relevantCities);
        usedLanguages = new HashSet<>(relevantCities);
        Manager m = new Manager();
        HashMap<String, LinkedList<String>> results = m_results = m.calculateQueries(postingPath,queries,stem,semantics);
        usedCities = null;
        usedLanguages = null;
        resultsToObservableList(results);
    }

    /**
     * returns the result for a query file
     * @param postingPath postings path
     * @param query the query
     * @param stem should stem or not
     * @param semantics should improve with semantic or not
     * @param relevantCities list of cities
     * @param relevantLanguages list of languages
     */
    public void getResults(String postingPath, String query ,boolean stem, boolean semantics, List<String> relevantCities, List<String> relevantLanguages){
        try {
            Random r = new Random();
            int queryNumber = Math.abs(r.nextInt(899)+100);
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
            getResults(postingPath,f,stem, semantics,relevantCities,relevantLanguages);
            f.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * change results to observable list
     * @param results the result
     */
    private void resultsToObservableList(HashMap<String, LinkedList<String>> results) {
        ObservableList<ShowResultRecord> observableResult = FXCollections.observableArrayList();
        for (Map.Entry<String,LinkedList<String>> entry: results.entrySet())
            observableResult.add(new ShowResultRecord(entry.getKey(),entry.getValue()));
        setChanged();
        notifyObservers(observableResult);
    }

    /**
     * this function filler the result by the list of cities marked by the user
     * @param toFilter all the cities that are checked
     */
    public void filterCities(List<String> toFilter) {
        usedCities = new HashSet<>();
        if (toFilter.size() > 0) {
            for(String nameOfCity:cityDictionary.keySet()) {
                if(toFilter.contains(nameOfCity)) {
                    int space = nameOfCity.indexOf(" ");
                    if (space!=-1)
                        nameOfCity = nameOfCity.substring(0,space);
                    usedCities.add(nameOfCity);
                }
            }
        }
    }

    /**
     * get the 5 important entities
     * @param docName document name
     * @return 5 entities
     */
    @Override
    public String show5words(String docName) {
        if (documentDictionary.containsKey(docName)){
            try {
                return documentDictionary.get(docName).get5words();
            }catch(Exception e){
                System.out.println(docName);
            }
        }
        return "";
    }

    /**
     * returns a string builder with the results ready to be written to the disk
     * @return a string builder ready to be written
     */
    private StringBuilder results() {
        StringBuilder res = new StringBuilder();
        ArrayList<String> queryIDs= new ArrayList<>(m_results.keySet());
        queryIDs.sort(String.CASE_INSENSITIVE_ORDER);
        if (m_results != null)
            for (String m:queryIDs) {
                for (String doc : m_results.get(m)) {
                    String line = m + " 0 " + doc + " 0 0 ah\n";
                    res.append(line);
                }
            }
        return res;
    }

    /**
     * writes the results to the disk
     * @param dest destionatio of the write
     * @return true if worked
     */
    @Override
    public boolean writeRes(String dest) {
        FileWriter fileWriter = null;
        StringBuilder toWrite=results();
        try {
            if (m_results.size()>0) {
                fileWriter = new FileWriter(dest + "\\results.txt");
                fileWriter.write(toWrite.toString());
                fileWriter.close();
                toWrite.delete(0, toWrite.length());
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
