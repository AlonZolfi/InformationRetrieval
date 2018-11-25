package Model;

import java.util.LinkedList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Manager {
    public void Manage(String corpusPath, String stopWordsPath, String destinationPath, boolean stem) {
        long start = System.currentTimeMillis();
        LinkedList<CorpusDocument> l = ReadFile.readFiles(corpusPath, stopWordsPath);
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
        LinkedList<Future<MiniDictionary>> futureMiniDicList = new LinkedList<Future<MiniDictionary>>();
        for(CorpusDocument cd : l){
            futureMiniDicList.add(pool.submit(new Parse(cd,stem)));
        }
        LinkedList<MiniDictionary> miniDicList = new LinkedList<>();
        for (Future<MiniDictionary> fMiniDic: futureMiniDicList) {
            try {
                miniDicList.add(fMiniDic.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println(end-start);
        for (MiniDictionary miniDic: miniDicList) {
            // DO WHAT YOU WANT HERE
        }

    }
}
