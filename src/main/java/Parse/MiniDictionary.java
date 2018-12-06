package Parse;

import Index.StringNaturalOrderComparator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class MiniDictionary {
    private String m_name; //name of the doc that past pars
    private Map<String,LinkedList<Integer>> m_dictionary; //string - the term ; int - TF in the doc
    private int m_maxFreq;
    private String m_city;
    private String m_maxFreqWord;

    public MiniDictionary (String name, String city){
        m_name=name;
        m_city = city;
        m_dictionary = new HashMap<String, LinkedList<Integer>>();
        m_maxFreq = 0;
        m_maxFreqWord = "";
    }


    public void addWord(String word,int placeInText){
        LinkedList<Integer> currentPositions;
        if (containsKey(word)==0){
            if (Character.isUpperCase(word.charAt(0)))
                word=word.toUpperCase();
            else
                word=word.toLowerCase();
            currentPositions = new LinkedList<>();
            currentPositions.add(placeInText);
            m_dictionary.put(word, currentPositions);
        }
        else if (containsKey(word)==1){
            if (Character.isUpperCase(word.charAt(0))){
                currentPositions = m_dictionary.get(word.toUpperCase());
                currentPositions.add(placeInText);
            }else {
                currentPositions = m_dictionary.remove(word.toUpperCase());
                currentPositions.add(placeInText);
                m_dictionary.put(word.toLowerCase(),currentPositions);
            }
        }
        else {
            word=word.toLowerCase();
            currentPositions = m_dictionary.get(word);
            currentPositions.add(placeInText);
        }
        if (m_maxFreq < currentPositions.size()) {
            m_maxFreq = currentPositions.size();
            m_maxFreqWord = word;
        }
    }

    public int containsKey(String word){
        String upper = word.toUpperCase();
        String lower = word.toLowerCase();
        if(m_dictionary.containsKey(upper))
            return 1;
        if(m_dictionary.containsKey(lower))
            return 2;
        return 0;
    }

    public int size(){
        return m_dictionary.size();
    }

    public String getName() {
        return m_name;
    }

    public int getFrequency(String word){
        if (size()>0 && containsKey(word)!=0)
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
        if (containsKey(word)!=0)
            return m_dictionary.get(word);
        return null;
    }

    public String listOfData(String word){
        //take all of the words in the mini dic and save the data for them in a **tmp** file(?)
        // under the name "name" and contains "word" | "num of apirens" | "where" (list)
        return ""+m_name+","+getFrequency(word)+","+listOfIndexes(word);

    }

    public String getCity() {
        return m_city;
    }

    public void setCity(String city) {
        m_city=city;
    }

    public String getMaxFreqWord() {
        return m_maxFreqWord;
    }
}
