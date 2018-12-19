package Queries;

import Index.DocDictionaryNode;
import Model.Model;
import org.apache.commons.lang3.StringUtils;

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

    double BM25AndPLN(String documentName, double k, double b){
        double rankBM25 = 0;
        double rankPLN = 0;
        HashMap<String,Double> wordsIDF = calculateLog();
        for (String word: m_wordsCount.keySet()) {
            String postingLine = m_wordsPosting.get(word);
            if(!postingLine.equals("")) {
                int wordInQueryCount = m_wordsCount.get(word);
                int wordInDocumentCount = getWordInDocumentCount(postingLine,documentName);
                int documentLength = getDocLength(documentName);
                double numeratorBM25 = wordInQueryCount * (k + 1) * wordInDocumentCount * wordsIDF.get(word);
                double denominatorBM25 = wordInDocumentCount + k * (1 - b + (b * (documentLength / m_averageDocumentLength)));
                double numeratorPLN = wordInQueryCount * Math.log(1+Math.log(1+wordInDocumentCount))*wordsIDF.get(word);
                double denominatorPLN = 1 - b + (b * (documentLength / m_averageDocumentLength));
                rankBM25 += numeratorBM25 / denominatorBM25;
                rankPLN += numeratorPLN / denominatorPLN;
            }
        }
        return (rankBM25+rankPLN)/2;
    }

    double tfIdf(String documentName){
        double tfidf = 0;
        double docInCorpusCount = Model.documentDictionary.keySet().size();
        for (String word: m_wordsCount.keySet()) {
            String postingLine = m_wordsPosting.get(word);
            if(!postingLine.equals(""))
                tfidf += getTfIdfForWord(postingLine,docInCorpusCount,documentName,word);
        }
        return  tfidf;
    }

    /*double cosSim(String documentName){
        double tfidf = 0;
        double numerator = 0,denominator = 0;
        double docInCorpusCount = Model.documentDictionary.keySet().size();
        for (String word: m_wordsCount.keySet()) {
            String postingLine = m_wordsPosting.get(word);
            if(!postingLine.equals("")) {
                numerator += getTfIdfForWord(postingLine,docInCorpusCount,documentName,word);
            }
        }
    }*/

    private double getTfIdfForWord(String postingLine, double docInCorpusCount, String documentName, String word){
        double tf = (double)(getWordInDocumentCount(postingLine,documentName)/getDocLength(documentName));
        double idf = Math.log10(docInCorpusCount/(StringUtils.countMatches(m_wordsPosting.get(word),"|")+1));
        return tf*idf;
    }


    private int getDocLength(String documentName) {
        return Model.documentDictionary.get(documentName).getDocLength();
    }

    private int getWordInDocumentCount(String postingLine, String docName) {
        String[] split = postingLine.split("\\|");
        for (String aSplit : split) {
            String[] splitLine = aSplit.split(",");
            String docNameToCheck = splitLine[0];
            if (docNameToCheck.equals(docName)) {
                return countSeparators(splitLine[2]);
            }
        }
        return 0;
    }

    private int countSeparators(String s) {
        return StringUtils.countMatches(s,"&")+1;
    }

    private HashMap<String,Double> calculateLog() {
        HashMap<String, Double> wordsIDF =  new HashMap<>();
        double docInCorpusCount = Model.documentDictionary.keySet().size();
        for (String word: m_wordsCount.keySet()) {
            double d = Math.log10(docInCorpusCount/(StringUtils.countMatches(m_wordsPosting.get(word),"|")+1));
            wordsIDF.put(word,d);
        }
        return wordsIDF;
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
