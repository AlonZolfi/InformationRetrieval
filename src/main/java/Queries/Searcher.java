package Queries;

import IO.ReadFile;
import Index.DocDictionaryNode;
import Model.*;

import java.util.HashMap;
import java.util.HashSet;

public class Searcher {
    private String postingPath;
    private boolean stem;

    public Searcher(String postingPath, boolean stem) {
        this.postingPath = postingPath;
        this.stem = stem;
    }

    public void getQueryResults(Query q){
        String query = q.getTitle();
        HashMap<String, Integer> wordsQuery = putWordsInMap(query);
        double averageDocumentLength= getDocumentAverageLength();
        HashSet<String> docCloseList = new HashSet<>();
        for (String word:wordsQuery.keySet()) {
            String lineNumber = getPostingLineNumber(word);
            if(!lineNumber.equals("")){
                String postingLine = ReadFile.readPostingLineAtIndex(postingPath,word.charAt(0),Integer.parseInt(lineNumber),stem);

            }

        }
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

    private String getPostingLineNumber(String word){
        String lineNumber = Model.invertedIndex.getPostingLink(word.toLowerCase());
        if(lineNumber.equals(""))
            lineNumber = Model.invertedIndex.getPostingLink(word.toUpperCase());
        return lineNumber;
    }
}
