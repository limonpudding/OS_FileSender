package psu.utils;

import psu.client.ClientMessageWorker;
import psu.server.UserConnection;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

import static psu.utils.GlobalConstants.BUF_SIZE;
import static psu.utils.GlobalConstants.EOF_GUID;

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
            outputStream.write(EOF_GUID);
            inputStream.close();
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
            while ((length = inputStream.read(buffer)) != -1 && !Arrays.equals(Arrays.copyOf(buffer, length), EOF_GUID)) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.write(EOF_GUID);
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
            while ((length = inputStream.read(buffer)) != -1 && !Arrays.equals(Arrays.copyOf(buffer, length), EOF_GUID)) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
