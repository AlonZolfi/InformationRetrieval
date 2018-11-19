package View;

import ViewModel.ViewModel;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
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

    public void setViewModel(ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void onStartClick(){
        viewModel.onStartClick("","", doStemming());
    }

    public void onStartOverClick() {
        viewModel.onStartOverClick(destination.getText());
    }

    public boolean doStemming(){
         return cb_stm.isSelected();
    }

    public void update(Observable o, Object arg) {

    }

    public void browseSource(){
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Load Path");
        File defaultDirectory = new File("src/main/resources");
        fileChooser.setInitialDirectory(defaultDirectory);
        File chosen = fileChooser.showDialog(new Stage());
        if (chosen!=null)
            source.setText(chosen.getName());
        else source.setText(defaultDirectory.getName());
    }

    public void browseDest(){
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Load Path");
        File defaultDirectory = new File("src/main/resources");
        fileChooser.setInitialDirectory(defaultDirectory);
        File chosen = fileChooser.showDialog(new Stage());
        if (chosen!=null)
            destination.setText(chosen.getName());
        else destination.setText(defaultDirectory.getName());
    }
}
