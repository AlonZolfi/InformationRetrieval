package View;

public class MyAlert {
    public static void showAlert(javafx.scene.control.Alert.AlertType at, String text){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(at);
        alert.setContentText(text);
        alert.show();
    }
}
