package Main;

import IO.WriteFile;
import Index.InvertedIndex;
import Model.*;
import View.View;
import ViewModel.ViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.util.LinkedList;

public class Main extends Application {
    private void mergePostings(InvertedIndex invertedIndex, String tempPostingPath, boolean stem){
        int numOfTerms = 0;
        LinkedList<BufferedReader> bufferedReaderList = initiateBufferedReaderList(tempPostingPath);
        String[] firstSentenceOfFile = initiateMergingArray(bufferedReaderList);
        char postingNum = '`';
        LinkedList<StringBuilder> writeToPosting = new LinkedList<>();
        String fileName = "Stem"+tempPostingPath+"/finalPosting";
        if (!stem)
            fileName= tempPostingPath+"/finalPosting";
        do {
            int numOfAppearances = 0;
            StringBuilder finalPostingLine = new StringBuilder();
            String minTerm = ""+(char)127;
            String[] saveSentences = new String[firstSentenceOfFile.length];
            boolean AID=false;
            for (int i = 0; i < firstSentenceOfFile.length; i++) {
                if(firstSentenceOfFile[i]!=null && !firstSentenceOfFile[i].equals("")) {
                    String[] termAndData = firstSentenceOfFile[i].split("~");
                    int result = termAndData[0].compareToIgnoreCase(minTerm);
                    if(termAndData[0].equals("AID"))
                        AID=true;
                    if (result == 0) {
                        finalPostingLine.append(termAndData[2]);
                        firstSentenceOfFile[i] = null;
                        saveSentences[i] = termAndData[0]+"~"+termAndData[1]+"~"+termAndData[2];
                        numOfAppearances += Integer.parseInt(termAndData[1]);
                    } else if (result < 0) {
                        minTerm = termAndData[0];
                        finalPostingLine.delete(0, finalPostingLine.length());
                        finalPostingLine.append(termAndData[0]).append("~").append(termAndData[2]);
                        firstSentenceOfFile[i] = null;
                        saveSentences[i] = termAndData[0]+"~"+termAndData[1]+"~"+termAndData[2];
                        numOfAppearances = Integer.parseInt(termAndData[1]);
                    }
                }
                else System.out.println("dieeee" + firstSentenceOfFile[i]);
            }
            for (int i = 0; i < saveSentences.length; i++) {
                if (saveSentences[i] != null) {
                    String[] termAndData = saveSentences[i].split("~");
                    if (!termAndData[0].equals(minTerm)) {
                        firstSentenceOfFile[i] = termAndData[0]+"~"+termAndData[1]+"~"+termAndData[2];
                    }
                    else
                        firstSentenceOfFile[i] = getNextSentence(bufferedReaderList.get(i));
                }
            }
            if(!finalPostingLine.toString().equals("")) {
                numOfTerms++;
                invertedIndex.setPointer(minTerm, fileName, writeToPosting.size());
                invertedIndex.setNumOfAppearance(minTerm,numOfAppearances);
            }
            else System.out.println("rtrtrtrtr   "+finalPostingLine);
            if(minTerm.toLowerCase().charAt(0)>postingNum) {
                WriteFile.writeToEndOfFile(fileName + "_"+ postingNum + ".txt", writeToPosting);
                postingNum++;
                writeToPosting = new LinkedList<>();
            }
            else System.out.println("iiiiiiiiii   "+minTerm);
            writeToPosting.add(finalPostingLine.append("\t").append(numOfAppearances));
        } while(containsNull(firstSentenceOfFile) && postingNum<123);
        WriteFile.writeToEndOfFile(fileName + "_z" + ".txt", writeToPosting);
        System.out.println(numOfTerms);
        System.out.println(invertedIndex.getNumOfUniqueTerms());
    }

    private boolean containsNull(String[] firstSentenceOfFile) {
        for (String sentence: firstSentenceOfFile) {
            if(sentence!=null)
                return true;
        }
        return false;
    }

    private String getNextSentence(BufferedReader bf){
        String line = null;
        try {
            if((line= bf.readLine())!=null)
                return line;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    private String[] initiateMergingArray(LinkedList<BufferedReader> bufferedReaderList){
        String[] firstSentenceOfFile = new String[bufferedReaderList.size()];
        int i = 0;
        for (BufferedReader bf: bufferedReaderList) {
            String line = getNextSentence(bf);
            if(line!= null) {
                firstSentenceOfFile[i]= line;
            }
            i++;
        }
        return firstSentenceOfFile;
    }

    private LinkedList<BufferedReader> initiateBufferedReaderList(String tempPostingPath){
        File dirSource = new File(tempPostingPath);
        File[] directoryListing = dirSource.listFiles();
        LinkedList<BufferedReader> bufferedReaderList = new LinkedList<>();
        if (directoryListing != null && dirSource.isDirectory()) {
            for (File file : directoryListing) {
                if (file.getName().startsWith("posting")) {
                    try {
                        bufferedReaderList.add(new BufferedReader(new FileReader(file)));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return bufferedReaderList;
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        mergePostings(new InvertedIndex(new File("C://Users//alonz//Desktop//stam//InvertedFile.txt")),"C://Users//alonz//Desktop//stam",false);
        /*Model model = new Model();
        ViewModel viewModel = new ViewModel(model);
        model.addObserver(viewModel);
        //--------------
        primaryStage.setTitle("Information Retrieval Project");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getClassLoader().getResource("View.fxml").openStream());
        primaryStage.setScene(new Scene(root));
        //--------------
        View view = fxmlLoader.getController();
        view.setViewModel(viewModel);
        viewModel.addObserver(view);
        //--------------
        primaryStage.show();
*/
        /*String s = "Alon";
        int i = s.compareToIgnoreCase("ALON");*/

        /*CorpusDocument cd = new CorpusDocument("","","","",
                "First " +
                "first " +
                "1001 " +
                "Grams " +
                "54.546456 " +
                "Tons " +
                "1000 " +
                "Seconds " +
                "3.2 " +
                "Minutes " +
                "4/9-11 " +
                "1/5 " +
                "4/9-11 " +
                "1/5 " +
                "187 " +
                "4/9-1/8 " +
                "999 " +
                "between " +
                "18 " +
                "and " +
                "24 " +
                "between " +
                "3/5 " +
                "and " +
                "78/96 " +
                "between " +
                "10 " +
                "3/5 " +
                "and " +
                "78/96 " +
                "between " +
                "1/8 " +
                "and " +
                "3 " +
                "1/83 " +
                "between " +
                "7879 " +
                "1/8 " +
                "and " +
                "3 " +
                "1/83 " +
                "24 " +
                "Value-added " +
                "step-by-step " +
                "10-part " +
                "8798789-848949 " +
                "-589 " +
                "1,010,560 " +
                "10,123 " +
                "123 " +
                "Thousand " +
                "1010.56 " +
                "10,123,000 " +
                "55 " +
                "Million " +
                "10,123,000,000 " +
                "55 " +
                "Billion " +
                "7 " +
                "Trillion " +
                "6% " +
                "6 "+
                "percent "+
                "6000 " +
                "percentage " +
                "1102.7320 " +
                "Dollars " +
                "$450,000 " +
                "$450000 " +
                "1,000,000 " +
                "Dollars " +
                "$450,000,000 " +
                "$100 " +
                "million " +
                "20.6m " +
                "Dollars " +
                "$100 " +
                "billion " +
                "100bn " +
                "Dollars " +
                "22 " +
                "3/7 " +
                "Dollars " +
                "654 " +
                "1451919/116161 " +
                "100 " +
                "billion "+
                "U.S. "+
                "dollars "+
                "320.5 " +
                "million " +
                "U.S. "+
                "dollars "+
                "1.2 "+
                "trillion "+
                "U.S. "+
                "dollars "+
                "14 "+
                "DEC "+
                "14 "+
                "Feb "+
                "Nov "+
                "1994 "+
                "10-11",
            "");
        Parse p = new Parse(cd,false);

        p.call();*/
    }


    public static void main(String[] args) {
        launch(args);
    }
}
