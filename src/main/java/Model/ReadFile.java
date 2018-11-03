package Model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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
            System.out.println("write docs to files somewhere");
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
