package View;

import ViewModel.ViewModel;
import Model.ShowDictionaryRecord;

import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;


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
    public TableColumn<ShowDictionaryRecord,String> tableCol_term;
    public TableColumn<ShowDictionaryRecord,String> tableCol_count;
    public TableView<ShowDictionaryRecord> table_showDic;
    public Label lbl_resultTitle;
    public Label lbl_totalDocs;
    public Label lbl_totalTerms;
    public Label lbl_totalTime;
    public Label lbl_totalDocsNum;
    public Label lbl_totalTermsNum;
    public Label lbl_totalTimeNum;

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
            MyAlert.showAlert(javafx.scene.control.Alert.AlertType.ERROR,"paths cannot be empty");
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
                MyAlert.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "path of corpus and stopReadAndParse words is unreachable");
                return;
            }
            File dirDest = new File(destination.getText());
            if(!dirDest.isDirectory()){
                MyAlert.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "destination path is unreachable");
                return;
            }


            viewModel.onStartClick(pathOfDocs, pathOfStopWords,dirDest.getAbsolutePath(), doStemming());
        }
    }

    /**
     * This function delete all of the work and let the option of clear start
     */
    public void onStartOverClick() {
        if(!destination.getText().equals("")) {
            ButtonType stay = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType leave = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION,"Are you sure?",leave,stay);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == stay)
                viewModel.onStartOverClick(destination.getText());
        }
        else
            MyAlert.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "destination path is unreachable");


    }

    /**
     * This function determen if we shuld stem or not
     * @return if we should stem or not
     */
    private boolean doStemming(){
         return cb_stm.isSelected();
    }

    public void update(Observable o, Object arg) {
        if(o==viewModel){
            if(arg instanceof String[]){
                String[] toUpdate = (String[])arg;
                if(toUpdate[0].equals("RaiseAlert"))
                    MyAlert.showAlert(javafx.scene.control.Alert.AlertType.ERROR,toUpdate[1]);
            } else if( arg instanceof ObservableList){
                showDictionary((ObservableList)arg);
            } else if( arg instanceof double[]){
                showResults((double[])arg);
            }
        }
    }

    private void showResults(double[] results) {
        lbl_totalDocsNum.setText(""+(int)results[0]);
        lbl_totalTermsNum.setText(""+(int)results[1]);
        lbl_totalTimeNum.setText(""+results[2]+" Minutes");
        lbl_resultTitle.setVisible(true);
        lbl_totalDocs.setVisible(true);
        lbl_totalDocsNum.setVisible(true);
        lbl_totalTerms.setVisible(true);
        lbl_totalTermsNum.setVisible(true);
        lbl_totalTime.setVisible(true);
        lbl_totalTimeNum.setVisible(true);




    }


    /***
     * This function let the user select his corpus and stopReadAndParse word list
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

    public void showDictionaryClick() {
        viewModel.showDictionary();
    }

    private void showDictionary(ObservableList<ShowDictionaryRecord> records){
        if(records != null){
            tableCol_term.setCellValueFactory(cellData -> cellData.getValue().getTermProperty());
            tableCol_count.setCellValueFactory(cellData -> cellData.getValue().getCountProperty());
            table_showDic.setItems(records);
        }
    }
}
