package IO;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.Callable;

public class ReadDocuments implements Callable<LinkedList<CorpusDocument>> {

    private File docToSeparate;

    ReadDocuments(File fileToSeparate){
        this.docToSeparate =fileToSeparate;
    }

    /**
     * goes throw all the files in a certain folder and read documents
     * @return a list of the corpus document exist in a file
     */
    public LinkedList<CorpusDocument> call(){
        File[] directoryListing = docToSeparate.listFiles();
        LinkedList<CorpusDocument> fileList= new LinkedList<>();
        if(directoryListing!= null && docToSeparate.isDirectory()){
            for(File file: directoryListing){
                fileList.addAll(separateDocs(file));
            }
        }
        return fileList;
    }

    /**
     * this function reads all the tags from a certain file and creates a list of corpus documents
     * @param fileToSeparate the file to be handled
     * @return returns a list of corpus document one file contains
     */
    private LinkedList<CorpusDocument> separateDocs(File fileToSeparate) {
        LinkedList<CorpusDocument> docList = new LinkedList<>();
        try {
            FileInputStream fis = new FileInputStream(fileToSeparate);
            Document doc = Jsoup.parse(fis, null, "", Parser.xmlParser());
            Elements elements = doc.select("DOC");
            //get contents of all tags in a specific doc
            for (Element element : elements) {
                String docNum = element.getElementsByTag("DOCNO").text();
                String docDate = element.getElementsByTag("DATE1").text();
                String docText = element.getElementsByTag("TEXT").text();
                String docTitle = element.getElementsByTag("TI").text();
                String docCity= element.getElementsByTag("F").select("F[P=104]").text().toUpperCase();
                CorpusDocument document = new CorpusDocument(fileToSeparate.getName(), docNum, docDate, docTitle, docText, docCity);
                docList.add(document);
            }
            return docList;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return docList;
    }
}
