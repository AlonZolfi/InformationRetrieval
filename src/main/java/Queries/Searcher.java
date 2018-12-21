package Queries;

import IO.CorpusDocument;
import IO.ReadFile;
import Model.*;
import Parse.*;

import java.util.*;

import static java.util.Collections.reverseOrder;

public class Searcher {
    private String postingPath;
    private boolean stem;

    public Searcher(String postingPath, boolean stem) {
        this.postingPath = postingPath;
        this.stem = stem;
    }

    public LinkedList<String> getQueryResults(Query q) {
        //parse query
        Parse p = new Parse(new CorpusDocument("","","","",q.getTitle(),""),stem);
        MiniDictionary md = p.parse();
        Set<String> hs =  md.listOfWords();
        //prepare for calculation
        HashMap<String, Integer> wordsCountInQuery = putWordsInMap(hs);
        CaseInsensitiveMap wordsPosting = getWordsPosting(hs);
        //objects for the iteration
        Ranker ranker = new Ranker(wordsCountInQuery, wordsPosting);
        HashMap<String, Double> score = new HashMap<>();


        for (String word : wordsCountInQuery.keySet())
        {
            if (!wordsPosting.get(word).equals("")) {
                String postingLine = wordsPosting.get(word);
                String[] split = postingLine.split("\\|");
                double idf = getIDF(split.length);
                for (String aSplit : split) {
                    String[] splitLine = aSplit.split(",");
                    String docName = splitLine[0];
                    if (splitLine.length>1) {
                        int tf = Integer.parseInt(splitLine[1]);
                        double BM25 = ranker.BM25AndPLN(word,docName,tf,idf, 1.2, 0.75);
                        addToScore(score,docName,BM25);
                    }
                }
            }
        }
        return sortByScore(score);
    }

    private LinkedList<String> sortByScore(HashMap<String, Double> score) {
        List<Map.Entry<String,Double>> list = new ArrayList<>(score.entrySet());
        list.sort(reverseOrder(Map.Entry.comparingByValue()));

        LinkedList<String> result= new LinkedList<>();
        for(Map.Entry<String,Double> entry: list){
            result.add(entry.getKey());
        }
        return result;
    }

    private void addToScore(HashMap<String, Double> score, String docName, double bm25AndPLN) {
        if(bm25AndPLN!=0) {
            Double d = score.get(docName);
            if (d != null)
                bm25AndPLN += d;
            score.replace(docName, bm25AndPLN);
        }
    }

    private Double getIDF(int length) {
        double docInCorpusCount = Model.documentDictionary.keySet().size();
        return Math.log10((docInCorpusCount+1)/length);
    }

    private CaseInsensitiveMap getWordsPosting(Set<String> query) {
        CaseInsensitiveMap words = new CaseInsensitiveMap();
        HashMap<Character, LinkedList<Integer>> allCharactersTogether = new HashMap<>();
        for (String word: query) {
            char letter;
            if(!Character.isLetter(word.charAt(0)))
                letter = '`';
            else
                letter = Character.toLowerCase(word.charAt(0));
            String lineNumber = getPostingLineNumber(word);
            if(!lineNumber.equals("")) {
                if (allCharactersTogether.containsKey(letter))
                    allCharactersTogether.get(letter).add(Integer.parseInt(lineNumber));
                else {
                    LinkedList<Integer> lettersLines = new LinkedList<>();
                    lettersLines.add(Integer.parseInt(lineNumber));
                    allCharactersTogether.put(letter,lettersLines);
                }
            }
            else
                words.put(word,"");
        }
        for (Character letter: allCharactersTogether.keySet()) {
            LinkedList<String> postings= ReadFile.readPostingLineAtIndex(postingPath,Character.toLowerCase(letter),allCharactersTogether.get(letter),stem);
            for (String posting: postings) {
                String[] wordAndRestOfPosting = posting.split("~");
                words.put(wordAndRestOfPosting[0],wordAndRestOfPosting[1]);
            }
        }
        return words;
    }

    private HashMap<String, Integer> putWordsInMap(Set<String> query) {
        HashMap<String,Integer> words = new HashMap<>();
        for (String word: query) {
            if(!words.containsKey(word))
                words.put(word,1);
            else
                words.replace(word,words.get(word)+1);
        }
        return words;
    }

    private String getPostingLineNumber(String word){
        String lineNumber = Model.invertedIndex.getPostingLink(word.toLowerCase());
        if(lineNumber.equals(""))
            lineNumber = Model.invertedIndex.getPostingLink(word.toUpperCase());
        return lineNumber;
    }
}
