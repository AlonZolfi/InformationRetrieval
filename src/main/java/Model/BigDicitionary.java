package Model;

import java.util.LinkedList;

public class BigDicitionary {

    //contains two docs one for this chank posting and one for this (chank words | df | link to posting)

    public void initDic(){
        //neeed to be a singlton(?!) I whant only one dictionary at all time
    }

    public void addNewText(MiniDictionary mini){
        //get mini and fix is data in the posting and in the dic (if will use in
        // thereds we need to make sure the thred finish whrite to both)
    }

    public void addNewWord(){
        //checks if word is in the dic - if it is the counter ++ and we add her new posting to the old
        //if it dosnt we create new line with counter = 1 and new link to a new posting
    }


}
