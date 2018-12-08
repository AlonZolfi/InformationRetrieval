package Index;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

public class InvertedIndex {
    // term  | num Of appearance | pointer(path of posting file, line number in the posting)
    private ConcurrentHashMap<String, InvertedIndexNode> invertedIndexDic;

    /**
     * create a new inverted index
     */
    public InvertedIndex() {
        invertedIndexDic = new ConcurrentHashMap<>();
    }

    /**
     * construct a inverted index from a file
     * @param file the file that has the data about he inverted file
     */
    public InvertedIndex(File file) {
        String line = null;
        invertedIndexDic = new ConcurrentHashMap<String, InvertedIndexNode>();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            line = bufferedReader.readLine();
            while(line != null) {
                String [] curLine = line.split("\t");
                InvertedIndexNode cur = new InvertedIndexNode(curLine[0],Integer.parseInt(curLine[1]),Integer.parseInt(curLine[2]),Integer.parseInt(curLine[3]));
                invertedIndexDic.put(curLine[0],cur);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * adds a term to the inverted index
     * @param term the term to be added
     */
    public void addTerm (String term) {
        if (term.charAt(0) < 123) {
            if (!invertedIndexDic.containsKey(term)) {//if the term doesn't exist in the inverted index
                InvertedIndexNode first = new InvertedIndexNode(term, 1, -1, -1);
                invertedIndexDic.put(term, first);
            }
            else {
                invertedIndexDic.get(term).increaseTermFreq(1); // if the term exist in the inverted index increase number of freqenecy
            }
        }
        if(term.equalsIgnoreCase("\t0"))
            System.out.println("REALLYY??");
    }

    /**
     * deletes all the terms that appeared in different ways in the corpus (their appearance number was never updated)
     */
    public void deleteEntriesOfIrrelevant() {
        for (String s: invertedIndexDic.keySet()){
            InvertedIndexNode cur = invertedIndexDic.get(s);
            if(cur.getNumOfAppearances()==-1) {
                int termFreqCur = cur.getTermFreq();
                if(invertedIndexDic.get(s.toLowerCase())!=null)
                    invertedIndexDic.get(s.toLowerCase()).increaseTermFreq(termFreqCur);
                invertedIndexDic.remove(s);
            }
        }
    }

    /**
     * returns number of unique terms that exist in the inverted index
     * @return number of unique terms that exist in the inverted index
     */
    public int getNumOfUniqueTerms(){
        return invertedIndexDic.size();
    }

    /**
     * updates the pointer to the final posting
     * @param minTerm the term to sent the pointer
     * @param lineNumber the line in the posting file
     */
    public void setPointer(String minTerm, int lineNumber){
        if (invertedIndexDic.containsKey(minTerm))
            invertedIndexDic.get(minTerm).setPointer(lineNumber);
    }

    /**
     * sets the number of appearances of the term
     * @param term the term to be updated
     * @param numOfAppearance num of appearances
     */
    public void setNumOfAppearance(String term, int numOfAppearance){
        if (invertedIndexDic.containsKey(term))
            invertedIndexDic.get(term).setNumOfAppearance(numOfAppearance);
    }

    public String getPostingLink(String word){
        if (invertedIndexDic.get(word)==null)
            return "";
        return invertedIndexDic.get(word).getPostingLink();
    }

    public ObservableList<ShowDictionaryRecord> getRecords() {
        ObservableList<ShowDictionaryRecord> showDictionaryRecords = FXCollections.observableArrayList();
        TreeMap<String,InvertedIndexNode> sorted = new TreeMap<String, InvertedIndexNode>(invertedIndexDic);
        for (String s : sorted.keySet())
            showDictionaryRecords.add(new ShowDictionaryRecord(s,invertedIndexDic.get(s).getNumOfAppearances()+""));
        return showDictionaryRecords;
    }

    @Override
    public String toString() {
        StringBuilder toWrite=new StringBuilder();
        for (InvertedIndexNode cur :invertedIndexDic.values()) {
            toWrite.append(cur.toString());
        }
        return toWrite.toString();
    }
}
