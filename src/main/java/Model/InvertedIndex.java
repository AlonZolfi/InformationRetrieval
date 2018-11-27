package Model;

import javafx.util.Pair;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InvertedIndex {
    // term  | num Of appearance | pointer(path of posting file, line number in the posting)
    ConcurrentHashMap<Pair<String, Integer>, Pair<String, Integer>> invertedIndexDic;

    public void addTerm (String term, String posting){
        // if the term exists increase counter and add the new posting
        //otherwise
    }
}
