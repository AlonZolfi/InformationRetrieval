package View;

import Model.ReadFile;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.awt.*;
import java.io.File;

public class ViewController implements IView{
    public TextField source;
    public TextField destination;
    public Button btn_start;
    public Button btn_startOver;
    public Button btn_showDic;
    public Button btn_loadDic;

    public void onStartClick(){
        ReadFile rf = new ReadFile();
        rf.readFiles(source.getText());
    }

    public void onStartOverClick() {

    }



}
