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

        Queue<String> s= new LinkedList<String>();
        Parse p = new Parse(s);
        s.add("999");
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
        s.add("JAN");
        s.add("14");
        s.add("Feb");
        p.run();

    }


    public static void main(String[] args) {
        launch(args);
    }
}
