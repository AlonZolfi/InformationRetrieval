package Queries;

import IO.ReadFile;
import Model.*;
import Parse.*;
import javafx.util.Pair;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.Callable;

import static java.util.Collections.reverseOrder;

public class Searcher implements Callable<LinkedList<String>> {
    private String postingPath;//path of the postings
    private boolean stem;//whether words should be stemmed
    private boolean semantics;//if we should improve results with semantics
    private Query q;//the query

    public Searcher(String postingPath, boolean stem, boolean semantics, Query q) {
        this.postingPath = postingPath;
        this.stem = stem;
        this.semantics = semantics;
        this.q =q;
    }

    @Override
    public LinkedList<String> call() throws Exception {
        return getQueryResults();
    }

    /**
     * Search for relevant documents for the given query
     * @return relevant documents
     */
    private LinkedList<String> getQueryResults() {
        //parse query
        Parse p = new Parse(new CorpusDocument("","","","",q.getTitle() +" " + q.getDescription(),"",""),stem);
        MiniDictionary md = p.parse(true);
        HashMap<String, Integer> wordsCountInQuery = md.countAppearances(); //count word in the query

        //search for semantic words if asked for
        Set<String> semanticWords = new HashSet<>();
        if(semantics)
            semanticWords = improveWithSemantics(wordsCountInQuery, q.getTitle().toLowerCase());

        //prepare for calculation
        CaseInsensitiveMap wordsPosting = getPosting(wordsCountInQuery.keySet());

        //get all doc occurences of the cities
        HashSet<String> docsByCitiesFilter = getCitiesDocs(getPosting(Model.usedCities));

        //objects for the iteration
        Ranker ranker = new Ranker(wordsCountInQuery);
        HashMap<String, Double> score = new HashMap<>();

        //for each word go throw its posting with relevant documents
        for (String word : wordsCountInQuery.keySet()) {
            if (!wordsPosting.get(word).equals("")) {
                String postingLine = wordsPosting.get(word);
                String[] split = postingLine.split("\\|");
                double idf = getIDF(split.length-1);
                double weight = 1;
                if(semanticWords.contains(word))
                    weight = 0.35;
                else if (word.contains("-"))
                    weight = 1.15;
                for (String aSplit : split) {
                    String[] splitLine = aSplit.split(",");
                    String docName = splitLine[0];
                    if (splitLine.length>1 &&(Model.usedCities.size()==0 || isInFilter(Model.documentDictionary.get(docName).getCity())) || docsByCitiesFilter.contains(docName)) {
                        if (Model.usedLanguages.size() == 0 || Model.usedLanguages.contains(Model.documentDictionary.get(docName).getDocLang())) {
                            int tf = Integer.parseInt(splitLine[1]);
                            double BM25 = weight * ranker.BM25(word, docName, tf, idf);
                            addToScore(score, docName, BM25);
                            calculateDocTitle(score, docName, wordsPosting.keySet());
                        }
                    }
                }
            }
        }
        calculate5Entities(score,wordsCountInQuery.keySet(),semanticWords);
        return sortByScore(score);
    }

    /**
     * adds to the score the 5 entities algorithm
     * @param score score map
     * @param wordsInQuery words of query
     * @param semanticWords words of semnatic
     */
    private void calculate5Entities(HashMap<String, Double> score, Set<String> wordsInQuery, Set<String> semanticWords) {
        for (String docName: score.keySet()){
            Pair<String,Integer>[] five = Model.documentDictionary.get(docName).getPrimaryWords();
            if(five!=null){
                for (Pair<String, Integer> aFive : five) {
                    if (aFive != null && wordsInQuery.contains(aFive.getKey()) && !semanticWords.contains(aFive.getKey())) {
                        addToScore(score, docName, 0.1);
                    }
                }
            }
        }
    }

    /**
     * calculates cosine simularity of vectors to get semantic words
     * @param wordsMap words to get semantic words for
     * @param query actual query
     * @return the semantic words
     */
    private HashSet<String> improveWithSemantics(HashMap<String,Integer> wordsMap, String query) {
        HashSet<String> result = new HashSet<>();
        //get the hash map of the GLOVE file
        try {
            Manager.m.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(Manager.vectors==null)
            Manager.vectors = buildSemanticMap();
        Manager.m.release();
        //go throw each word and search for semantic words
        String[] split = StringUtils.split(query," ~;!?=#&^*+\\|:\"(){}[]<>\n\r\t");
        for (String word:split) {
            if (Manager.vectors!=null && Manager.vectors.containsKey(word)) {
                double[] wordVector = Manager.vectors.get(word);
                for (Map.Entry<String,double[]> vec:Manager.vectors.entrySet()) {
                    double mone = 0;
                    double mecaneword = 0;
                    double mecaneVec = 0;
                    if (wordExistsInQuery(split, vec.getKey()) || vec.getValue().length != wordVector.length)
                        continue;
                    int end = Math.min(vec.getValue().length-1,wordVector.length);
                    //calculate similarity
                    for (int i = 0; i < end-1; i++) {
                        mone += wordVector[i] * vec.getValue()[i];
                        mecaneword += Math.pow(wordVector[i], 2);
                        mecaneVec += Math.pow(vec.getValue()[i], 2);
                    }
                    double res = mone / (Math.sqrt(mecaneVec) * Math.sqrt(mecaneword));
                    if (res >= 0.83 && !Model.invertedIndex.getPostingLink(vec.getKey()).equals("")) {
                        String newSemWord = vec.getKey();
                        if(stem){
                            Parse p = new Parse(new CorpusDocument("","","","",vec.getKey(),"",""),stem);
                            MiniDictionary md = p.parse(true);
                            newSemWord = md.getMaxFreqWord();
                        }
                        if(!wordExistsInMap(wordsMap,newSemWord)) {
                            wordsMap.put(newSemWord, 1);
                            result.add(newSemWord);
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * checks if the word exists in the query
     * @param split words in the query
     * @param key the word
     * @return true if exists false otherwise
     */
    private boolean wordExistsInQuery(String[] split, String key) {
        for (String word:split)
            if (word.equals(key))
                return true;
        return false;
    }

    /**
     * checks if a word exists in a map
     * @param wordsMap map of words
     * @param newSemWord the word
     * @return true if exists, false otherwise
     */
    private boolean wordExistsInMap(HashMap<String, Integer> wordsMap, String newSemWord){
        return wordsMap.containsKey(newSemWord.toLowerCase()) || wordsMap.containsKey(newSemWord.toUpperCase());
    }

    /**
     * builds the semantic map according to the file
     * @return map - words as keys and vectors as values
     */
    private HashMap<String, double[]> buildSemanticMap() {
        HashMap<String, double[]> result = new HashMap<>();
        List<String> vectors = ReadFile.fileToList("src/glove.txt");
        for (String line : vectors) {
            String[] split = line.split(" ");
            double[] values = new double[split.length - 1];
            for (int i = 1; i < split.length; i++) {
                values[i - 1] = Double.parseDouble(split[i]);
            }
            result.put(split[0], values);
        }
        return result;
    }

    /**
     * calculates the doc title with the query
     * @param score map of scores
     * @param docName document name
     * @param wordsSet words of query
     */
    private void calculateDocTitle(HashMap<String, Double> score, String docName, Set<String> wordsSet) {
        //go throw all words of title and check if words appear in the query
        String title = Model.documentDictionary.get(docName).getTitle().toLowerCase();
        if(!title.equals("")){
            String[] split = StringUtils.split(title," ~;!?=#&^*+\\|:\"(){}[]<>\n\r\t");
            for (String wordTitle: split) {
                if (wordsSet.contains(wordTitle))
                    addToScore(score,docName,0.1);
            }
        }
    }

    /**
     * returns the docs that are relevant for the filtered cities (where the city appears in the text)
     * @param postings posting lines for the cities
     * @return relevant docs
     */
    private HashSet<String> getCitiesDocs(CaseInsensitiveMap postings) {
        HashSet<String> citiesDocs = new HashSet<>();
        for (String postingLine:postings.values()) {
            String[] split = postingLine.split("\\|");
            for (String docDetails: split) {
                String[] splitLine = docDetails.split(",");
                citiesDocs.add(splitLine[0]);
            }
        }
        return citiesDocs;
    }

    /**
     * checks if documents city is in the city filter
     * @param city_name name of the city
     * @return true if is in filter, false otherwise
     */
    private boolean isInFilter(String city_name) {
        for (String city: Model.usedCities){
            if(city.equals(city_name))
                return true;
        }
        return false;
    }

    /**
     * sort the scores of documents from higher to lower
     * @param score scores to sort
     * @return sorted list
     */
    private LinkedList<String> sortByScore(HashMap<String, Double> score) {
        List<Map.Entry<String,Double>> list = new ArrayList<>(score.entrySet());
        list.sort(reverseOrder(Map.Entry.comparingByValue()));

        LinkedList<String> result= new LinkedList<>();
        for(Map.Entry<String,Double> entry: list){
            result.add(entry.getKey());
        }
        return result;
    }

    /**
     * adds the new score to the score of the document
     * @param score scores of documents
     * @param docName document name
     * @param newScore new score be added
     */
    private void addToScore(HashMap<String, Double> score, String docName, double newScore) {
        if(newScore!=0) {
            Double d = score.get(docName);
            if (d != null)
                newScore += d;
            score.put(docName, newScore);
        }
    }

    /**
     * returns the IDF of a word in the corpus
     * @param length number of docs the word appears in
     * @return calculated IDF
     */
    private Double getIDF(int length) {
        double docInCorpusCount = Model.documentDictionary.keySet().size();
        return Math.log10((docInCorpusCount+1)/length);
    }

    /**
     * gets the posting of all query words
     * @param query query words
     * @return the postings of the words
     */
    private CaseInsensitiveMap getPosting(Set<String> query) {
        CaseInsensitiveMap words = new CaseInsensitiveMap();
        HashMap<Character, LinkedList<Integer>> allCharactersTogether = new HashMap<>();
        for (String word: query) {
            char letter;
            if(!Character.isLetter(word.charAt(0)))
                letter = '`';
            else
                letter = Character.toLowerCase(word.charAt(0));
            String lineNumber = getPostingLineNumber(word);
            if(!lineNumber.equals("")) {
                if (allCharactersTogether.containsKey(letter))
                    allCharactersTogether.get(letter).add(Integer.parseInt(lineNumber));
                else {
                    LinkedList<Integer> lettersLines = new LinkedList<>();
                    lettersLines.add(Integer.parseInt(lineNumber));
                    allCharactersTogether.put(letter,lettersLines);
                }
            }
            else
                words.put(word,"");
        }
        for (Character letter: allCharactersTogether.keySet()) {
            LinkedList<String> postings= ReadFile.readPostingLineAtIndex(postingPath,Character.toLowerCase(letter),allCharactersTogether.get(letter),stem);
            for (String posting: postings) {
                String[] wordAndRestOfPosting = posting.split("~");
                words.put(wordAndRestOfPosting[0],wordAndRestOfPosting[1]);
            }
        }
        return words;
    }

    /**
     * returns the posting line number in the file
     * @param word the word
     * @return line number
     */
    private String getPostingLineNumber(String word){
        String lineNumber = Model.invertedIndex.getPostingLink(word.toLowerCase());
        if(lineNumber.equals(""))
            lineNumber = Model.invertedIndex.getPostingLink(word.toUpperCase());
        return lineNumber;
    }
}
