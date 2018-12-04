package IO;

import Index.InvertedIndex;
import Index.CityInfoNode;
import Index.DocDictionaryNode;
import javafx.util.Pair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

public class WriteFile {

    public static void writeTmpPosting(String path, int i , HashMap<String, Pair<Integer,StringBuilder>> temporaryPosting) {
        //get all the info needed and write it to dest
        StringBuilder toWrite = new StringBuilder();
        TreeMap<String, Pair<Integer, StringBuilder>> sorted = new TreeMap<>(String::compareToIgnoreCase);
        sorted.putAll(temporaryPosting);
        String [] words = sorted.keySet().toArray(new String[0]);
        for (int j = 0; j <sorted.size() ; j++) {
            int shows = sorted.get(words[j]).getKey();
            StringBuilder stringBuilder = sorted.get(words[j]).getValue();
            toWrite.append(words[j]).append("~").append(shows).append("~").append(stringBuilder+"\n");
        }
        File dir = new File(path);
        File actualFile = new File(dir,"posting_"+i+".txt");
        write(actualFile,toWrite);
    }

    public static void writeDocDictionary(String path, LinkedList<DocDictionaryNode> documentDictionary, boolean stem) {
        StringBuilder toWrite = new StringBuilder();
        for (DocDictionaryNode cur :documentDictionary) {
            toWrite.append(cur.toString());
        }
        File dir = new File(path);
        String fileName = "StemDocumentDictionary.txt";
        if (!stem)
            fileName= "DocumentDictionary.txt";
        File actualFile = new File(dir,fileName);
        write(actualFile,toWrite);
    }

    public static void writeInvertedFile(String path, InvertedIndex invertedIndex, boolean stem) {
        String toWrite = invertedIndex.toString();
        File dir = new File(path);
        String fileName = "StemInvertedFile.txt";
        if (!stem)
            fileName= "InvertedFile.txt";
        File actualFile = new File(dir,fileName);
        write(actualFile,new StringBuilder(toWrite));
    }

    public static void writeCityDictionary(String path, HashMap<String, CityInfoNode> cityDictionary){
        StringBuilder toWrite = new StringBuilder();
        for (CityInfoNode cur : cityDictionary.values()) {
            toWrite.append(cur.toString());
        }
        File dir = new File(path);
        String fileName= "CityDictionary.txt";
        File actualFile = new File(dir,fileName);
        write(actualFile,toWrite);
    }

    private static void write(File actualFile, StringBuilder toWrite){
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(actualFile);
            fileWriter.write(toWrite.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void writeToEndOfFile(String fileName, LinkedList<StringBuilder> finalPostingLine) {
        try {
            StringBuilder ans = new StringBuilder();
            for (StringBuilder s : finalPostingLine)
                ans.append(s+"\n");
            File file = new File(fileName);
            FileWriter fileWriter = new FileWriter(file,true);
            fileWriter.write(ans.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
