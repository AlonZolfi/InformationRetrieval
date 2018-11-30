package Model;

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;
import javafx.application.Platform;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.*;

public class Manager {
    private final int NUM_OF = 5;
    public static Semaphore fullCorpusDocSemaphore = new Semaphore(0);
    public static Semaphore emptyCorpusDocSemaphore = new Semaphore(5);
    public static Semaphore fullMiniDicSemaphore = new Semaphore(0);
    public static Semaphore emptyMiniDicSemaphore = new Semaphore(5);
    public static ConcurrentLinkedDeque<Future<LinkedList<CorpusDocument>>> corpusDocQueue = new ConcurrentLinkedDeque<Future<LinkedList<CorpusDocument>>>();
    public static ConcurrentLinkedDeque<LinkedList<MiniDictionary>> miniDicQueue = new ConcurrentLinkedDeque<LinkedList<MiniDictionary>>();
    public static boolean stopReadAndParse = false;
    public static boolean stopIndexAndTempPosting = false;
    private int numOfDocs = 0;
    private int numOfPostings=0;

    public double[] Manage(LinkedList<DocDictionaryNode> documentDictionary, InvertedIndex invertedIndex, String corpusPath, String stopWordsPath, String destinationPath, boolean stem) {

        double start = System.currentTimeMillis();
        new Thread(() -> ReadFile.readFiles(corpusPath)).start();
        new Thread(()->Parse(stem)).start();
        new Thread(()->indexAndWriteTemporaryPosting(destinationPath,invertedIndex)).start();
        while(!stopIndexAndTempPosting);
        return new double[]{numOfDocs, invertedIndex.getNumOfUniqueTerms(), (System.currentTimeMillis() - start) / 60000};

    }

    private void Parse( boolean stem) {

        while (!stopReadAndParse) {
            ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
            //CONSUME
            LinkedList<MiniDictionary> listOfMiniDics = new LinkedList<MiniDictionary>();
            try {
                fullCorpusDocSemaphore.acquire();

                LinkedList<CorpusDocument> docsOfOneFile = corpusDocQueue.poll().get();
                LinkedList<Future<MiniDictionary>> futureListOfMiniDics = new LinkedList<Future<MiniDictionary>>();
                emptyCorpusDocSemaphore.release();
                for(CorpusDocument cd: docsOfOneFile) {
                    numOfDocs++;
                    futureListOfMiniDics.add(pool.submit(new Parse(cd, stem)));
                }

                for(Future<MiniDictionary> f: futureListOfMiniDics){
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
        stopIndexAndTempPosting = true;
    }

    private void indexAndWriteTemporaryPosting(String destinationPath,InvertedIndex invertedIndex) {
        //CONSUME
        while(!stopIndexAndTempPosting) {
            ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
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
                WriteFile.writeToDest(destinationPath,numOfPostings++,temporaryPosting);
                //second fill the InvertedIndex with words and linkes
                for (MiniDictionary mini:bulkToIndex) {
                    for (String word:mini.listOfWords()) {
                        invertedIndex.addTerm(word);
                    }
                }

            } catch (InterruptedException | ExecutionException | IOException e) {
                e.printStackTrace();
            }
            pool.shutdown();
        }
    }

    /*public double[] Manage(LinkedList<DocDictionaryNode> documentDictionary, InvertedIndex invertedIndex, String corpusPath, String stopWordsPath, String destinationPath, boolean stem) {
        int numOfDocs = 0;
        ReadFile.initStopWords(stopWordsPath);
        double start = System.currentTimeMillis();
        int iter = 150;
        for (int i = 0; i < iter; i++) {
            double startInner = System.currentTimeMillis();
            LinkedList<CorpusDocument> l = ReadFile.readFiles(corpusPath, stopWordsPath, i, iter);
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
            Future<HashMap<String, StringBuilder>> futureTemporaryPosting = pool.submit(index);
            HashMap<String, StringBuilder> temporaryPosting = null;
            try {
                temporaryPosting = futureTemporaryPosting.get();

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            /*for (String s: temporaryPosting.keySet()) {
                //add to inverted index and doc index and bla bla bla
            }

            //write temporary posting to the disk
            //System.out.println(System.currentTimeMillis()-startInner);
            pool.shutdown();
        }
        //System.out.println(System.currentTimeMillis()-start);
        return new double[]{numOfDocs,invertedIndex.getNumOfUniqueTerms(),(System.currentTimeMillis()-start)/60000};
        //MERGE ALL POSTINGS
        //fix link in the inverted index
    }*/
    //MERGE ALL POSTINGS
    //fix link in the inverted index
}


