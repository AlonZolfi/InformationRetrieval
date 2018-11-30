package Model;

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;
import javafx.application.Platform;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.*;

public class Manager {
    public static Semaphore fullCorpusDoc = new Semaphore(0);
    public static Semaphore emptyCorpusDoc = new Semaphore(5);
    public static Semaphore fullMiniDic = new Semaphore(0);
    public static Semaphore emptyMiniDic = new Semaphore(5);
    public static ConcurrentLinkedDeque<Future<LinkedList<CorpusDocument>>> corpusDocQueue = new ConcurrentLinkedDeque<Future<LinkedList<CorpusDocument>>>();
    public static ConcurrentLinkedDeque<LinkedList<MiniDictionary>> miniDicQueue = new ConcurrentLinkedDeque<LinkedList<MiniDictionary>>();
    public static boolean stopReadAndParse = false;
    public static boolean stopIndexAndTempPosting = false;
    private int numOfDocs = 0;

    public double[] Manage(LinkedList<DocDictionaryNode> documentDictionary, InvertedIndex invertedIndex, String corpusPath, String stopWordsPath, String destinationPath, boolean stem) {

        double start = System.currentTimeMillis();
        new Thread(() -> ReadFile.readFiles(corpusPath)).start();
        new Thread(()->readAndParse(corpusPath,stem)).start();
        new Thread(this::indexAndWriteTemporaryPosting).start();
        while(!stopIndexAndTempPosting);
        return new double[]{numOfDocs, invertedIndex.getNumOfUniqueTerms(), (System.currentTimeMillis() - start) / 60000};

    }

    private void readAndParse(String corpusPath, boolean stem) {

        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        LinkedList<MiniDictionary> listOfMiniDics = new LinkedList<MiniDictionary>();
        while (!stopReadAndParse) {
            //CONSUME
            try {
                fullCorpusDoc.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            LinkedList<CorpusDocument> docsOfOneBulk = corpusDocQueue.poll();


            LinkedList<Future<MiniDictionary>> futureListOfMiniDics = new LinkedList<Future<MiniDictionary>>();
            for(CorpusDocument cd: docsOfOneBulk) {
                futureListOfMiniDics.add(pool.submit(new Parse(cd, stem)));
            }

            for(Future<MiniDictionary> f: futureListOfMiniDics){
                try {
                    listOfMiniDics.add(f.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            emptyCorpusDoc.release();







            //PRODUCE
            try {
                emptyMiniDic.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            miniDicQueue.add(listOfMiniDics);
            listOfMiniDics = new LinkedList<MiniDictionary>();
            fullMiniDic.release();
        }
        pool.shutdown();
        stopIndexAndTempPosting = true;
    }

    private void indexAndWriteTemporaryPosting() {
        //CONSUME
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
        while(!stopIndexAndTempPosting) {
            try {
                fullMiniDic.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            Indexer index = new Indexer(miniDicQueue.poll());


            Future<HashMap<String, StringBuilder>> futureTemporaryPosting = pool.submit(index);



            HashMap<String, StringBuilder> temporaryPosting = null;


            try {
                temporaryPosting = futureTemporaryPosting.get();

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            emptyMiniDic.release();
        }
        pool.shutdown();
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


