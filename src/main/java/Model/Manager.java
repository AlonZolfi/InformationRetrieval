package Model;

import IO.CorpusDocument;
import IO.ReadFile;
import IO.WriteFile;
import Index.*;
import Parse.*;
import Web.APIRequest;
import Web.CitysMemoryDataBase;
import javafx.util.Pair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

class Manager {
    private AtomicInteger numOfPostings = new AtomicInteger(0);

    /**
     * This function manages the index process by separating it to a few bunches
     * @param cityDictionary - the city dictionary
     * @param documentDictionary - the document dictionary
     * @param invertedIndex - the inverted index
     * @param corpusPath - the path of the corpus
     * @param destinationPath - the path where the postings will be written
     * @param stem - if stemming should be done
     * @return returns data about the current run [num of documents, number of unique terms]
     * @throws Exception .
     */
    double[] manage(HashMap<String, CityInfoNode> cityDictionary, LinkedList<DocDictionaryNode> documentDictionary, InvertedIndex invertedIndex, String corpusPath, String destinationPath, boolean stem) throws Exception {

        CitysMemoryDataBase cityMemoryDataBaseRESTAPI = fillCityDataBase();
        int numOfDocs = 0;
        int numOfTempPostings = 900;
        LinkedList<Thread> tmpPostingThread = new LinkedList<>();

        for (int i = 0; i < numOfTempPostings; i++) {
            //read number of files
            LinkedList<CorpusDocument> l = ReadFile.readFiles(corpusPath, i, numOfTempPostings);

            //gather all the corpus documents together and parse them
            ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
            ConcurrentLinkedDeque<Future<MiniDictionary>> futureMiniDicList = new ConcurrentLinkedDeque<Future<MiniDictionary>>();
            //each document is being submitted to a new thread to be parsed
            for (CorpusDocument cd : l) {
                futureMiniDicList.add(pool.submit(new Parse(cd, stem)));
            }

            ConcurrentLinkedDeque<MiniDictionary> miniDicList = new ConcurrentLinkedDeque<>();
            for (Future<MiniDictionary> fMiniDic : futureMiniDicList) {
                miniDicList.add(fMiniDic.get());
                numOfDocs++; //counts all the documents we
            }

            //index together all the documents we've parsed in this iteration
            Indexer index = new Indexer(miniDicList);
            Future<HashMap<String, Pair<Integer, StringBuilder>>> futureTemporaryPosting = pool.submit(index);
            HashMap<String, Pair<Integer, StringBuilder>> temporaryPosting = futureTemporaryPosting.get();

            // Write the posting to the disk, then get the "link" of each word in list from the "WriteFile"
            Thread t1 = new Thread(() -> WriteFile.writeTempPosting(destinationPath, numOfPostings.getAndIncrement(), temporaryPosting));
            t1.start();
            tmpPostingThread.add(t1);

            //with all the information about the documents, fill the inverted index, doc dictionary and city dictionary
            fillCityData(miniDicList, cityDictionary, cityMemoryDataBaseRESTAPI, invertedIndex, documentDictionary);
            pool.shutdown();
        }

        //wait for all the temp postings to be written
        for (Thread t : tmpPostingThread)
            t.join();

        //merge all the temp postings
        mergePostings(invertedIndex, destinationPath, stem);

        //for each city in the city dictionary set pointer to the city name in the posting
        for (String word : cityDictionary.keySet()) {
            String pointer = invertedIndex.getPostingLink(word);
            if(pointer.equals("")) {
                pointer = invertedIndex.getPostingLink(word.toLowerCase());
                if(pointer.equals("") &&  word.indexOf(' ')!=-1) {
                    pointer = invertedIndex.getPostingLink(word.substring(0, word.indexOf(' ')));
                    if(pointer.equals(""))
                        pointer = invertedIndex.getPostingLink(word.toLowerCase().substring(0, word.indexOf(' ')));
                }
            }
            cityDictionary.get(word).setPosting(pointer);
        }

        return new double[]{numOfDocs, invertedIndex.getNumOfUniqueTerms()};
    }

    /**
     * approaches an api of cities to build a DB
     * @return the DB of cities
     */
    private CitysMemoryDataBase fillCityDataBase(){
        CitysMemoryDataBase citysMemoryDataBaseRESTAPI = null;
        try {
            citysMemoryDataBaseRESTAPI = new CitysMemoryDataBase("https://restcountries.eu/rest/v2/all?fields=name;capital;population;currencies");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return citysMemoryDataBaseRESTAPI;
    }

    /**
     * for every round of reading few files, this function polls data about the city the doc was retrieved, the data about the city
     * and updates inverted index about all the terms
     * @param miniDicList list of Mini Dictionaries
     * @param cityDictionary - the city of the
     * @param citysMemoryDataBaseRESTAPI - the city database
     * @param invertedIndex - the inverted index
     * @param documentDictionary - the document dictionary
     */
    private void fillCityData(ConcurrentLinkedDeque<MiniDictionary> miniDicList, HashMap<String, CityInfoNode> cityDictionary, CitysMemoryDataBase citysMemoryDataBaseRESTAPI, InvertedIndex invertedIndex, LinkedList<DocDictionaryNode> documentDictionary) {
        for (MiniDictionary mini : miniDicList) {
            String curCity = mini.getCity();
            StringBuilder cityTry = new StringBuilder();
            DocDictionaryNode cur=null;
            if (!curCity.equals("") && !cityDictionary.containsKey(curCity)) {
                String[] cityWords = curCity.split(" ");
                int j = 0;
                boolean found = false;
                while (j < cityWords.length && !found) {
                    cityTry.append(cityWords[j].toUpperCase());
                    if (!cityDictionary.containsKey(cityTry.toString())) {
                        CityInfoNode toPut = citysMemoryDataBaseRESTAPI.getCountryByCapital(cityTry.toString());
                        if (toPut != null) {
                            if (!cityDictionary.containsKey(cityTry.toString())) {
                                int idx = cityTry.toString().indexOf(" ");
                                if (idx!=-1)
                                    cityDictionary.put(cityTry.toString().substring(0,idx),toPut);
                                else
                                    cityDictionary.put(cityTry.toString(), toPut);
                                found = true;
                                cur = new DocDictionaryNode(mini.getName(), mini.getMaxFrequency(), mini.size(), cityTry.toString(), mini.getMaxFreqWord());
                            }
                        } else {
                            cityTry.append(" ");
                        }
                        j++;
                    } else {
                        found = true;
                        cur = new DocDictionaryNode(mini.getName(), mini.getMaxFrequency(), mini.size(), cityTry.toString(), mini.getMaxFreqWord());
                    }
                }
                if (!found) {
                    int space = curCity.indexOf(" ");
                    if (space != -1) {
                        curCity = curCity.substring(0, space);
                    }
                    if(!cityDictionary.containsKey(curCity)) {
                        String realCity = "";
                        String realCuntry = "";
                        String realCurancy = "";
                        String realPopulation = "";
                        try {
                            APIRequest request = new APIRequest();
                            JSONObject data = request.post("http://getcitydetails.geobytes.com/GetCityDetails?fqcn=" + curCity);
                            JSONObject result = data.getJSONObject("result");
                            realCity = result.get("geobytescity").toString();
                            realCuntry = result.get("geobytescountry").toString();
                            realCurancy = result.get("geobytescurrency").toString();
                            realPopulation = result.get("geobytespopulation").toString();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (!realPopulation.equals("")) {
                            Parse parse = new Parse();
                            realPopulation = parse.handleNumber(Integer.parseInt(realPopulation));
                        }
                        if (realCity.equals("")) {
                            realCity = curCity;
                        }
                        int idx = realCity.indexOf(" ");
                        String oneWordCity=realCity;
                        if(idx!=-1)
                            oneWordCity=realCity.substring(0,idx);
                        cityDictionary.put(oneWordCity.toUpperCase(), new CityInfoNode(realCity.toUpperCase(), realCuntry, realPopulation, realCurancy, false));
                    }
                    cur = new DocDictionaryNode(mini.getName(), mini.getMaxFrequency(), mini.size(), curCity, mini.getMaxFreqWord());
                }
            }
            cityTry.delete(0, cityTry.length());
            if(cur!=null)
                documentDictionary.add(cur);
            for (String word : mini.listOfWords()) {
                invertedIndex.addTerm(word);
            }
        }
    }

    /**
     * this function merges the temp postings to postings according to the letter of words
     * @param invertedIndex - the inverted index
     * @param tempPostingPath - the path of the temp postings
     * @param stem - if should be stemmed
     */
    private void mergePostings(InvertedIndex invertedIndex, String tempPostingPath,boolean stem){
        //save buffers for each temp file
        LinkedList<BufferedReader> bufferedReaderList = initiateBufferedReaderList(tempPostingPath);
        //download all the first sentences of each file
        String[] firstSentenceOfFile = initiateMergingArray(bufferedReaderList);
        char postingNum = '`';
        HashMap<String, StringBuilder> writeToPosting = new HashMap<>();
        //separate the name of the file to be with stem or not
        String fileName = tempPostingPath+"\\finalPostingStem";
        if (!stem)
            fileName= tempPostingPath+"\\finalPosting";
        do {
            int numOfAppearances = 0;
            StringBuilder finalPostingLine = new StringBuilder(); //current posting line
            String minTerm = ""+(char)127;
            String[] saveSentences = new String[firstSentenceOfFile.length];
            //go throw all the lines currently in the array to merge them together if a certain term exists in more than 1 file
            for (int i = 0; i < firstSentenceOfFile.length; i++) {
                if(firstSentenceOfFile[i]!=null && !firstSentenceOfFile[i].equals("")) {
                    String[] termAndData = firstSentenceOfFile[i].split("~");
                    int result = termAndData[0].compareToIgnoreCase(minTerm);
                    if (result == 0) { // if it is the same term add his posting data to the old term
                        if (Character.isLowerCase(termAndData[0].charAt(0)))
                            finalPostingLine.replace(0, termAndData[0].length(), termAndData[0].toLowerCase());
                        finalPostingLine.append(termAndData[2]);
                        firstSentenceOfFile[i] = null;
                        saveSentences[i] = termAndData[0] + "~" + termAndData[1] + "~" + termAndData[2];
                        numOfAppearances += Integer.parseInt(termAndData[1]);
                    } else if (result < 0) { // if it is more lexi smaller than min term than it is time to take care of it
                        minTerm = termAndData[0];
                        finalPostingLine.delete(0, finalPostingLine.length());
                        finalPostingLine.append(termAndData[0]).append("~").append(termAndData[2]);
                        firstSentenceOfFile[i] = null;
                        saveSentences[i] = termAndData[0]+"~"+termAndData[1]+"~"+termAndData[2];
                        numOfAppearances = Integer.parseInt(termAndData[1]);
                    }
                }
            }
            //restore all the lines that were deleted (because they weren't the minimal term)
            restoreSentence(bufferedReaderList,minTerm,firstSentenceOfFile,saveSentences);
            finalPostingLine.append("\t").append(numOfAppearances);
            if(minTerm.toLowerCase().charAt(0)>postingNum) { //write the current posting to the disk once a term with higher first letter has riched
                writeFinalPosting(writeToPosting,invertedIndex,fileName,postingNum);
                writeToPosting = new HashMap<>();
                postingNum++;
            }
            //merge terms that appeared in different case
            lookForSameTerm(finalPostingLine.toString().split("~")[0],finalPostingLine,writeToPosting);
        } while(readingIsDone(firstSentenceOfFile) && postingNum<'z'+1);
        writeFinalPosting(writeToPosting,invertedIndex,fileName,'z');

        invertedIndex.deleteEntriesOfIrrelevant();
        closeAllFiles(bufferedReaderList);
        deleteTempFiles(tempPostingPath);
    }

    /**
     * write the data of a current letter to the disk
     * @param writeToPosting - what should be written
     * @param invertedIndex - the inverted index
     * @param fileName - the file name as it should be written
     * @param postingNum - the header to the posting num indicating what letter is it
     */
    private void writeFinalPosting(HashMap<String, StringBuilder> writeToPosting, InvertedIndex invertedIndex, String fileName, char postingNum) {
        List<String> keys = new LinkedList<String>(writeToPosting.keySet());
        int k = 0;
        //set the pointers in the inverted index for each term
        for (String word0: keys){
            String toNum=writeToPosting.get(word0).toString().split("\t")[1];
            int num = Integer.parseInt(toNum);
            invertedIndex.setPointer(word0, k++);
            invertedIndex.setNumOfAppearance(word0,num);
        }
        final HashMap<String, StringBuilder> sendToThread = new HashMap<>(writeToPosting);
        String file = fileName + "_"+ postingNum + ".txt";
        new Thread(()->WriteFile.writeFinalPosting(file, sendToThread)).start();
    }

    /**
     * closes all the buffered readers of the temp postings
     * @param bufferedReaderList the list of the files
     */
    private void closeAllFiles(LinkedList<BufferedReader> bufferedReaderList) {
        for (BufferedReader bf: bufferedReaderList) {
            try {
                bf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * searches if a term has appeared in a different case
     * @param minTerm the term
     * @param finalPostingLine term's final posting
     * @param writeToPosting - the collection of the final posting
     */
    private void lookForSameTerm(String minTerm, StringBuilder finalPostingLine, HashMap<String, StringBuilder> writeToPosting) {
        boolean option1 = writeToPosting.containsKey(minTerm.toUpperCase());
        boolean option2 = writeToPosting.containsKey(minTerm.toLowerCase());
        boolean option3 = writeToPosting.containsKey(minTerm);
        String replace;
        if (option1) { //if term appeared in upper case
            //remove the current appearance of the term
            if (Character.isLowerCase(minTerm.charAt(0))) {
                replace = writeToPosting.remove(minTerm.toUpperCase()).toString();
                minTerm = minTerm.toLowerCase();
            } else {
                replace = writeToPosting.remove(minTerm.toUpperCase()).toString();
            }
            //update the posting with old and new data
            String[] separatePostingAndNumOld = replace.split("\t");
            String[] separatePostingAndNumNew = finalPostingLine.toString().split("\t");
            int numOfAppearance = Integer.parseInt(separatePostingAndNumOld[1]) + Integer.parseInt(separatePostingAndNumNew[1]);
            String oldPosting = separatePostingAndNumOld[0].substring(separatePostingAndNumOld[0].indexOf("~") + 1);
            String newPosting = separatePostingAndNumNew[0].substring(separatePostingAndNumNew[0].indexOf("~") + 1);
            StringBuilder allTogether = new StringBuilder(minTerm + "~" + oldPosting + newPosting + "\t" + numOfAppearance);
            writeToPosting.put(minTerm, allTogether);
        }
        else if (option3 || option2) { //if term appeared in lower case
            if(option2)
                minTerm = minTerm.toLowerCase();
            //update the posting with old and new data
            replace = writeToPosting.get(minTerm).toString();
            String[] separatePostingAndNumOld = replace.split("\t");
            String[] separatePostingAndNumNew = finalPostingLine.toString().split("\t");
            int numOfAppearance = Integer.parseInt(separatePostingAndNumOld[1]) + Integer.parseInt(separatePostingAndNumNew[1]);
            String oldPosting = separatePostingAndNumOld[0].substring(separatePostingAndNumOld[0].indexOf("~") + 1);
            String newPosting = separatePostingAndNumNew[0].substring(separatePostingAndNumNew[0].indexOf("~") + 1);
            StringBuilder allTogether = new StringBuilder(minTerm + "~" + oldPosting + newPosting + "\t" + numOfAppearance);
            writeToPosting.replace(minTerm, allTogether);
        }
        else {
            writeToPosting.put(minTerm, finalPostingLine);
        }
    }

    /**
     * restores the lines that were deleted during merge because a smaller case has appeared
     * @param bufferedReaderList bf list
     * @param minTerm the current term
     * @param firstSentenceOfFile array of current lines
     * @param saveSentences the array that saves all the lines
     */
    private void restoreSentence(LinkedList<BufferedReader> bufferedReaderList,String minTerm,String[] firstSentenceOfFile, String[] saveSentences){
        for (int i = 0; i < saveSentences.length; i++) {
            if (saveSentences[i] != null) {
                String[] termAndData = saveSentences[i].split("~");
                if (termAndData[0].compareToIgnoreCase(minTerm)!=0) {
                    firstSentenceOfFile[i] = termAndData[0]+"~"+termAndData[1]+"~"+termAndData[2];
                }
                else
                    firstSentenceOfFile[i] = getNextSentence(bufferedReaderList.get(i));;
            }
        }
    }

    /**
     * checks if the reading from the files is done
     * @param firstSentenceOfFile lines from the
     * @return returns true if reading should continue, or false if reading is done
     */
    private boolean readingIsDone(String[] firstSentenceOfFile) {
        for (String sentence: firstSentenceOfFile) {
            if(sentence!=null)
                return true;
        }
        return false;
    }

    /**
     * retrieves the next sentence of a file
     * @param bf the buffered reader of the file
     * @return returns the next sentence or null if there is no further reading
     */
    private String getNextSentence(BufferedReader bf){
        String line = null;
        try {
            if((line= bf.readLine())!=null) {
                return line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    /**
     * initiates an array of all first sentences in all files
     * @param bufferedReaderList the list of buffers
     * @return an array filled with lines
     */
    private String[] initiateMergingArray(LinkedList<BufferedReader> bufferedReaderList){
        String[] firstSentenceOfFile = new String[bufferedReaderList.size()];
        int i = 0;
        for (BufferedReader bf: bufferedReaderList) {
            String line = getNextSentence(bf);
            if(line!= null) {
                firstSentenceOfFile[i]= line;
            }
            i++;
        }
        return firstSentenceOfFile;
    }

    /**
     * initiates the buffered readers of all files
     * @param tempPostingPath the path of the temp postings
     * @return returns a list of all buffers
     */
    private LinkedList<BufferedReader> initiateBufferedReaderList(String tempPostingPath){
        File dirSource = new File(tempPostingPath);
        File[] directoryListing = dirSource.listFiles();
        LinkedList<BufferedReader> bufferedReaderList = new LinkedList<>();
        if (directoryListing != null && dirSource.isDirectory()) {
            for (File file : directoryListing) {
                if (file.getName().startsWith("posting")) {
                    try {
                        bufferedReaderList.add(new BufferedReader(new FileReader(file)));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return bufferedReaderList;
    }

    /**
     * deletes all the temporary postings
     * @param destPath the path of the temp postings
     */
    private void deleteTempFiles(String destPath){
        File dirSource = new File(destPath);
        File[] directoryListing = dirSource.listFiles();
        if (directoryListing != null && dirSource.isDirectory()) {
            for (File file : directoryListing) {
                if (file.getName().startsWith("posting")) {
                    file.delete();
                }
            }
        }
    }
}