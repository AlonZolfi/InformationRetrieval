package Queries;

import IO.CorpusDocument;
import IO.ReadFile;
import Model.*;
import Parse.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Searcher {
    private String postingPath;
    private boolean stem;

    public Searcher(String postingPath, boolean stem) {
        this.postingPath = postingPath;
        this.stem = stem;
    }

    public void getQueryResults(Query q) {
        Parse p = new Parse(new CorpusDocument("","","","",q.getTitle(),""),stem);
        MiniDictionary md = p.parse();
        Set<String> hs =  md.listOfWords();
        HashMap<String, Integer> wordsCount = putWordsInMap(hs);
        CaseInsensitiveMap wordsPosting = getWordsPosting(hs);
        HashSet<String> docCloseList = new HashSet<>();
        Ranker ranker = new Ranker(wordsCount, wordsPosting);
        for (String word : wordsCount.keySet()) {
            if (!wordsPosting.get(word).equals("")) {
                String postingLine = wordsPosting.get(word);
                String[] split = postingLine.split("\\|");
                for (String aSplit : split) {
                    String[] splitLine = aSplit.split(",");
                    String docName = splitLine[0];
                    if (splitLine.length>1 && !docCloseList.contains(docName)) {
                        ranker.BM25AndPLN(docName, 2, 0.75);
                        ranker.tfIdf(docName);
                    }
                    docCloseList.add(docName);
                }
            }
        }
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
