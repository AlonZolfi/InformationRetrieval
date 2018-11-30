package Model;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ReadFile {

    public static Set<String> stopWords;

    public static void initStopWords(String pathOfStopWords){
        stopWords = new HashSet<>();
        String fileName = pathOfStopWords;
        String line = null;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                stopWords.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*public static LinkedList<CorpusDocument> readFiles(String pathOfDocs, String pathOfStopWords,int mone,int mechane) {
        File dir = new File(pathOfDocs);
        File[] directoryListing = dir.listFiles();
        LinkedList<CorpusDocument> allDocsInCorpus = new LinkedList<>();
        int start = mone*directoryListing.length/mechane;
        int end = ((mone+1)*directoryListing.length/mechane)-1;
        if (directoryListing != null && dir.isDirectory()) {
            ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
            LinkedList<Future> futureDocsInFile = new LinkedList<>();
            /*for (File file : directoryListing) {
                Future<LinkedList<CorpusDocument>> f = pool.submit(new ReadDocuments(file));
                futureDocsInFile.add(f);
            }
            for (int i = start; i <= end; i++) {
                Future<LinkedList<CorpusDocument>> f = pool.submit(new ReadDocuments(directoryListing[i]));
                futureDocsInFile.add(f);
            }

            for (Future f : futureDocsInFile) {
                try {
                    allDocsInCorpus.addAll((LinkedList<CorpusDocument>) (f.get()));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            pool.shutdown();
        } else {
            System.out.println("Not a directory");
        }

        return allDocsInCorpus;
    }*///

    public static void readFiles(String pathOfDocs) {
        File dir = new File(pathOfDocs);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null && dir.isDirectory()) {
            ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
            LinkedList<Future<LinkedList<CorpusDocument>>> futureBulk = new LinkedList<>();
            for (File file : directoryListing) {
                try {
                    Manager.emptyCorpusDocSemaphore.acquire();
                    Manager.corpusDocQueue.add(pool.submit(new ReadDocuments(file)));
                    Manager.fullCorpusDocSemaphore.release();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Manager.stopReadAndParse=true;
            try {
                pool.awaitTermination(1,TimeUnit.DAYS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pool.shutdown();
        } else {
            System.out.println("Not a directory");
        }
    }
}
