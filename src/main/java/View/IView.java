package View;

import ViewModel.ViewModel;
import java.util.Observable;
import java.util.Observer;

public interface IView extends Observer{

    /**
     * constructor of view, connect the view to the viewModel
     * @param viewModel the view model of the MVVM
     */
    void setViewModel(ViewModel viewModel);

    /**
     * This function starts the process of parse and index the dictionary
     */
    void onStartClick();

    /**
     * This function deletes all the contents of the destination path
     */
    void onStartOverClick();

    /**
     * a function that gets called when an observer has raised a flag for something that changed
     * @param o - who changed
     * @param arg - the change
     */
    void update(Observable o, Object arg);

    /***
     * This function lets the user select his corpus and stop words path
     */
    void browseSourceClick();

    /***
     * This function lets the user select his  location to save the postings and other data
     */
    void browseDestClick();

    /**
     * transfers a request to show the dictionary of the current indexing
     */
    void showDictionaryClick();

    /**
     * transfers to the view model a load dictionary request
     */
    void loadDictionary();
}
