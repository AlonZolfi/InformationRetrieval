package Model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class WriteFile {

    public static void writeTmpPosting(String path, int i , HashMap<String, StringBuilder> temporaryPosting) throws IOException {
        //get all the info needed and write it to dest
        StringBuilder toWrite = new StringBuilder();
        String [] words = temporaryPosting.keySet().toArray(new String[0]);
        for (int j = 0; j <temporaryPosting.size() ; j++) {
            toWrite.append(words[j]+" ");
            toWrite.append(temporaryPosting.get(words[j])+"\n");
        }

        File dir = new File(path);
        File actualFile = new File(dir,"posting_"+i+".txt");
        FileWriter fileWriter = new FileWriter(actualFile);
        fileWriter.write(toWrite.toString());
        fileWriter.close();
    }

    public static void writeDocDictionary(String path, LinkedList<DocDictionaryNode> documentDictionary) throws IOException {
        StringBuilder toWrite = new StringBuilder();
        for (DocDictionaryNode cur:documentDictionary) {
            toWrite.append(cur.getDocName()+"\t");
            toWrite.append(cur.getNumOfUniWords()+"\t");
            toWrite.append(cur.getMaxFreq()+"\t");
            toWrite.append(cur.getCity()+"\n");
        }
        File dir = new File(path);
        File actualFile = new File(dir,"docomentDictionay.txt");
        FileWriter fileWriter = new FileWriter(actualFile);
        fileWriter.write(toWrite.toString());
        fileWriter.close();
    }


    public String[][] showDictionay(){
        //fix the dic to string [][] of word|df|link and send it to model how updata it to VM how updata it to view
        //how shoes it when press on "show dictionary"
        return null;
    }

    //public void writeTemporaryPosting (List<MiniDic> dic>)

}
