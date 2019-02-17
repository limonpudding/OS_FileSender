package psu.lp.app.testConsole.message.server;

import java.io.*;
import java.net.ServerSocket;

public class ServerTest {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(25565)) {
            while (true) {
                new UserConnection(serverSocket.accept()).start();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
