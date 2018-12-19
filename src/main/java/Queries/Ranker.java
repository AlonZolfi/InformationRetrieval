package Queries;

import Index.DocDictionaryNode;
import Model.Model;

import java.util.*;

public class Ranker {
    private double m_averageDocumentLength;
    private HashMap<String, Integer> m_wordsCount;
    private CaseInsensitiveMap m_wordsPosting;

    Ranker(HashMap<String, Integer> wordsCount, CaseInsensitiveMap wordsPosting){
        this.m_averageDocumentLength = getDocumentAverageLength();
        this.m_wordsCount = wordsCount;
        this.m_wordsPosting = wordsPosting;
    }

    double BM25AndPLN(String word, String documentName, int tf, double idf, double k, double b) {
        double rankBM25 = 0;
        double rankPLN = 0;
        int documentLength = getDocLength(documentName);
        int wordInQueryCount = m_wordsCount.get(word);
        double numeratorBM25 = wordInQueryCount * (k + 1) * tf * idf;
        double denominatorBM25 = tf + k * (1 - b + (b * (documentLength / m_averageDocumentLength)));
        double numeratorPLN = wordInQueryCount * Math.log(1 + Math.log(1 + tf)) * idf;
        double denominatorPLN = 1 - b + (b * (documentLength / m_averageDocumentLength));
        rankBM25 += numeratorBM25 / denominatorBM25;
        rankPLN += numeratorPLN / denominatorPLN;
        return (rankBM25 + rankPLN) / 2;
    }

    private int getDocLength(String documentName) {
        return Model.documentDictionary.get(documentName).getDocLength();
    }

    private double getDocumentAverageLength() {
        double sum = 0, count = 0;
        for(DocDictionaryNode node: Model.documentDictionary.values()){
            sum += node.getDocLength();
            count++;
        }
        return sum/count;
    }

    /*double tfIdf(String documentName){
        double tfidf = 0;
        double docInCorpusCount = Model.documentDictionary.keySet().size();
        for (String word: m_wordsCount.keySet()) {
            String postingLine = m_wordsPosting.get(word);
            if(!postingLine.equals(""))
                tfidf += getTfIdfForWord(postingLine,docInCorpusCount,documentName,word);
        }
        return  tfidf;
    }

    private double getTfIdfForWord(String postingLine, double docInCorpusCount, String documentName, String word){
        double tf = ((double)getWordInDocumentCount(postingLine,documentName))/getDocLength(documentName);
        double idf = Math.log10(docInCorpusCount/(StringUtils.countMatches(m_wordsPosting.get(word),"|")+1));
        return tf*idf;
    }

    private int getWordInDocumentCount(String postingLine, String docName) {
        String[] split = postingLine.split("\\|");
        for (String aSplit : split) {
            String[] splitLine = aSplit.split(",");
            String docNameToCheck = splitLine[0];
            if (docNameToCheck.equals(docName)) {
                return Integer.parseInt(splitLine[1]);
            }
        }
        return 0;
    }*/
}
