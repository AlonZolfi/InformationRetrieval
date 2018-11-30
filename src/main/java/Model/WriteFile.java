package Model;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class WriteFile {

    public static void writeToDest(String path, int i , HashMap<String, StringBuilder> temporaryPosting) throws IOException {
        //get all the info needed and write it to dest
        StringBuilder toWrite = new StringBuilder();
        String [] words = (String[]) temporaryPosting.keySet().toArray();
        for (int j = 0; j <temporaryPosting.size() ; j++) {
            toWrite.append(words[j]+" ");
            toWrite.append(temporaryPosting.get(words[j])+"/n");
        }

        FileWriter fileWriter = new FileWriter(""+path+"_"+i+".txt");
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
