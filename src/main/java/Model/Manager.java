package Model;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Manager {
    public void Manage(String corpusPath, String stopWordsPath, String destinationPath, boolean stem) {
        LinkedList<CorpusDocument> l = ReadFile.readFiles(corpusPath, stopWordsPath);
        ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*2);
        for(CorpusDocument cd : l){
            pool.execute(new Parse(cd,stem));
        }

    }
}
