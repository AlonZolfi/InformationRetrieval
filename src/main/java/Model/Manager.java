package Model;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.*;

public class Manager {


    public void Manage(LinkedList<DocDictionaryNode> documentDictionary, InvertedIndex invertedIndex, String corpusPath, String stopWordsPath, String destinationPath, boolean stem) {
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
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            Index index = new Index(miniDicList);
            Future<HashMap<String, StringBuilder>> futureTemporaryPosting = pool.submit(index);
            HashMap<String, StringBuilder> temporaryPosting = null;
            try {
                temporaryPosting = futureTemporaryPosting.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            /*for (String s: temporaryPosting.keySet()) {
                //add to inverted index and doc index and bla bla bla
            }*/

            //write temporary posting to the disk
            System.out.println(System.currentTimeMillis()-startInner);
            pool.shutdown();
        }
        System.out.println(System.currentTimeMillis()-start);
        //MERGE ALL POSTINGS
        //fix link in the inverted index
    }
}

