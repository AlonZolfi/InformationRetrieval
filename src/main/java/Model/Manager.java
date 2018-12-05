package Model;

import IO.CorpusDocument;
import IO.ReadFile;
import IO.WriteFile;
import Index.CityInfoNode;
import Index.DocDictionaryNode;
import Index.Indexer;
import Index.InvertedIndex;
import Parse.*;
import Web.CitysMemoryDataBase;
import javafx.util.Pair;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.*;

public class Manager {
    /*private final int NUM_OF = 5;
    public static Semaphore fullCorpusDocSemaphore = new Semaphore(0);
    public static Semaphore emptyCorpusDocSemaphore = new Semaphore(5);
    public static Semaphore fullMiniDicSemaphore = new Semaphore(0);
    public static Semaphore emptyMiniDicSemaphore = new Semaphore(5);
    public static ConcurrentLinkedDeque<Future<LinkedList<CorpusDocument>>> corpusDocQueue = new ConcurrentLinkedDeque<Future<LinkedList<CorpusDocument>>>();
    public static ConcurrentLinkedDeque<LinkedList<MiniDictionary>> miniDicQueue = new ConcurrentLinkedDeque<LinkedList<MiniDictionary>>();
    private int numOfDocs = 0;*/
    private int numOfPostings = 0;

    /*public double[] Manage(LinkedList<DocDictionaryNode> documentDictionary, InvertedIndex invertedIndex, String corpusPath, String stopWordsPath, String destinationPath, boolean stem) {

        double start = System.currentTimeMillis();

        new Thread(() -> ReadFile.readFiles(corpusPath)).start();
        new Thread(() -> Parse(stem)).start();
        new Thread(() -> indexAndWriteTemporaryPosting(destinationPath, invertedIndex)).start();

        return new double[]{numOfDocs, invertedIndex.getNumOfUniqueTerms(), (System.currentTimeMillis() - start) / 60000};

    }

    private void Parse(boolean stem) {
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        //CONSUME
        LinkedList<MiniDictionary> listOfMiniDics = new LinkedList<MiniDictionary>();
        try {
            fullCorpusDocSemaphore.acquire();

            LinkedList<CorpusDocument> docsOfOneFile = corpusDocQueue.poll().get();
            LinkedList<Future<MiniDictionary>> futureListOfMiniDics = new LinkedList<Future<MiniDictionary>>();
            emptyCorpusDocSemaphore.release();
            for (CorpusDocument cd : docsOfOneFile) {
                numOfDocs++;
                futureListOfMiniDics.add(pool.submit(new Parse(cd, stem)));
            }

            for (Future<MiniDictionary> f : futureListOfMiniDics) {
                try {
                    listOfMiniDics.add(f.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        //PRODUCE
        try {
            emptyMiniDicSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        miniDicQueue.add(listOfMiniDics);
        fullMiniDicSemaphore.release();
        pool.shutdown();
    }


    private void indexAndWriteTemporaryPosting(String destinationPath, InvertedIndex invertedIndex) {
        //CONSUME

        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        LinkedList<MiniDictionary> bulkToIndex = new LinkedList<MiniDictionary>();
        try {
            fullMiniDicSemaphore.acquire();
            bulkToIndex.addAll(miniDicQueue.poll());
            emptyMiniDicSemaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Indexer index = new Indexer(new ConcurrentLinkedDeque(bulkToIndex));

        Future<HashMap<String, StringBuilder>> futureTemporaryPosting = pool.submit(index);

        HashMap<String, StringBuilder> temporaryPosting = null;
        try {
            temporaryPosting = futureTemporaryPosting.get();
            //first Write the posting to the disk, thene get the "link" of hitch word in list from the "WriteFile"
            WriteFile.writeToDest(destinationPath, numOfPostings++, temporaryPosting);
            //second fill the InvertedIndex with words and linkes
            for (MiniDictionary mini : bulkToIndex) {
                for (String word : mini.listOfWords()) {
                    invertedIndex.addTerm(word);
                }
            }

        } catch (InterruptedException | ExecutionException | IOException e) {
            e.printStackTrace();
        }
        pool.shutdown();
    }*/


    public double[] Manage(HashMap<String, CityInfoNode> cityDictionary, LinkedList<DocDictionaryNode> documentDictionary, InvertedIndex invertedIndex, String corpusPath, String destinationPath, boolean stem) {
        CitysMemoryDataBase citysMemoryDataBaseRESTAPI = null;
        try {
            citysMemoryDataBaseRESTAPI = new CitysMemoryDataBase("https://restcountries.eu/rest/v2/all?fields=name;capital;population;currencies");
        } catch (IOException e) {
            e.printStackTrace();
        }

        int numOfDocs = 0;
        double start = System.currentTimeMillis();
        int iter = 3;
        for (int i = 0; i < iter; i++) {
            LinkedList<CorpusDocument> l = ReadFile.readFiles(corpusPath, i, iter);
            ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
            ConcurrentLinkedDeque<Future<MiniDictionary>> futureMiniDicList = new ConcurrentLinkedDeque<Future<MiniDictionary>>();
            for (CorpusDocument cd : l) {
                futureMiniDicList.add(pool.submit(new Parse(cd, stem)));
            }
            ConcurrentLinkedDeque<MiniDictionary> miniDicList = new ConcurrentLinkedDeque<>();
            for (Future<MiniDictionary> fMiniDic : futureMiniDicList) {
                try {
                    miniDicList.add(fMiniDic.get());
                    numOfDocs++;
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            Indexer index = new Indexer(miniDicList);
            Future<HashMap<String, Pair<Integer,StringBuilder>>> futureTemporaryPosting = pool.submit(index);
            try {
                HashMap<String, Pair<Integer,StringBuilder>> temporaryPosting = futureTemporaryPosting.get();
                //first Write the posting to the disk, then get the "link" of each word in list from the "WriteFile"
                new Thread(()-> WriteFile.writeTmpPosting(destinationPath, numOfPostings++, temporaryPosting)).start();
                //second fill the InvertedIndex with words and linkes
                for (MiniDictionary mini : miniDicList) {
                    String curCity = mini.getCity();
                    StringBuilder cityTry = new StringBuilder();
                    if (!curCity.equals("") && !cityDictionary.containsKey(curCity)) {
                        String[] cityWords = curCity.split(" ");
                        int j = 0;
                        boolean found = false;
                        while (j < cityWords.length  && !found) {
                            if (!cityDictionary.containsKey(cityTry.toString())) {
                                cityTry.append(cityWords[j]);
                                CityInfoNode toPut = citysMemoryDataBaseRESTAPI.getCountryByCapital(cityTry.toString());
                                if (toPut != null) {
                                    if (!cityDictionary.containsKey(cityTry.toString())) {
                                        cityDictionary.put(cityTry.toString(), toPut);
                                        found = true;
                                    }
                                } else cityTry.append(" ");
                                j++;
                            }
                            else
                                found=true;
                        }
                        if(!found)
                            cityTry = new StringBuilder();
                        DocDictionaryNode cur = new DocDictionaryNode(mini.getName(),mini.getMaxFrequency(),mini.size(),cityTry.toString());
                        documentDictionary.add(cur);
                    }
                    for (String word : mini.listOfWords()) {
                        invertedIndex.addTerm(word);
                    }
                    System.out.println(invertedIndex.getNumOfUniqueTerms() +"hjhjhj" );
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            pool.shutdown();
        }

        Thread tCity = new Thread(()->WriteFile.writeDocDictionary(destinationPath,documentDictionary,stem));
        tCity.start();
        Thread tDocs = new Thread(()->WriteFile.writeCityDictionary(destinationPath,cityDictionary));
        tDocs.start();
        try {
            tCity.join();
            tDocs.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //mergePostings(invertedIndex,destinationPath,stem);
        WriteFile.writeInvertedFile(destinationPath,invertedIndex,stem);
        return new double[]{numOfDocs,invertedIndex.getNumOfUniqueTerms(),(System.currentTimeMillis()-start)/60000};
    }

    /*private void mergePostings(InvertedIndex invertedIndex, String tempPostingPath,boolean stem){

        LinkedList<BufferedReader> bufferedReaderList = initiateBufferedReaderList(tempPostingPath);
        String[] firstSentenceOfFile = initiateMergingArray(bufferedReaderList);
        char postingNum = '`';
        LinkedList<StringBuilder> writeToPosting = new LinkedList<>();
        String fileName = "Stem"+tempPostingPath+"/finalPosting";
        if (!stem)
            fileName= tempPostingPath+"/finalPosting";
        do {
            int numOfAppearances = 0;
            StringBuilder finalPostingLine = new StringBuilder();
            String minTerm = ""+(char)127;
            String[] saveSentences = new String[firstSentenceOfFile.length];
            for (int i = 0; i < firstSentenceOfFile.length; i++) {
                if(firstSentenceOfFile[i]!=null && !firstSentenceOfFile[i].equals("")) {
                    String[] termAndData = firstSentenceOfFile[i].split("~");
                    int result = termAndData[0].compareToIgnoreCase(minTerm);
                    if (result == 0) {
                        finalPostingLine.append(termAndData[2]);
                        firstSentenceOfFile[i] = null;
                        saveSentences[i] = termAndData[0]+"~"+termAndData[1]+"~"+termAndData[2];
                        numOfAppearances += Integer.parseInt(termAndData[1]);
                    } else if (result < 0) {
                        minTerm = termAndData[0];
                        finalPostingLine.delete(0, finalPostingLine.length());
                        finalPostingLine.append(termAndData[0]).append("~").append(termAndData[2]);
                        firstSentenceOfFile[i] = null;
                        saveSentences[i] = termAndData[0]+"~"+termAndData[1]+"~"+termAndData[2];
                        numOfAppearances = Integer.parseInt(termAndData[1]);
                    }
                }
            }
            for (int i = 0; i < saveSentences.length; i++) {
                if (saveSentences[i] != null) {
                    String[] termAndData = saveSentences[i].split("~");
                    if (!termAndData[0].equals(minTerm)) {
                        firstSentenceOfFile[i] = termAndData[0]+"~"+termAndData[1]+"~"+termAndData[2];
                    }
                    else
                        firstSentenceOfFile[i] = getNextSentence(bufferedReaderList.get(i));
                }
            }
            if(!finalPostingLine.toString().equals("")) {
                invertedIndex.setPointer(minTerm, fileName, writeToPosting.size());
                invertedIndex.setNumOfAppearance(minTerm,numOfAppearances);
            }
            if(minTerm.toLowerCase().charAt(0)>postingNum) {
                WriteFile.writeToEndOfFile(fileName + "_"+ postingNum + ".txt", writeToPosting);
                postingNum++;
                writeToPosting = new LinkedList<>();
            }
            writeToPosting.add(finalPostingLine.append("\t").append(numOfAppearances));
        } while(containsNull(firstSentenceOfFile) && postingNum<123);
        WriteFile.writeToEndOfFile(fileName + "_z" + ".txt", writeToPosting);
    }

    private boolean containsNull(String[] firstSentenceOfFile) {
        for (String sentence: firstSentenceOfFile) {
            if(sentence!=null)
                return true;
        }
        return false;
    }

    private String getNextSentence(BufferedReader bf){
        String line = null;
        try {
            if((line= bf.readLine())!=null)
                return line;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

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
    }*/

    /*private void fillTheCityDictionary(LinkedList<DocDictionaryNode> documentDictionary ,HashMap<String,CityInfoNode> cityDictionary) {
        CitysMemoryDataBase citysMemoryDataBaseRESTAPI = null;
        //CitysMemoryDataBase citysMemoryDataBaseGeoBytesAPI = null;
        try {
            ///citysMemoryDataBaseGeoBytesAPI = new CitysMemoryDataBase("http://getcitydetails.geobytes.com/GetCityDetails?fqcn=geobytescapital");
            citysMemoryDataBaseRESTAPI = new CitysMemoryDataBase("https://restcountries.eu/rest/v2/all?fields=name;capital;population;currencies");

        } catch (IOException e) {
            e.printStackTrace();
        }
        for (DocDictionaryNode cur:documentDictionary){
            String curCity = cur.getCity();
            if (!curCity.equals("") && !cityDictionary.containsKey(curCity)) {
                CityInfoNode toPut = citysMemoryDataBaseRESTAPI.getCountryByCapital(curCity);
                if(toPut!=null)
                    cityDictionary.put(curCity, toPut);
                //else toPut = citysMemoryDataBaseGeoBytesAPI.getCountryByCapital(curCity);
                if(toPut==null)
                    System.out.println(curCity+"   couldnt find city in API");
            }
        }
        /*for (DocDictionaryNode cur : documentDictionary){
            String curCity = cur.getCity();
            if (!curCity.equals("") && !cityDictionary.containsKey(curCity)) {
                String[] cityWords = curCity.split(" ");
                int i = 0;
                StringBuilder cityTry = new StringBuilder();
                while(i < cityWords.length && !cityDictionary.containsKey(cityTry.toString())) {
                    cityTry.append(cityWords[i]);
                    CityInfoNode toPut = citysMemoryDataBaseRESTAPI.getCountryByCapital(cityTry.toString());
                    if (toPut != null ) {
                        if(!cityDictionary.containsKey(cityTry.toString()))
                            cityDictionary.put(cityTry.toString(), toPut);
                        break;
                    }
                    i++;
                    cityTry.append(" ");
                }
            }
        }
    }*/
}


