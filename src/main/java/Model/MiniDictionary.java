package Model;


import java.util.HashMap;
import java.util.Map;

public class MiniDictionary {
    private String m_name; //name of the doc that past pars
    private Map<String,Integer> dictionary; //string - the term ; int - TF in the doc


    public MiniDictionary (String name){
        m_name=name;
        dictionary = new HashMap<String, Integer>();
    }

    public void addWord(String word){
        if (!dictionary.containsKey(word))
            dictionary.put(word,1);
        else {
            int i = dictionary.get(word);
            dictionary.remove(word);
            dictionary.put(word, i+1);
        }
    }

    public boolean containsKey( String word){
        return dictionary.containsKey(word);
    }

    public int size(){
        return dictionary.size();
    }

    public String getName() {
        return m_name;
    }

    public int giveMaxFrequency(){
        return 0;
    }




}
