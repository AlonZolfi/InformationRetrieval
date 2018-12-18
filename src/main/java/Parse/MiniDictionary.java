package Parse;

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

    /**
     * create new MiniDictionary
     * @param name name of the file and doc
     * @param city city of the doc
     */
    MiniDictionary (String name, String city){
        m_name=name;
        m_city = city;
        m_dictionary = new HashMap<>();
        m_maxFreq = 0;
        m_maxFreqWord = "";
    }

    /**
     * adds a term to the dictionary
     * @param word the term to be added
     * @param placeInText the index of the term in the text
     */
    void addWord(String word,int placeInText){
        LinkedList<Integer> currentPositions;
        //adds the word according to parsing rule 2
        int result = containsKey(word);
        if (result==0){
            if(Character.isLetter(word.charAt(0))) {
                if (Character.isUpperCase(word.charAt(0)))
                    word = word.toUpperCase();
                else
                    word = word.toLowerCase();
            }
            currentPositions = new LinkedList<>();
            currentPositions.add(placeInText);
            m_dictionary.put(word, currentPositions);
        }
        else if (result==1){
            if (Character.isUpperCase(word.charAt(0))){
                currentPositions = m_dictionary.get(word.toUpperCase());
                currentPositions.add(placeInText);
            }else {
                currentPositions = m_dictionary.remove(word.toUpperCase());
                currentPositions.add(placeInText);
                m_dictionary.put(word.toLowerCase(),currentPositions);
            }
        }
        else if(result==2){
            word=word.toLowerCase();
            currentPositions = m_dictionary.get(word);
            currentPositions.add(placeInText);
        }
        else{
            currentPositions = m_dictionary.get(word);
            currentPositions.add(placeInText);
        }
        //check if max freq has changed
        if (m_maxFreq < currentPositions.size()) {
            m_maxFreq = currentPositions.size();
            m_maxFreqWord = word;
        }
    }

    /**
     * checks if the term has been added in any case
     * @param word the term to be checked
     * @return returns 1 if exists in upper case, 2 if exists in lower case and 0 if doesnt exist
     */
    private int containsKey(String word){
        String upper = word.toUpperCase();
        String lower = word.toLowerCase();
        if(m_dictionary.containsKey(upper))
            return 1;
        if(m_dictionary.containsKey(lower))
            return 2;
        if(!Character.isLetter(word.charAt(0)) && m_dictionary.containsKey(word))
            return 3;
        return 0;
    }

    /**
     * returns all the terms in the dictionary
     * @return all the terms in the dictionary
     */
    public Set<String> listOfWords(){
        return m_dictionary.keySet();
    }

    /**
     * returns the data about a certain term
     * @param word the term
     * @return the data about a certain term
     */
    public String listOfData(String word){
        return ""+m_name+","+getFrequency(word)+","+ printIndexes(getIndexesOfWord(word));
    }

    private String printIndexes(LinkedList<Integer> indexesOfWord) {
        StringBuilder s = new StringBuilder("[");
        for (Integer i: indexesOfWord) {
            s.append(i).append("-");
        }
        s.replace(s.length()-1,s.length(),"]");
        return s.toString();
    }

    /**
     * returns the size of the dictionary
     * @return the size of the dictionary
     */
    public int size(){
        return m_dictionary.size();
    }

    /**
     * returns the indexes of the term
     * @param word the term
     * @return the indexes of the term
     */
    private LinkedList<Integer> getIndexesOfWord(String word){
        if (containsKey(word)!=0)
            return m_dictionary.get(word);
        return null;
    }

    /**
     * returns the word that has the max freq in the doc
     * @return the word that has the max freq in the doc
     */
    public String getMaxFreqWord() {
        return m_maxFreqWord;
    }

    /**
     * returns the name of the doc
     * @return the name of the doc
     */
    public String getName() {
        return m_name;
    }

    /**
     * returns the freq of a certain term
     * @param word the term
     * @return the freq of a certain term
     */
    public int getFrequency(String word){
        if (size()>0 && containsKey(word)!=0)
            return m_dictionary.get(word).size();
        return 0;
    }

    /**
     * returns the max freq exists in a doc
     * @return the max freq exists in a doc
     */
    public int getMaxFrequency(){
        return m_maxFreq;
    }

    /**
     * returns the city name of the doc
     * @return the city name of the doc
     */
    public String getCity() {
        return m_city;
    }

    public int getDocLength(){
        int count = 0;
        for (LinkedList l: m_dictionary.values()) {
            count += l.size();
        }
        return count;
    }
}
