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

    public InvertedIndex() {
        invertedIndexDic = new ConcurrentHashMap<>();
    }

    public InvertedIndex(File file) {
        String line = null;
        invertedIndexDic = new ConcurrentHashMap<String, InvertedIndexNode>();
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            line = bufferedReader.readLine();
            while(line != null) {
                String [] curLine = line.split("\t");
                InvertedIndexNode cur = new InvertedIndexNode(curLine[0],Integer.parseInt(curLine[1]),Integer.parseInt(curLine[2]),curLine[3],Integer.parseInt(curLine[4]));
                invertedIndexDic.put(curLine[0],cur);
                line = bufferedReader.readLine();
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addTerm (String term) {
        if (term.charAt(0) < 123) {
            if (!invertedIndexDic.containsKey(term)) {
                InvertedIndexNode first = new InvertedIndexNode(term, 1, -1, null, -1);
                invertedIndexDic.put(term, first);
            }
            else {
                invertedIndexDic.get(term).increaseTermFreq(1);
            }
        }
        if(term.equals("pampa"))
            System.out.println("pampa small is in the invertedIndex");
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

    public void setPointer(String minTerm, String fileName, int lineNumber){
        invertedIndexDic.get(minTerm).setPointer(fileName,lineNumber);
    }

    public void setNumOfAppearance(String term, int numOfAppearance){
        invertedIndexDic.get(term).setNumOfAppearance(numOfAppearance);
    }
    @Override
    public String toString() {
        StringBuilder toWrite=new StringBuilder();
        for (InvertedIndexNode cur :invertedIndexDic.values()) {
            toWrite.append(cur.toString());
        }
        return toWrite.toString();
    }

    public String getPostingLink(String word){
        if (invertedIndexDic.get(word)==null)
            return "posting dose not found";
        return invertedIndexDic.get(word).getPostingLink();
    }

    public void deleteEntriesOfIrrelevant() {
        for (String s: invertedIndexDic.keySet()){
            InvertedIndexNode cur = invertedIndexDic.get(s);
            if(cur.getNumOfAppearances()==-1) {
                int termFreqCur = cur.getTermFreq();
                if(invertedIndexDic.get(s.toLowerCase())!=null)
                    invertedIndexDic.get(s.toLowerCase()).increaseTermFreq(termFreqCur);
                else
                    System.out.println(s);
                invertedIndexDic.remove(s);
            }
        }
    }
}
