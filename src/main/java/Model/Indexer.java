package Model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Indexer implements Callable<HashMap<String,StringBuilder>> {
    private ConcurrentLinkedDeque<MiniDictionary> m_miniDicList;
    //private LinkedList<MiniDictionary> m_miniDicList;

    public Indexer(ConcurrentLinkedDeque minidic){
        m_miniDicList = minidic;
    }

    @Override
    public HashMap<String, StringBuilder> call() {
        // adding to inverted index the term and the other data
        // AND adding to the map (temoporary posting)
        HashMap<String, StringBuilder> toReturn = new HashMap<>();
        if(m_miniDicList !=null){
            for (MiniDictionary miniDic: m_miniDicList) {
                for (String word : miniDic.listOfWords()) {
                    if (toReturn.containsKey(word)) {
                        toReturn.get(word).append(miniDic.listOfData(word)).append("|");
                    }
                    else{
                        StringBuilder sb = new StringBuilder(miniDic.listOfData(word)+"|");
                        toReturn.put(word,sb);
                    }
                }
            }
        }
        return toReturn;
    }


}
