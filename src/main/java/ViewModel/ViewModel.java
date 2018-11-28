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
        if(o==model){
            setChanged();
            notifyObservers(arg);
        }
    }


    public void onStartClick(String pathOfDocs, String pathOfStopWords, String destinationPath, boolean stm){
        model.onStartClick(pathOfDocs,pathOfStopWords,destinationPath,stm);
    }

    public void onStartOverClick(String path) {
        model.onStartOverClick(path);
    }

}
