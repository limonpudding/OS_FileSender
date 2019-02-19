package psu.utils;

import javafx.scene.control.Alert;

import java.io.File;

public class Utils {
    public static void showAlertMessage(String title, String header, String text, Alert.AlertType type) {
        Alert succesConnect = new Alert(type);
        succesConnect.setTitle(title);
        succesConnect.setHeaderText(header);
        succesConnect.setContentText(text);
        succesConnect.showAndWait();
    }

    public static String getFileSize(File file) {
        return String.valueOf(file.length());
    }
}
