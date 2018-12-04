package Index;

import Parse.MiniDictionary;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;

public class Indexer implements Callable<HashMap<String, Pair<Integer,StringBuilder>>> {
    private ConcurrentLinkedDeque<MiniDictionary> m_miniDicList;
    //private LinkedList<MiniDictionary> m_miniDicList;

    public Indexer(ConcurrentLinkedDeque minidic){
        m_miniDicList = minidic;
    }

    @Override
    public HashMap<String, Pair<Integer,StringBuilder>> call() {
        // adding to inverted index the term and the other data
        // AND adding to the map (temoporary posting)
        HashMap<String, Pair<Integer,StringBuilder>> toReturn = new HashMap<>();
        if(m_miniDicList !=null){
            for (MiniDictionary miniDic: m_miniDicList) {
                for (String word : miniDic.listOfWords()) {
                    if (toReturn.containsKey(word)) {
                        Pair<Integer,StringBuilder> all = toReturn.remove(word);
                        int newShows = all.getKey()+miniDic.getFrequency(word);
                        StringBuilder newSb = all.getValue().append(miniDic.listOfData(word)).append("|");
                        Pair<Integer,StringBuilder> newAll = new Pair<>(newShows,newSb);
                        toReturn.put(word,newAll);
                    }
                    else{
                        int shows = miniDic.getFrequency(word);
                        StringBuilder sb = new StringBuilder(miniDic.listOfData(word)+"|");
                        Pair<Integer,StringBuilder> all = new Pair<>(shows,sb);
                        toReturn.put(word,all);
                    }
                }
            }
        }
        return toReturn;
    }


}
