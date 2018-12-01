package ViewModel;

import Model.IModel;
import javafx.application.Platform;

import java.util.Observable;
import java.util.Observer;

public class ViewModel extends Observable implements Observer {

    private IModel model;

    public ViewModel(IModel model) {
        this.model = model;
    }

    public void update(Observable o, Object arg) {
        if(o==model){
            setChanged();
            notifyObservers(arg);
        }
    }

    public void onStartClick(String pathOfDocs, String destinationPath, boolean stm){
        Platform.runLater(()->model.onStartClick(pathOfDocs,destinationPath,stm));
    }

    public void onStartOverClick(String path) {
        Platform.runLater(()->model.onStartOverClick(path));
    }

    public void showDictionary(){
        Platform.runLater(()->model.showDictionary());
    }
}
