package psu.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import psu.entities.Message;
import psu.entities.MessageType;
import psu.server.UserConnection;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Utils {
    public static void showAlertMessage(String title, String header, String text, Alert.AlertType type) {
        Platform.runLater(() -> {
            Alert successConnect = new Alert(type);
            successConnect.setTitle(title);
            successConnect.setHeaderText(header);
            successConnect.setContentText(text);
            successConnect.showAndWait();
        });
    }

    public static String getFileSize(File file) {
        return String.valueOf(file.length());
    }

    public static Message createNewMessage(MessageType messageType) {
        return new Message(messageType);
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

    public static String getThirdIpPart() {
        try {
            DatagramSocket socket = new DatagramSocket(GlobalConstants.PORT - 1);
            socket.connect(InetAddress.getByName("8.8.8.8"), GlobalConstants.PORT); // Немного костыльный способ получения адреса в локальной сети
            String part = String.valueOf(socket.getLocalAddress().getAddress()[2]);
            socket.disconnect();
            return part;
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }
}
