package Queries;

import Index.DocDictionaryNode;
import Model.*;

import java.util.HashMap;
import java.util.List;


public class Searcher {

    public void getQueryResults(String query){
        HashMap<String, Integer> wordsQuery = putWordsInMap(query);
        double averageDocmentLength= getDocumentAverageLength();
        Ranker r = new Ranker(2,0.5,averageDocmentLength);
        //GO TO BM25 AND OTHER STUFF
    }

    private double getDocumentAverageLength() {
        double sum = 0, count = 0;
        for(DocDictionaryNode node: Model.documentDictionary){
            sum += node.getDocLength();
            count++;
        }
        return sum/count;
    }

    private HashMap<String, Integer> putWordsInMap(String query) {
        HashMap<String,Integer> words = new HashMap<>();
        String[] splitBySpace = query.split(" ");
        for (String word: splitBySpace) {
            if(words.containsKey(word))
                words.put(word,1);
            else
                words.replace(word,words.get(word)+1);
        }
        return words;
    }
}
