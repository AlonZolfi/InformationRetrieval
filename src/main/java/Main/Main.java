package Main;

import Index.InvertedIndex;
import Model.*;
import View.View;
import ViewModel.ViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Model model = new Model();
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
        model.loadDictionary("C:\\Users\\alonz\\Desktop\\stam",false);
        model.getResults("C:\\Users\\alonz\\Desktop\\stam","C:\\Users\\alonz\\Desktop\\littlecorpus",new File("C:\\Users\\alonz\\Desktop\\queries.txt"),false);
    }


    public static void main(String[] args) {
        launch(args);
    }
}
