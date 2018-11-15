package View;

import Model.*;
import Model.ReadFile;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("View.fxml"));
        primaryStage.setTitle("Information Retrieval Project");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        /*Parse p = new Parse();
        Queue<String> s= new LinkedList<String>();
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
        s.add("6");
        s.add("percentage");
        p.Parse(s);*/

    }


    public static void main(String[] args) {
        launch(args);
    }
}
