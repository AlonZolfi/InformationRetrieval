package IO;

import Index.InvertedIndex;
import Index.CityInfoNode;
import Index.DocDictionaryNode;
import Index.StringNaturalOrderComparator;
import javafx.util.Pair;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class WriteFile {
    public static void writeTmpPosting(String path, int i , HashMap<String, Pair<Integer,StringBuilder>> temporaryPosting) {
        //get all the info needed and write it to dest
        StringBuilder toWrite = new StringBuilder();
        LinkedList<String> sorted = new LinkedList<>(temporaryPosting.keySet());
        sorted.sort(new StringNaturalOrderComparator());
        for (String s : sorted) {
            int shows = temporaryPosting.get(s).getKey();
            StringBuilder stringBuilder = temporaryPosting.get(s).getValue();
            toWrite.append(s).append("~").append(shows).append("~").append(stringBuilder).append("\n");
        }
        File dir = new File(path);
        File actualFile = new File(dir, "posting_" + i + ".txt");
        write(actualFile, toWrite);

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
        write(actualFile,new StringBuilder("word\ttermFreq\tnum Of Appearances\tposting Link\tposting Line\n"+toWrite));
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
            toWrite.delete(0,toWrite.length());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*public static void writeToEndOfFile(String fileName, LinkedList<StringBuilder> finalPostingLine) {
        StringBuilder ans = new StringBuilder();
        StringBuilder[] sbArray = finalPostingLine.toArray(new StringBuilder[0]);
        File file = new File(fileName);
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            for (int i = 0; i <= sbArray.length / 1000; i++) {
                for (int j = 0; j < 1000 && (j + i * 1000) < sbArray.length; j++) {
                    ans.append(sbArray[j + i * 1000] + "\n");
                }
                fileWriter.write(ans.toString());
                ans.delete(0, ans.length());
            }
            fileWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    public static void writeToEndOfFile(String fileName, HashMap<String, StringBuilder> finalPostingLine) {
        StringBuilder ans = new StringBuilder();
        StringBuilder[] sbArray = finalPostingLine.values().toArray(new StringBuilder[0]);
        File file = new File(fileName);
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            for (int i = 0; i <= sbArray.length / 1000; i++) {
                for (int j = 0; j < 1000 && (j + i * 1000) < sbArray.length; j++) {
                    ans.append(sbArray[j + i * 1000]).append("\n");
                }
                fileWriter.write(ans.toString());
                ans.delete(0, ans.length());
            }
            fileWriter.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
