package Model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Callable;

public class Index implements Callable<HashMap<String,StringBuilder>> {
    private LinkedList<DocDictionaryNode> m_documentDictionary;
    private InvertedIndex m_invertedIndex;
    private LinkedList<MiniDictionary> m_miniDicList;

    public Index(LinkedList<MiniDictionary> minidic){//LinkedList<DocDictionaryNode> documentDictionary, InvertedIndex invertedIndex){
        /*m_documentDictionary = documentDictionary;
        m_invertedIndex = invertedIndex;*/
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
                        toReturn.get(word).append(miniDic.listOfData(word)+"|");
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
