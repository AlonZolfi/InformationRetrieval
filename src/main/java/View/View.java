package View;

import ViewModel.ViewModel;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

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

    public void setViewModel(ViewModel viewModel) {
        this.viewModel = viewModel;
    }

    public void onStartClick(){
        viewModel.onStartClick(source.getText());
    }

    public void onStartOverClick() {
        viewModel.onStartOverClick(destination.getText());
    }

    public void update(Observable o, Object arg) {

    }
}
