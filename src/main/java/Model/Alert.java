package Model;

public class Alert {
    public static void showAlert(javafx.scene.control.Alert.AlertType at, String text){
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setContentText(text);
        alert.show();
    }
}
