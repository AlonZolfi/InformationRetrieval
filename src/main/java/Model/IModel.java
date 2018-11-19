package Model;

public interface IModel {

   void Parse(String path);
   void onStartClick(String pathOfDocs,String pathOfStopWords,boolean stm);
   void onStartOverClick(String path);
}
