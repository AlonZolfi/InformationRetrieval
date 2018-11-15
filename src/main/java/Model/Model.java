package Model;

public class Model implements IModel{

    public void Parse(String path) {
        ReadFile.readFiles(path);
    }

}
