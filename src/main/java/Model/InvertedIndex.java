package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class InvertedIndex {
    // term  | num Of appearance | pointer(path of posting file, line number in the posting)
    private ConcurrentHashMap<String,InvertedIndexNode> invertedIndexDic;

    public InvertedIndex() {
        invertedIndexDic = new ConcurrentHashMap<>();
    }

    public void addTerm (String term){
        if (!invertedIndexDic.containsKey(term)){
            InvertedIndexNode first = new InvertedIndexNode(term,1,null,-1);
            invertedIndexDic.put(term,first);
        }
        else{
            //if the word already exist we shold fo to the posting and add the new postin to him
            /*InvertedIndexNode notFirst = invertedIndexDic.remove(term);
            notFirst.increaseAppearances();
            invertedIndexDic.put(term,notFirst);
        */
            invertedIndexDic.get(term).increaseAppearances();
        }
    }

    public int getNumOfUniqueTerms(){
        return invertedIndexDic.size();
    }

    public ObservableList<ShowDictionaryRecord> getRecords() {
        ObservableList<ShowDictionaryRecord> showDictionaryRecords = FXCollections.observableArrayList();
        TreeMap<String,InvertedIndexNode> sorted = new TreeMap<String, InvertedIndexNode>(invertedIndexDic);
        for (String s : sorted.keySet())
            showDictionaryRecords.add(new ShowDictionaryRecord(s,invertedIndexDic.get(s).getNumOfAppearances()+""));
        return showDictionaryRecords;
    }
}
