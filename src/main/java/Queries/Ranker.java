package Queries;

import java.util.*;

public class Ranker {
    private double k;
    private double b;
    private double averageDocumentLength;

    public Ranker(double k, double b, double averageDocumentLength) {
        this.k = k;
        this.b = b;
        this.averageDocumentLength = averageDocumentLength;
    }

    public double BM25(Map<String,Integer> queryWords, String documentName){
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
        return false;
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

}
