package psu.utils;

import javafx.scene.control.Alert;
import psu.client.ClientMessageWorker;
import psu.server.UserConnection;

import java.io.*;
import java.net.Socket;
import java.text.MessageFormat;

import static psu.utils.GlobalConstants.BUF_SIZE;
import static psu.utils.Utils.showAlertMessage;

public class FileSender {

    private static byte[] buffer = new byte[BUF_SIZE];

    public static void sendFile(File file) {
        Socket socket = ClientMessageWorker.getClientSocket();

        try {
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = new FileInputStream(file);

            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            showAlertMessage("Отправка файла", "Статус", GlobalConstants.SEND_FILE_SUCCESS, Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void redirectFile(String from, String to) {
        Socket fromSocket = findSocketByUserName(from);
        Socket toSocket = findSocketByUserName(to);
        try {
            InputStream inputStream = fromSocket.getInputStream();
            OutputStream outputStream = toSocket.getOutputStream();

            int length;
            while (isAvailable(inputStream) && (length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void acceptFile(File file) {
        Socket socket = ClientMessageWorker.getClientSocket();
        try {
            InputStream inputStream = socket.getInputStream();
            OutputStream outputStream = new FileOutputStream(file);

            int length;
            while (isAvailable(inputStream) && (length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            showAlertMessage("Отправка файла", "Статус", MessageFormat.format(GlobalConstants.ACCEPT_FILE_SUCCESS, file.getName(), file.getAbsolutePath()), Alert.AlertType.INFORMATION);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean isAvailable(InputStream inputStream) throws IOException {
        if (inputStream.available()!=0){
            return true;
        }
        try {//даём время подойти отстающим в поток считывания
            for (int i = 0; i< GlobalConstants.TIMEOUT; ++i) {
                Thread.sleep(1);
                if (inputStream.available()!=0){
                    return true;
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("При проверке на авайлабл поток неожиданно проснулся");
        }
        return inputStream.available() != 0;
    }

    public static Socket findSocketByUserName(String name) {
        for (UserConnection userConnection : UserConnection.getUserConnections()) {
            if (userConnection.getUserName().equals(name)) {
                return userConnection.getUserSocket();
            }
        }
        throw new RuntimeException("Не найден пользователь с таким именем: " + name);
    }

    public static UserConnection findUserConnectionByUserName(String name) {
        for (UserConnection userConnection : UserConnection.getUserConnections()) {
            if (userConnection.getUserName().equals(name)) {
                return userConnection;
            }
        }
        throw new RuntimeException("Не найден пользователь с таким именем: " + name);
    }
}
