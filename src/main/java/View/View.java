package View;

import Queries.ShowQueryResult;
import Queries.ShowResultRecord;
import ViewModel.ViewModel;
import Index.ShowDictionaryRecord;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;

import java.io.*;
import java.net.URL;
import java.util.*;

public class View implements Observer, IView, Initializable {
    private ViewModel viewModel;
    private File queryFile;

    public Button btn_browse_saveDic;
    public TabPane tabPane_main;
    public TextField tf_queriesFile;
    public TextField tf_simpleQuery;
    public Tab tab_search;
    public CheckComboBox ccb_cities;
    public TextField source;
    public TextField destination;
    public Button btn_start;
    public Button btn_startOver;
    public Button btn_showDic;
    public Button btn_loadDic;
    public CheckBox cb_stm;
    public CheckBox cb_searchStem;
    public Button btn_browse_corpus;

    public TableView<ShowQueryResult> table_showDocs;
    public TableColumn<ShowQueryResult, String> tableCol_docs;

    public TableView<ShowDictionaryRecord> table_showDic;
    public TableColumn<ShowDictionaryRecord,String> tableCol_term;
    public TableColumn<ShowDictionaryRecord,Number> tableCol_count;

    public TableView<ShowResultRecord> table_showResults;
    public TableColumn<ShowResultRecord,String> tableCol_query;

    public Label lbl_resultTitle;
    public Label lbl_totalDocs;
    public Label lbl_totalTerms;
    public Label lbl_totalTime;
    public Label lbl_totalDocsNum;
    public Label lbl_totalTermsNum;
    public Label lbl_totalTimeNum;
    public Label lbl_docSpecialWords;

    public TextField tf_saveResultIn;
    public Button btn_browseSaveAnswers;
    public Button btn_saveAnswers;

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
         return cb_stm.isSelected() || cb_searchStem.isSelected();
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
                        lbl_docSpecialWords.setVisible(false);
                        btn_saveAnswers.setDisable(true);
                    }
                }
            } else if( arg instanceof ObservableList){ // a show dictionary operation was finished and can be shown on display
                List l = (ObservableList)arg;
                if(!l.isEmpty()&& l.get(0) instanceof ShowDictionaryRecord)
                    showDictionary((ObservableList<ShowDictionaryRecord>)arg);
                else
                    showQueryResults((ObservableList<ShowResultRecord>)l);
            } else if( arg instanceof double[]){ // show the results of the indexing
                showIndexResults((double[])arg);
                btn_showDic.setDisable(false);
                tab_search.setDisable(false);
                fillCities();
                lbl_docSpecialWords.setVisible(false);
                btn_saveAnswers.setDisable(true);
            }
        }
    }
    private void showQueryResults(ObservableList<ShowResultRecord> results) {
        if(results != null){
            tableCol_query.setCellValueFactory(cellData -> cellData.getValue().sp_queryIDProperty());
            table_showResults.setItems(results);
            table_showResults.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ShowResultRecord>() {
                @Override
                public void changed(ObservableValue<? extends ShowResultRecord> observable, ShowResultRecord oldValue, ShowResultRecord newValue) {
                    if (observable!=null && table_showResults.getItems().size()>0) {
                        showQueryResult((ObservableValue<ShowResultRecord>) observable);
                        lbl_docSpecialWords.setText("");
                    }
                }
            });
        }
    }
    private void showQueryResult(ObservableValue<ShowResultRecord> observable) {
        if(observable!=null) {
            ObservableList<ShowQueryResult> observableList = FXCollections.observableList(observable.getValue().getDocNames());
            tableCol_docs.setCellValueFactory(cellData -> cellData.getValue().sp_docNameProperty());
            table_showDocs.setItems(observableList);
            table_showDocs.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ShowQueryResult>() {
                @Override
                public void changed(ObservableValue<? extends ShowQueryResult> observable, ShowQueryResult oldValue, ShowQueryResult newValue) {
                    if(observable!=null && newValue!=null)
                        show5words(observable.getValue().getSp_docName());
                }
            });
        }
    }
    /**
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
        lbl_docSpecialWords.setVisible(false);
        btn_saveAnswers.setDisable(true);
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
    private ArrayList<String> listOfCities() {
        ArrayList<String> cities = new ArrayList<>();
        if (!btn_showDic.isDisable()) {
            try {
                FileReader fileReader = new FileReader(destination.getText() + "/CityDictionary.txt");
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    String[] words = line.split("\t");
                    if (!words[4].equals("")) {
                        cities.add(words[0]);
                    }
                }
                bufferedReader.close();
                return cities;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    private void fillCities(){
        ObservableList cities = FXCollections.observableArrayList(listOfCities());
        cities.sort(String.CASE_INSENSITIVE_ORDER);
        ccb_cities.getItems().addAll(cities);
    }
    public void onSearchClick() {
        clearTables();
        if(destination.getText().equals("")) {
            MyAlert.showAlert(Alert.AlertType.ERROR, "You must specify postings path");
            return;
        }
        if(tf_queriesFile.getText().equals("") && tf_simpleQuery.getText().equals("")) {
            MyAlert.showAlert(Alert.AlertType.ERROR, "You must specify a query!");
            return;
        }
        if(!tf_queriesFile.getText().equals("") && !tf_simpleQuery.getText().equals("")) {
            MyAlert.showAlert(Alert.AlertType.ERROR, "Choose what you want to search and delete the other");
            return;
        }
        btn_saveAnswers.setDisable(false);
        List<String> relevantCities = new ArrayList<>();
        ccb_cities.getCheckModel().getCheckedIndices();
        for (Object o: ccb_cities.getCheckModel().getCheckedIndices()){
            Integer integer = (Integer)o;
            relevantCities.add(ccb_cities.getCheckModel().getItem(integer).toString());
        }
        String simpleQuery = tf_simpleQuery.getText();
        if (!simpleQuery.equals(""))
            viewModel.simpleQuery(destination.getText(),source.getText(),simpleQuery,doStemming(),relevantCities);
        else
            viewModel.fileQuery(destination.getText(),source.getText(),queryFile,doStemming(),relevantCities);
        queryFile = null;

    }
    private void clearTables() {
        table_showResults.getItems().clear();
        table_showDocs.getItems().clear();
    }
    public void btn_browseQueries(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Destination Path");
        File defaultDirectory = new File("C:");
        fileChooser.setInitialDirectory(defaultDirectory);
        File chosen = fileChooser.showOpenDialog(new Stage());
        if (chosen!=null) {
            tf_queriesFile.setText(chosen.getAbsolutePath());
            queryFile = chosen;
        }
    }
    private void show5words(String docName){
        lbl_docSpecialWords.setText(viewModel.show5words(docName));
        lbl_docSpecialWords.setVisible(true);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tabPane_main.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    }
    public void btn_browsePathForAnswer() {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Load Destination Path");
        File defaultDirectory = new File("C:");
        dirChooser.setInitialDirectory(defaultDirectory);
        File chosen = dirChooser.showDialog(new Stage());
        if (chosen!=null) {
            tf_saveResultIn.setText(chosen.getAbsolutePath());
        }
    }

    public void saveResults(){
        if(tf_saveResultIn.getText().equals("")) {
            MyAlert.showAlert(Alert.AlertType.ERROR, "Choose where to save the results");
            return;
        }
        boolean isWrite = viewModel.writeRes(tf_saveResultIn.getText());
        if (isWrite)
            MyAlert.showAlert(Alert.AlertType.ERROR, "Your results are saved!");
        else MyAlert.showAlert(Alert.AlertType.ERROR, "Please try again later...");
    }

    public void duplicateStem(ActionEvent actionEvent) {
        if(actionEvent.getSource().equals(cb_stm)) {
            if (cb_stm.isSelected())
                cb_searchStem.setSelected(true);
            else
                cb_searchStem.setSelected(false);
        }
        else if (actionEvent.getSource().equals(cb_searchStem)){
            if(cb_searchStem.isSelected())
                cb_stm.setSelected(true);
            else
                cb_stm.setSelected(false);
        }


    }
}
