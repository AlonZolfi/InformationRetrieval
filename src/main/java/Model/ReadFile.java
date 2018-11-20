package Model;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;

public class ReadFile {

    public static Set<String> stopWords;


    public static void readFiles(String pathOfDocs,String pathOfStopWords, boolean stm){
        initStopWords(pathOfStopWords);
        File dir = new File(pathOfDocs);
        File[] directoryListing = dir.listFiles();
        if(directoryListing!= null && dir.isDirectory()){
            for(File file: directoryListing){
                iterateOverFolders(file,stm);
            }
        }
        else{
            System.out.println("Not a directory");
        }
    }


    public static void initStopWords(String pathOfStopWords){
        stopWords = new HashSet<String>();
        String fileName = pathOfStopWords;
        String line = null;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while((line = bufferedReader.readLine()) != null) {
                stopWords.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void iterateOverFolders(File dir,boolean stm){
        File[] directoryListing = dir.listFiles();
        if(directoryListing!= null && dir.isDirectory()){
            for(File file: directoryListing){
                separateFileToDocs(file,stm);
            }
        }
        else{
            System.out.println("Not a directory");
        }
    }

    private static void separateFileToDocs(File fileToSeparate,boolean stm) {
        try {
            FileInputStream fis = new FileInputStream(fileToSeparate);
            Document doc = Jsoup.parse(fis, null, "", Parser.xmlParser());
            Elements elements = doc.select("DOC");
            for(Element element: elements){
                String s = element.getElementsByTag("TEXT").toString();
                /*String docNum = element.getElementsByTag("DOCNO").toString();
                String docDate = element.getElementsByTag("DATE1").toString();
                String docText = element.getElementsByTag("TEXT").toString();
                String docTitle = element.getElementsByTag("TI").toString();
                String docCity =  element.getElementsByTag("F P=104").toString();
                //CorpusDocument docu = new Document(docNum,docDate,docTitle,docText,docCity);*/
                Queue<String> tokensQueue = StringToQueue(StringUtils.split(s," .\n\r\t"));
                Parse p = new Parse(tokensQueue,stm);
                new Thread(p).start();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Queue<String> StringToQueue(String[] splitted) {
        Queue<String> queue = new LinkedList<String>();
        Collections.addAll(queue,splitted);
        return queue;
    }
}
