package Queries;

import Index.DocDictionaryNode;
import Model.Model;

import java.util.*;

class Ranker {
    private double m_averageDocumentLength;
    private HashMap<String, Integer> m_wordsCount;

    Ranker(HashMap<String, Integer> wordsCount){
        this.m_averageDocumentLength = getDocumentAverageLength();
        this.m_wordsCount = wordsCount;
    }

    double BM25(String word, String documentName, int tf, double idf, double weight) {
        double k = 1.2, b= 0.75;
        int documentLength = Model.documentDictionary.get(documentName).getDocLength();
        int wordInQueryCount = m_wordsCount.get(word);
        double numeratorBM25 = wordInQueryCount * (k + 1) * weight*tf * idf;
        double denominatorBM25 = weight*tf + k * (1 - b + (b * (documentLength / m_averageDocumentLength)));
        return numeratorBM25 / denominatorBM25;
    }

    private double getDocumentAverageLength() {
        double sum = 0, count = 0;
        for(DocDictionaryNode node: Model.documentDictionary.values()){
            sum += node.getDocLength();
            count++;
        }
        return sum/count;
    }
}
