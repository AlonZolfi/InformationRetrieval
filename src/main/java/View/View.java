package View;

import ViewModel.ViewModel;
import Index.ShowDictionaryRecord;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

public class View implements Observer, IView {

    public Tab tab_search;
    public ComboBox cb_citiesList;
    private ViewModel viewModel;
    public MenuButton mb_cities;
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
     * constructor of view, connect the view to the viewModel
     * @param viewModel the view model of the MVVM
     */
    public void setViewModel(ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    /**
     * This function starts the process of parse and index the dictionary
     */
    public void onStartClick() {
        if (source.getText().equals("") || destination.getText().equals(""))// check if the paths are not empty
            MyAlert.showAlert(javafx.scene.control.Alert.AlertType.ERROR,"paths cannot be empty");
        else
            viewModel.onStartClick(source.getText(),destination.getText(), doStemming());//transfer to the view Model
    }

    /**
     * This function deletes all the contents of the destination path
     */
    public void onStartOverClick() {
        if(!destination.getText().equals("")) { // check if the user is sure he wants to delete the whole folder he chose
            ButtonType stay = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
            ButtonType leave = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Are you sure?",leave,stay);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == stay)
                viewModel.onStartOverClick(destination.getText());
        }
        else
            MyAlert.showAlert(javafx.scene.control.Alert.AlertType.ERROR, "destination path is unreachable");


    }

    /**
     * This function determines if we should stem or not
     * @return if we should stem or not
     */
    private boolean doStemming(){
         return cb_stm.isSelected();
    }

    /**
     * a function that gets called when an observer has raised a flag for something that changed
     * @param o - who changed
     * @param arg - the change
     */
    public void update(Observable o, Object arg) {
        if(o==viewModel){
            if(arg instanceof String[]){
                String[] toUpdate = (String[])arg;
                if(toUpdate[0].equals("Fail")) // if we received a fail message from the model
                    MyAlert.showAlert(Alert.AlertType.ERROR,toUpdate[1]);
                else if(toUpdate[0].equals("Successful")) {// if we received a successful message from the model
                    MyAlert.showAlert(Alert.AlertType.INFORMATION, toUpdate[1]);
                    if(toUpdate[1].substring(0,toUpdate[1].indexOf(" ")).equals("Dictionary")) {
                        btn_showDic.setDisable(false);
                        tab_search.setDisable(false);
                        fillCities();
                    }
                }
            } else if( arg instanceof ObservableList){ // a show dictionary operation was finished and can be shown on display
                showDictionary((ObservableList<ShowDictionaryRecord>)arg);
            } else if( arg instanceof double[]){ // show the results of the indexing
                showIndexResults((double[])arg);
                btn_showDic.setDisable(false);
                tab_search.setDisable(false);
                fillCities();
            }
        }
    }

    /***
     * This function lets the user select his corpus and stop words path
     */
    public void browseSourceClick(){
        //open a choose folder dialog
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Load Source Path");
        File defaultDirectory = new File("C:");
        fileChooser.setInitialDirectory(defaultDirectory);
        File chosen = fileChooser.showDialog(new Stage());
        if (chosen!=null)
            source.setText(chosen.getAbsolutePath());
    }

    /***
     * This function lets the user select his  location to save the postings and other data
     */
    public void browseDestClick(){
        //open a choose folder dialog
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Load Destination Path");
        File defaultDirectory = new File("C:");
        fileChooser.setInitialDirectory(defaultDirectory);
        File chosen = fileChooser.showDialog(new Stage());
        if (chosen!=null)
            destination.setText(chosen.getAbsolutePath());
    }

    /**
     * transfers a request to show the dictionary of the current indexing
     */
    public void showDictionaryClick() {
        viewModel.showDictionary();
    }

    /**
     * shows the data of the current indexing process such as: Number of docs, Number of terms, Total time to index
     * @param results the results of the current indexing
     */
    private void showIndexResults(double[] results) {
        //makes all the fields visible to the user and sets the results into them
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

    /**
     * shows an observable list that contains all the data about the current indexing: Term and TF
     * @param records all the data about the current indexing
     */
    private void showDictionary(ObservableList<ShowDictionaryRecord> records){
        if(records != null){
            tableCol_term.setCellValueFactory(cellData -> cellData.getValue().getTermProperty());
            tableCol_count.setCellValueFactory(cellData -> cellData.getValue().getCountProperty());
            table_showDic.setItems(records);
        }
        btn_showDic.setDisable(false);
        tab_search.setDisable(false);
        fillCities();
    }

    /**
     * transfers to the view model a load dictionary request
     */
    public void loadDictionary() {
        if(!destination.getText().equals(""))
            viewModel.loadDictionary(destination.getText(),doStemming());
        else
            MyAlert.showAlert(Alert.AlertType.ERROR,"Destination path cannot be empty");
    }

    /**
     * fill the cities list with the content of the cityDictionary
     */
    private void fillCities() {
        ArrayList<CheckMenuItem> cities = new ArrayList<>();
        cities.add(new CheckMenuItem("All"));
        if (!btn_showDic.isDisable()) {
            try {
                FileReader fileReader = new FileReader(destination.getText() + "/CityDictionary.txt");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    int i = line.indexOf('\t');
                    mb_cities.getItems().add(new CheckMenuItem(line.substring(0, i)));
                }
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
