package Model;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class MiniDictionary {
    private String m_name; //name of the doc that past pars
    private Map<String,LinkedList> m_dictionary; //string - the term ; int - TF in the doc
    private int m_maxFreq;
    private String m_city;

    public MiniDictionary (String name, String city){
        m_name=name;
        m_city = city;
        m_dictionary = new HashMap<String, LinkedList>();
        m_maxFreq = 1;
    }

    public void addWord(String word,int placeInText){
        if (!containsKey(word)) {
            LinkedList root = new LinkedList();
            root.add(placeInText);
            m_dictionary.put(word, root);
        }
        else {
            LinkedList linkl = m_dictionary.get(word);
            linkl.add(placeInText);
            if (m_maxFreq < linkl.size())
                m_maxFreq = linkl.size();
        }
    }

    public boolean containsKey(String word){
        return m_dictionary.containsKey(word);
    }

    public int size(){
        return m_dictionary.size();
    }

    public String getName() {
        return m_name;
    }

    public int getFrequency(String word){
        if (size()>0 && containsKey(word))
            return m_dictionary.get(word).size();
        return 0;
    }

    public int getMaxFrequency(){
        return m_maxFreq;
    }

    public Set<String> listOfWords(){
        return m_dictionary.keySet();
    }

    public LinkedList<Integer> listOfIndexes(String word){
        if (containsKey(word))
            return m_dictionary.get(word);
        return null;
    }

    public String listOfData(String word){
        //take all of the words in the mini dic and save the data for them in a **tmp** file(?)
        // under the name "name" and contains "word" | "num of apirens" | "where" (list)
        return ""+m_name+" "+getFrequency(word)+" "+listOfIndexes(word);

    }

    public String getCity() {
        return m_city;
    }
}
