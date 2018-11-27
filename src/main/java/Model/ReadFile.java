package Model;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ReadFile {

    public static Set<String> stopWords;

    public static void initStopWords(String pathOfStopWords){
        stopWords = new HashSet<>();
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

    public static LinkedList<CorpusDocument> readFiles(String pathOfDocs, String pathOfStopWords,int mone,int mechane) {
        File dir = new File(pathOfDocs);
        File[] directoryListing = dir.listFiles();
        LinkedList<CorpusDocument> allDocsInCorpus = new LinkedList<>();
        int start = mone*directoryListing.length/mechane;
        int end = ((mone+1)*directoryListing.length/mechane)-1;
        if (directoryListing != null && dir.isDirectory()) {
            ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
            LinkedList<Future> futureDocsInFile = new LinkedList<>();
            /*for (File file : directoryListing) {
                Future<LinkedList<CorpusDocument>> f = pool.submit(new ReadDocuments(file));
                futureDocsInFile.add(f);
            }*/
            for (int i = start; i <= end; i++) {
                Future<LinkedList<CorpusDocument>> f = pool.submit(new ReadDocuments(directoryListing[i]));
                futureDocsInFile.add(f);
            }

            for (Future f : futureDocsInFile) {
                try {
                    allDocsInCorpus.addAll((LinkedList<CorpusDocument>) (f.get()));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            pool.shutdown();
        } else {
            System.out.println("Not a directory");
        }

        return allDocsInCorpus;
    }


    /*private static LinkedList<LinkedList<CorpusDocument>> iterateOverFolders(File dir,boolean stm){
        File[] directoryListing = dir.listFiles();
        if(directoryListing!= null && dir.isDirectory()){
            LinkedList<LinkedList<CorpusDocument>> fileList= new LinkedList<>();
            for(File file: directoryListing){
                fileList.add(separateFileToDocs(file,stm));
            }
            return fileList;
        }
        else{
            System.out.println("Not a directory");
        }
    }

    private static LinkedList<CorpusDocument> separateFileToDocs(File docToSeparate){//, boolean stm) {
        LinkedList<CorpusDocument> docList = new LinkedList<>();
        try {
            FileInputStream fis = new FileInputStream(docToSeparate);
            Document doc = Jsoup.parse(fis, null, "", Parser.xmlParser());
            Elements elements = doc.select("DOC");
            for(Element element: elements){
                String docNum = element.getElementsByTag("DOCNO").toString();
                String docDate = element.getElementsByTag("DATE1").toString();
                String docText = element.getElementsByTag("TEXT").toString();
                String docTitle = element.getElementsByTag("TI").toString();
                String docCity =  element.getElementsByTag("F P=104").toString();
                CorpusDocument document = new CorpusDocument(docToSeparate.getName(),docNum,docDate,docTitle,docText,docCity);
                docList.add(document);
                //Queue<String> tokensQueue = StringToQueue(StringUtils.split(document.getM_docText()," .\n\r\t"));
                //Parse p = new Parse(tokensQueue,stm);
                /*Parse p = new Parse(document,stm);
                new Thread(p).start();
            }
            return docList;
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return docList;
    }

    private static Queue<String> StringToQueue(String[] split) {
        Queue<String> queue = new LinkedList<String>();
        Collections.addAll(queue,split);
        return queue;
    }*/
}
