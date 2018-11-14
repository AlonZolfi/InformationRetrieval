package Model;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class ReadFile {

    public void readFiles(String path){
        File dir = new File(path);
        File[] directoryListing = dir.listFiles();
        if(directoryListing!= null && dir.isDirectory()){
            for(File file: directoryListing){
                iterateOverFolders(file);
            }
        }
        else{
            System.out.println("Not a directory");
        }
    }

    private void iterateOverFolders(File dir){
        File[] directoryListing = dir.listFiles();
        if(directoryListing!= null && dir.isDirectory()){
            for(File file: directoryListing){
                separateFileToDocs(file);
            }
        }
        else{
            System.out.println("Not a directory");
        }
    }

    private void separateFileToDocs(File fileToSeparate) {
        try {
            FileInputStream fis = new FileInputStream(fileToSeparate);
            Document doc = Jsoup.parse(fis, null, "", Parser.xmlParser());
            Elements elements = doc.select("DOC");
            for(Element element: elements){
                String s = element.getElementsByTag("TEXT").toString();
                Queue<String> queue = StringToQueue(StringUtils.split(s," .\n\r\t"));
                Parse p = new Parse(queue);
                new Thread(p).start();
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Queue<String> StringToQueue(String[] splitted) {
        Queue<String> queue = new LinkedList<String>();
        Collections.addAll(queue,splitted);
        return queue;
    }
}
