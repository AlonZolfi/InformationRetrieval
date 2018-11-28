package Model;

public interface IModel {

   void Parse(String pathOfDocs,String pathOfStopWords, String destinationPath, boolean stm);
   void onStartClick(String pathOfDocs,String pathOfStopWords,String destinationPath, boolean stm);
   void onStartOverClick(String path);
}
