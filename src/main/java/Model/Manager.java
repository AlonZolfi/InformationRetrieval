package Model;

import IO.CorpusDocument;
import IO.ReadFile;
import IO.WriteFile;
import Index.*;
import Parse.*;
import Web.CitysMemoryDataBase;
import javafx.util.Pair;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Manager {
    private AtomicInteger numOfPostings = new AtomicInteger(0);

    public double[] Manage(HashMap<String, CityInfoNode> cityDictionary, LinkedList<DocDictionaryNode> documentDictionary, InvertedIndex invertedIndex, String corpusPath, String destinationPath, boolean stem) {
        CitysMemoryDataBase citysMemoryDataBaseRESTAPI = fillCityDataBase();
        int numOfDocs = 0;
        double start = System.currentTimeMillis();
        int iter = 1800;
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
                new Thread(()->WriteFile.writeTmpPosting(destinationPath, numOfPostings.getAndIncrement(), temporaryPosting)).start();
                //second fill the InvertedIndex with words and linkes
                fillCityData(miniDicList,cityDictionary,citysMemoryDataBaseRESTAPI,invertedIndex,documentDictionary);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            pool.shutdown();
        }

        mergePostings(invertedIndex,destinationPath,stem);
        WriteFile.writeInvertedFile(destinationPath,invertedIndex,stem);
        for (String word:cityDictionary.keySet()) {
            cityDictionary.get(word).setPosting(invertedIndex.getPostingLink(word));
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
                        firstSentenceOfFile[i] = getNextSentence(bufferedReaderList.get(i));;
                }
            }
            if(!finalPostingLine.toString().equals("")) {
                invertedIndex.setPointer(minTerm, fileName+"_"+postingNum+".txt", writeToPosting.size());
                invertedIndex.setNumOfAppearance(minTerm,numOfAppearances);
            }
            if(minTerm.toLowerCase().charAt(0)>postingNum) {
                WriteFile.writeToEndOfFile(fileName + "_"+ postingNum + ".txt", writeToPosting);
                postingNum++;
                writeToPosting = new LinkedList<>();
            }
            writeToPosting.add(finalPostingLine.append("\t").append(numOfAppearances));
        } while(containsNull(firstSentenceOfFile) && postingNum<'z'+1);
        WriteFile.writeToEndOfFile(fileName + "_z" + ".txt", writeToPosting);
    }*/


    private void mergePostings(InvertedIndex invertedIndex, String tempPostingPath,boolean stem){
        LinkedList<BufferedReader> bufferedReaderList = initiateBufferedReaderList(tempPostingPath);
        String[] firstSentenceOfFile = initiateMergingArray(bufferedReaderList);
        char postingNum = '`';
        HashMap<String, StringBuilder> writeToPosting = new HashMap<>();
        Comparator<String> comparator = new StringNaturalOrderComparator();
        String fileName = "Stem"+tempPostingPath+"\\finalPosting";
        if (!stem)
            fileName= tempPostingPath+"\\finalPosting";
        do {
            int numOfAppearances = 0;
            StringBuilder finalPostingLine = new StringBuilder();
            String minTerm = ""+(char)127;
            String[] saveSentences = new String[firstSentenceOfFile.length];
            for (int i = 0; i < firstSentenceOfFile.length; i++) {
                if(firstSentenceOfFile[i]!=null && !firstSentenceOfFile[i].equals("")) {
                    String[] termAndData = firstSentenceOfFile[i].split("~");
                    int result = comparator.compare(termAndData[0],minTerm);
                    if (result == 0) {
                        if(Character.isLowerCase(termAndData[0].charAt(0)))
                            finalPostingLine.replace(0, 1, "" + termAndData[0].charAt(0));
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
            restoreSentence(bufferedReaderList,minTerm,firstSentenceOfFile,saveSentences);
            finalPostingLine.append("\t").append(numOfAppearances);
            if(minTerm.toLowerCase().charAt(0)>postingNum) {
                writeFinalPosting(writeToPosting,invertedIndex,fileName,postingNum);
                writeToPosting = new HashMap<>();
                postingNum++;
            }
            lookForSameTerm(finalPostingLine.toString().split("~")[0],finalPostingLine,writeToPosting);
        } while(containsNull(firstSentenceOfFile) && postingNum<'z'+1);
        writeFinalPosting(writeToPosting,invertedIndex,fileName,'z');
        invertedIndex.deleteEntriesOfIrrelevant();
        closeAllFiles(bufferedReaderList);
    }

    private void writeFinalPosting(HashMap<String, StringBuilder> writeToPosting, InvertedIndex invertedIndex, String fileName, char postingNum) {
        List<String> keys = new LinkedList<String>(writeToPosting.keySet());
        int k = 0;
        for (String word0: keys){
            String toNum=writeToPosting.get(word0).toString().split("\t")[1];
            int num = Integer.parseInt(toNum);
            invertedIndex.setPointer(word0, fileName+"_"+postingNum+".txt", k++);
            invertedIndex.setNumOfAppearance(word0,num);
        }
        final HashMap<String, StringBuilder> sendToThread = new HashMap<>(writeToPosting);
        String file = fileName + "_"+ postingNum + ".txt";
        new Thread(()->WriteFile.writeToEndOfFile(file, sendToThread)).start();
    }

    private void closeAllFiles(LinkedList<BufferedReader> bufferedReaderList) {
        for (BufferedReader bf: bufferedReaderList) {
            try {
                bf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void lookForSameTerm(String minTerm, StringBuilder finalPostingLine, HashMap<String, StringBuilder> writeToPosting){
        boolean option1 = writeToPosting.containsKey(Character.toUpperCase(minTerm.charAt(0))+minTerm.substring(1));
        boolean option2 = writeToPosting.containsKey(minTerm.toUpperCase());
        boolean option3 = Character.isLowerCase(minTerm.charAt(0));
        boolean option4 = writeToPosting.containsKey(minTerm);
        if(option1 || (option2 && option3) || option4) {
            String replace;
            if (option1)
                replace = writeToPosting.remove(Character.toUpperCase(minTerm.charAt(0))+minTerm.substring(1)).toString();
            else if(option2)
                replace = writeToPosting.remove(minTerm.toUpperCase()).toString();
            else
                replace = writeToPosting.remove(minTerm).toString();
            String[] separatePostingAndNumOld = replace.split("\t");
            String[] separatePostingAndNumNew = finalPostingLine.toString().split("\t");
            int numOfAppearance = Integer.parseInt(separatePostingAndNumOld[1]) + Integer.parseInt(separatePostingAndNumNew[1]);
            writeToPosting.put(minTerm, new StringBuilder(minTerm + "~" + separatePostingAndNumOld[0].substring(separatePostingAndNumOld[0].indexOf("~") + 1) + separatePostingAndNumNew[0].substring(separatePostingAndNumNew[0].indexOf("~") + 1) + "\t" + numOfAppearance));
        } else if(option2){
            String replace = writeToPosting.remove(minTerm.toUpperCase()).toString();
            String[] separatePostingAndNumOld = replace.split("\t");
            String[] separatePostingAndNumNew = finalPostingLine.toString().split("\t");
            int numOfAppearance = Integer.parseInt(separatePostingAndNumOld[1]) + Integer.parseInt(separatePostingAndNumNew[1]);
            writeToPosting.put(minTerm.toUpperCase(), new StringBuilder(minTerm + "~" + separatePostingAndNumOld[0].substring(separatePostingAndNumOld[0].indexOf("~") + 1) + separatePostingAndNumNew[0].substring(separatePostingAndNumNew[0].indexOf("~") + 1) + "\t" + numOfAppearance));
        }
        else
            writeToPosting.put(minTerm,finalPostingLine);
    }

    private void restoreSentence(LinkedList<BufferedReader> bufferedReaderList,String minTerm,String[] firstSentenceOfFile, String[] saveSentences){
        for (int i = 0; i < saveSentences.length; i++) {
            if (saveSentences[i] != null) {
                String[] termAndData = saveSentences[i].split("~");
                if (!termAndData[0].equals(minTerm)) {
                    firstSentenceOfFile[i] = termAndData[0]+"~"+termAndData[1]+"~"+termAndData[2];
                }
                else
                    firstSentenceOfFile[i] = getNextSentence(bufferedReaderList.get(i));;
            }
        }
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
            if((line= bf.readLine())!=null) {
                return line;
            }
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
    }

    private void fillCityData(ConcurrentLinkedDeque<MiniDictionary> miniDicList, HashMap<String, CityInfoNode> cityDictionary, CitysMemoryDataBase citysMemoryDataBaseRESTAPI, InvertedIndex invertedIndex, LinkedList<DocDictionaryNode> documentDictionary) {
        for (MiniDictionary mini : miniDicList) {
            String curCity = mini.getCity();
            StringBuilder cityTry = new StringBuilder();
            if (!curCity.equals("") && !cityDictionary.containsKey(curCity)) {
                String[] cityWords = curCity.split(" ");
                int j = 0;
                boolean found = false;
                while (j < cityWords.length && !found) {
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
                    } else found = true;
                }
                if (!found) cityTry = new StringBuilder();
                DocDictionaryNode cur = new DocDictionaryNode(mini.getName(), mini.getMaxFrequency(), mini.size(), cityTry.toString());
                documentDictionary.add(cur);
            }
            cityTry.delete(0, cityTry.length());
            for (String word : mini.listOfWords()) {
                invertedIndex.addTerm(word);
            }
        }
    }

    private CitysMemoryDataBase fillCityDataBase(){
        CitysMemoryDataBase citysMemoryDataBaseRESTAPI = null;
        try {
            citysMemoryDataBaseRESTAPI = new CitysMemoryDataBase("https://restcountries.eu/rest/v2/all?fields=name;capital;population;currencies");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return citysMemoryDataBaseRESTAPI;
    }


}