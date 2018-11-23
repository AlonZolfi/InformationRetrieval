package View;

import Model.*;
import ViewModel.*;
import View.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        /*Model model = new Model();
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
        primaryStage.show();*/

        CorpusDocument cd = new CorpusDocument("","","","", "999 " +
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
            "1994 ",
            "");
        Parse p = new Parse(cd,false);

        /*s.add("999");
        s.add("1,010,560");
        s.add("10,123");
        s.add("123");
        s.add("Thousand");
        s.add("1010.56");
        s.add("10,123,000");
        s.add("55");
        s.add("Million");
        s.add("10,123,000,000");
        s.add("55");
        s.add("Billion");
        s.add("7");
        s.add("Trillion");
        s.add("6%");
        s.add("6");
        s.add("percent");
        s.add("6000");
        s.add("percentage");
        s.add("1102.7320");
        s.add("Dollars");
        s.add("$450,000");
        s.add("1,000,000");
        s.add("Dollars");
        s.add("$450,000,000");
        s.add("$100");
        s.add("million");
        s.add("20.6m");
        s.add("Dollars");
        s.add("$100");
        s.add("billion");
        s.add("100bn");
        s.add("Dollars");
        s.add("22");
        s.add("3/7");
        s.add("Dollars");
        s.add("654");
        s.add("1451919/116161");
        s.add("100");
        s.add("billion");
        s.add("U.S.");
        s.add("dollars");
        s.add("320.5");
        s.add("million");
        s.add("U.S.");
        s.add("dollars");
        s.add("1.2");
        s.add("trillion");
        s.add("U.S.");
        s.add("dollars");
        s.add("14");
        s.add("DEC");
        s.add("14");
        s.add("Feb");
        s.add("Nov");
        s.add("1994");
*/
        p.run();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
