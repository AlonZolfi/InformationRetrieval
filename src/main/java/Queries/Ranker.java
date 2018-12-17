package Queries;

import Index.DocDictionaryNode;
import Model.Model;

import java.util.*;

public class Ranker {

    public double BM25(Map<String,Integer> queryWords, String documentName, int k, int b){
        double averageDocumentLength = getDocumentAverageLength();
        double rank = 0;
        HashMap<String,Double> wordsIDF = calculateLog(queryWords.keySet());
        for (String word: queryWords.keySet()) {
            if(documentContainsWord(word)) {
                int wordInQueryCount = queryWords.get(word);
                int wordInDocumentCount = getWordInDocumentCount(word);
                double numerator = wordInQueryCount * (k + 1) * wordInDocumentCount * wordsIDF.get(word);
                int documentLength = getDocLength(documentName);
                double denominator = wordInDocumentCount + k * (1 - b + b * (documentLength / averageDocumentLength));
                rank = numerator / denominator;
            }
        }

        return rank;
    }

    private boolean documentContainsWord(String word) {
        boolean lower = !Model.invertedIndex.getPostingLink(word.toLowerCase()).equals("");
        boolean upper = !Model.invertedIndex.getPostingLink(word.toUpperCase()).equals("");
        return upper || lower;
    }

    private int getDocLength(String documentName) {
        return 0;
    }

    private int getWordInDocumentCount(String word) {
        return 0;
    }

    private HashMap<String,Double> calculateLog(Set<String> words) {
        return new HashMap<>();
    }

    private double getDocumentAverageLength() {
        double sum = 0, count = 0;
        for(DocDictionaryNode node: Model.documentDictionary){
            sum += node.getDocLength();
            count++;
        }
        return sum/count;
    }
}
