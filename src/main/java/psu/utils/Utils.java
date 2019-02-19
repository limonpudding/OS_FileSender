package psu.utils;

import javafx.scene.control.Alert;
import psu.client.ClientMessageWorker;
import psu.entities.Message;
import psu.server.UserConnection;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

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

    public static Message createNewMessage() {
        return new Message();
    }

    public static void putInOutStreamToUser(Message authMessage, String user) {
        UserConnection userConnection = FileSender.findUserConnectionByUserName(user);
        try {
            userConnection.getMessageOutput().writeObject(authMessage);
            userConnection.getMessageOutput().flush();
        } catch (IOException e) {
            throw new RuntimeException("Ошибка запроса отправки файла");
        }
    }
}
