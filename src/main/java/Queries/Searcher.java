package Queries;

import IO.CorpusDocument;
import IO.ReadFile;
import Model.*;
import Parse.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;

import static java.util.Collections.reverseOrder;

public class Searcher {
    private String postingPath;
    private boolean stem;
    private boolean semantics;


    public Searcher(String postingPath, boolean stem, boolean semantics) {
        this.postingPath = postingPath;
        this.stem = stem;
        this.semantics = semantics;
    }

    public LinkedList<String> getQueryResults(Query q) {
        //parse query

        String s = q.getTitle();//createNewQuery(q);
        Parse p = new Parse(new CorpusDocument("","","","",s+ " " + q.getDescription(),""),stem);
        MiniDictionary md = p.parse(true);
        Set<String> hs =  new HashSet<>(md.listOfWords());
        if(semantics)
            improveWithSemantics(hs,q.getTitle());
        //prepare for calculation
        HashMap<String, Integer> wordsCountInQuery = putWordsInMap(hs);
        HashSet<String> docsByCitiesFilter = getCitiesDocs(getPosting(Model.usedCities));
        CaseInsensitiveMap wordsPosting = getPosting(wordsCountInQuery.keySet());
        //objects for the iteration
        Ranker ranker = new Ranker(wordsCountInQuery);
        HashMap<String, Double> score = new HashMap<>();


        for (String word : wordsCountInQuery.keySet())
        {
            if (!wordsPosting.get(word).equals("")) {
                String postingLine = wordsPosting.get(word);
                String[] split = postingLine.split("\\|");
                double idf = getIDF(split.length-1);
                for (String aSplit : split) {
                    String[] splitLine = aSplit.split(",");
                    String docName = splitLine[0];
                    if (splitLine.length>1 &&(Model.usedCities.size()==0 || isInFilter(Model.documentDictionary.get(docName).getCity())) || docsByCitiesFilter.contains(docName)) {
                        int tf = Integer.parseInt(splitLine[1]);
                        double BM25 = ranker.BM25(word,docName,tf,idf, 1.2, 0.75);
                        addToScore(score,docName,BM25);
                        //calculateDocTitle(score,docName,wordsPosting.keySet());
                    }
                }
            }
        }
        return sortByScore(score);
    }

    private void improveWithSemantics(Set<String> wordsSet, String hs) {
        String[] split = StringUtils.split(hs," ?=#&^*+\\|:\"(){}[]\n\r\t");
        HashMap<String,double[]> vectors = getVectorString();
        for (String word:split) {
            if (vectors!=null && vectors.containsKey(word)) {
                double[] wordVector = vectors.get(word);
                for (Map.Entry<String,double[]> vec:vectors.entrySet()) {
                    double mone = 0;
                    double mecaneword = 0;
                    double mecaneVec = 0;
                    if (vec.getValue().length!=wordVector.length) {
                        System.out.println("lll");
                        continue;
                    }
                    int end = Math.min(vec.getValue().length-1,wordVector.length);
                    for (int i = 0; i < end-1; i++) {
                        mone += wordVector[i] * vec.getValue()[i];
                        mecaneword += Math.pow(wordVector[i], 2);
                        mecaneVec += Math.pow(vec.getValue()[i], 2);
                    }
                    double res = mone / (Math.sqrt(mecaneVec) * Math.sqrt(mecaneword));
                    if (res >= 0.89)
                        wordsSet.add(vec.getKey());
                }
            }
        }
    }

    private HashMap<String, double[]> getVectorString() {
        HashMap<String, double[]> result = new HashMap<>();
        try {
            List<String> vectors = FileUtils.readLines(new File("src/glove.txt"));
            for (String line:vectors) {
                String[] split = line.split(" ");
                double[] values = new double[split.length-1];
                for (int i = 1; i < split.length; i++) {
                    values[i-1] = Double.parseDouble(split[i]);
                }
                result.put(split[0],values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private String createNewQuery(Query q) {
        StringBuilder queryTitle = new StringBuilder(q.getTitle().toLowerCase());
        String[] split = StringUtils.split(queryTitle.toString()," ?=#&^*+\\|:\"(){}[]\n\r\t");
        for (int i = 0; i < split.length; i++) {
            for (int j = 0; j < split.length; j++) {
                if (!getPostingLineNumber(split[i] + "-" + split[j]).equals("")) {
                    queryTitle.append(" ").append(split[i]).append("-").append(split[j]);
                }
            }
        }
        return  queryTitle.toString();

    }

    /*private void calculateDocTitle(HashMap<String, Double> score, String docName, Set<String> wordsSet) {
        String title = Model.documentDictionary.get(docName).getTitle().toLowerCase();
        if(!title.equals("")){
            String[] split = StringUtils.split(title," ?=#&^*+\\|:\"(){}[]\n\r\t");
            for (String wordTitle: split) {
                if (wordsSet.contains(wordTitle));
                    addToScore(score,docName,0.25);
            }

        }

    }*/

    private void putDescInMap(HashMap<String, Integer> wordsCountInQuery, String description) {
        String[] split = StringUtils.split(description," ?=#&^*+\\|:\"(){}[]\n\r\t");
        for (String word: split) {
            if(wordsCountInQuery.containsKey(word))
                wordsCountInQuery.replace(word,wordsCountInQuery.get(word)+1);
        }
    }

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

    private boolean isInFilter(String city_name) {
        for (String city: Model.usedCities){
            if(city.equals(city_name))
                return true;
        }
        return false;
    }

    private LinkedList<String> sortByScore(HashMap<String, Double> score) {
        List<Map.Entry<String,Double>> list = new ArrayList<>(score.entrySet());
        list.sort(reverseOrder(Map.Entry.comparingByValue()));

        LinkedList<String> result= new LinkedList<>();
        for(Map.Entry<String,Double> entry: list){
            result.add(entry.getKey());
        }
        return result;
    }

    private void addToScore(HashMap<String, Double> score, String docName, double newScore) {
        if(newScore!=0) {
            Double d = score.get(docName);
            if (d != null)
                newScore += d;
            score.put(docName, newScore);
        }
    }

    private Double getIDF(int length) {
        double docInCorpusCount = Model.documentDictionary.keySet().size();
        return Math.log10((docInCorpusCount+1)/length);
    }

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

    private HashMap<String, Integer> putWordsInMap(Set<String> query) {
        HashMap<String,Integer> words = new HashMap<>();
        for (String word: query) {
            if (!words.containsKey(word) && !getPostingLineNumber(word).equals(""))
                words.put(word, 1);
            else if(words.containsKey(word))
                words.replace(word, words.get(word) + 1);

        }
        return words;
    }

    private String getPostingLineNumber(String word){
        String lineNumber = Model.invertedIndex.getPostingLink(word.toLowerCase());
        if(lineNumber.equals(""))
            lineNumber = Model.invertedIndex.getPostingLink(word.toUpperCase());
        return lineNumber;
    }
}
