package View;

import Model.*;
import ViewModel.ViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        //the semtemce ia "hila kesem hadad the malka"
        /*String line = "hila kesem hadad ha malka ve ha kesem";
        MiniDictionary miniDictionary = new MiniDictionary("hila");
        String[] lineMem = line.split(" ");
        for (int i = 0; i < lineMem.length; i++) {
            miniDictionary.addWord(lineMem[i], i);
        }
        miniDictionary.listOfData();


        String lineTwo = "alon zolfi zylfi ha shamen ve ha mechoar";
        MiniDictionary miniDictionaryTwo = new MiniDictionary("alon");
        String[] lineMemTwo = lineTwo.split(" ");
        for (int i = 0; i < lineMemTwo.length; i++) {
            miniDictionaryTwo.addWord(lineMemTwo[i], i);
        }

        miniDictionaryTwo.listOfData();*/

        Model model = new Model();
        ViewModel viewModel = new ViewModel(model);
        model.addObserver(viewModel);
        //--------------
        primaryStage.setTitle("Information Retrieval Project");
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getClassLoader().getResource("View.fxml").openStream());
        //scene.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());
        primaryStage.setScene(new Scene(root));
        //--------------
        View view = fxmlLoader.getController();
        view.setViewModel(viewModel);
        viewModel.addObserver(view);
        //--------------
        primaryStage.show();


        /*CorpusDocument cd = new CorpusDocument("","","","",
                "$ " +
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
        Parse p = new Parse(cd,true);

        p.call();*/
    }


    public static void main(String[] args) {
        launch(args);
    }
}
