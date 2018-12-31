package Queries;

import Index.DocDictionaryNode;
import Model.Model;

import java.util.*;

class Ranker {
    private double m_averageDocumentLength;//average document size in the corpus
    private HashMap<String, Integer> m_wordsCount;//count of words in the query

    Ranker(HashMap<String, Integer> wordsCount){
        this.m_averageDocumentLength = getDocumentAverageLength();
        this.m_wordsCount = wordsCount;
    }

    /**
     * calculates the BM25 rank of word with a document
     * @param word word to cal
     * @param documentName document name
     * @param tf number of appearnces of the word in the document
     * @param idf idf of the word
     * @return the value of part of BM25 (word with doc)
     */
    double BM25(String word, String documentName, int tf, double idf) {
        double k = 1.2, b= 0.75;
        int documentLength = Model.documentDictionary.get(documentName).getDocLength();
        int wordInQueryCount = m_wordsCount.get(word);
        double numeratorBM25 = wordInQueryCount * (k + 1) * tf * idf;
        double denominatorBM25 = tf + k * (1 - b + (b * (documentLength / m_averageDocumentLength)));
        return numeratorBM25 / denominatorBM25;
    }

    /**
     * returns the average document length
     * @return the average length
     */
    private double getDocumentAverageLength() {
        double sum = 0, count = 0;
        for(DocDictionaryNode node: Model.documentDictionary.values()){
            sum += node.getDocLength();
            count++;
        }
        return sum/count;
    }
}
