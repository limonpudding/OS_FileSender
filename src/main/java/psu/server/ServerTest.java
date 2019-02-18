package psu.server;

import java.io.*;
import java.net.ServerSocket;

import static psu.utils.GlobalConstants.PORT;

public class ServerTest {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new UserConnection(serverSocket.accept()).start();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
