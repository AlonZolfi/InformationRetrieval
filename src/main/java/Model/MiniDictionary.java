package Model;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class MiniDictionary {
    private String m_name; //name of the doc that past pars
    private Map<String,LinkedList> dictionary; //string - the term ; int - TF in the doc

    public MiniDictionary (String name){
        m_name=name;
        dictionary = new HashMap<String, LinkedList>();
    }

    public void addWord(String word,int placeInText){
        if (!containsKey(word)) {
            LinkedList root = new LinkedList();
            root.add(placeInText);
            dictionary.put(word, root);
        }
        else {
            LinkedList linkl = dictionary.get(word);
            linkl.add(placeInText);
        }
    }

    public boolean containsKey(String word){
        return dictionary.containsKey(word);
    }

    public int size(){
        return dictionary.size();
    }

    public String getName() {
        return m_name;
    }

    public int getFrequency(String word){
        if (size()>0 && containsKey(word))
            return dictionary.get(word).size();
        return 0;
    }

    public int giveMaxFrequency(){
        int max = 0;
        for (String word: dictionary.keySet() ) {
            int tmp= getFrequency(word);
            if (max <= tmp)
                max = tmp;
        }
        return max;
    }

    public Set<String> listOfWords(){
        return dictionary.keySet();
    }

    public LinkedList<Integer> listOfIndexes(String word){
        if (containsKey(word))
            return dictionary.get(word);
        return null;
    }

    public int numOfUniqueWords(){
        int count=0;
        for (String word: dictionary.keySet() ) {
            int tmp= getFrequency(word);
            if (tmp==1)
                count++;
        }
        return count;
    }

    public void listOfData(){
        //take all of the words in the mini dic and save the data for them in a **tmp** file(?)
        // under the name "name" and contains "word" | "num of apirens" | "where" (list)
        System.out.println("****"+getName()+"****");
        for (String word: listOfWords()){
            if (listOfIndexes(word)!=null)
                System.out.println(""+word+" "+getFrequency(word)+" "+listOfIndexes(word));
        }
    }

}
