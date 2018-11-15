package ViewModel;

import Model.IModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.shape.Path;

import java.util.Observable;
import java.util.Observer;

public class ViewModel extends Observable implements Observer {

    private IModel model;

    public ViewModel(IModel model) {
        if (model != null)
            this.model = model;
    }

    public void update(Observable o, Object arg) {

    }


    public void onStartClick(String path){
        model.onStartClick(path);
    }

    public void onStartOverClick(String path) {
        model.onStartOverClick(path);
    }

}
