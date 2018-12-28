package IO;

import com.sun.corba.se.impl.orbutil.concurrent.Mutex;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class ReadFile {

    public static Set<String> stopWords;
    public static Mutex m = new Mutex();

    /**
     * initiate the stop words set containing all the stop words
     * @param fileName the path of the file
     */
    public static void initStopWords(String fileName) {
        stopWords = new HashSet<>();
        String line = null;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                stopWords.add(line);
            }
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * this function reads a bunch of files
     * @param pathOfDocs the path of the corpus
     * @param mone iteration number
     * @param mechane number of temp postings
     * @return a list of Corpus Document
     */
    public static LinkedList<CorpusDocument> readFiles(String pathOfDocs, int mone, int mechane) {
        File dir = new File(pathOfDocs);
        File[] directoryListing = dir.listFiles();
        LinkedList<CorpusDocument> allDocsInCorpus = new LinkedList<>();
        if (directoryListing != null && dir.isDirectory()) {
            int start = mone * directoryListing.length / mechane;
            int end = ((mone + 1) * directoryListing.length / mechane) - 1;
            ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
            LinkedList<Future<LinkedList<CorpusDocument>>> futureDocsInFile = new LinkedList<>();
            //go throw end-start FILES
            for (int i = start; i <= end; i++) {
                futureDocsInFile.add(pool.submit(new ReadDocuments(directoryListing[i])));
            }

            //add together all the lists of the corpus docs to one list
            for (Future<LinkedList<CorpusDocument>> f : futureDocsInFile) {
                try {
                    allDocsInCorpus.addAll(f.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
            pool.shutdown();
        }
        return allDocsInCorpus;
    }

    public static LinkedList<String> readPostingLineAtIndex(String path, char c, LinkedList<Integer> indices, boolean stem){
        try {
            m.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String fileName = path+"\\finalPosting";
        if(stem)
            fileName += "Stem";
        fileName+="_"+c+".txt";
        File postingFile = new File(fileName);
        LinkedList<String> postings = new LinkedList<>();
        try {
            List l = FileUtils.readLines(postingFile);
            for (Integer index : indices)
                postings.add(l.get(index).toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        m.release();
        return postings;
    }

    public static List<String> fileToList(String path){
        List<String> l = null;
        try {
            l = FileUtils.readLines(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return l;
    }
}
