package View;

import ViewModel.ViewModel;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Observable;
import java.util.Observer;


public class View implements Observer, IView {

    private ViewModel viewModel;
    public TextField source;
    public TextField destination;
    public Button btn_start;
    public Button btn_startOver;
    public Button btn_showDic;
    public Button btn_loadDic;
    public CheckBox cb_stm;
    public Button btn_browse_corpus;
    public Button btn_browse_saveDic;

    /**
     * constractor of view, connect the view to the viewModel
     * @param viewModel
     */
    public void setViewModel(ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * This function start the procces of pars and index the dictionary
     */
    public void onStartClick() {
        if (source.getText().equals("") || destination.getText().equals("")) {
            Alert.showAlert(javafx.scene.control.Alert.AlertType.ERROR,"paths cannot be empty");
        } else {
            String pathOfDocs = "" , pathOfStopWords = "";
            File dirSource = new File(source.getText());
            File[] directoryListing = dirSource.listFiles();
            if (directoryListing != null && dirSource.isDirectory()) {
                for (File file : directoryListing) {
                    if (file.isDirectory())
                        pathOfDocs = file.getAbsolutePath();
                    else
                        pathOfStopWords = file.getAbsolutePath();
                }
            }
            else {
                Alert.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "path of corpus and stop words is unreachable");
                return;
            }
            File dirDest = new File(destination.getText());
            if(!dirDest.isDirectory()){
                Alert.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "destination path is unreachable");
                return;
            }


            viewModel.onStartClick(pathOfDocs, pathOfStopWords,dirDest.getAbsolutePath(), doStemming());
        }
    }

    /**
     * This function delete all of the work and let the option of clear start
     */
    public void onStartOverClick() {
        if(!destination.getText().equals(""))
            viewModel.onStartOverClick(destination.getText());
        else
            Alert.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "destination path is unreachable");


    }

    /**
     * This function determen if we shuld stem or not
     * @return if we should stem or not
     */
    public boolean doStemming(){
         return cb_stm.isSelected();
    }

    public void update(Observable o, Object arg) {
        if(o==viewModel){
            if(arg instanceof String[]){
                String[] toUpdate = (String[])arg;
                if(toUpdate[0].equals("Raise Alert"))
                    Alert.showAlert(javafx.scene.control.Alert.AlertType.ERROR,toUpdate[1]);
            }
        }
    }


    /***
     * This function let the user select his corpus and stop word list
     */
    public void browseSource(){
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Load Source Path");
        File defaultDirectory = new File("C:\\Users\\alonz\\Desktop");
        fileChooser.setInitialDirectory(defaultDirectory);
        File chosen = fileChooser.showDialog(new Stage());
        if (chosen!=null)
            source.setText(chosen.getAbsolutePath());
        /*else
            source.setText(defaultDirectory.getName());*/
    }


    /***
     * This function let the user select his favorite location to save the documents
     */

    public void browseDest(){
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Load Destination Path");
        File defaultDirectory = new File("C:\\Users\\alonz\\Desktop");
        fileChooser.setInitialDirectory(defaultDirectory);
        File chosen = fileChooser.showDialog(new Stage());
        if (chosen!=null)
            destination.setText(chosen.getAbsolutePath());
        /*else
            destination.setText(defaultDirectory.getName());*/
    }
}
