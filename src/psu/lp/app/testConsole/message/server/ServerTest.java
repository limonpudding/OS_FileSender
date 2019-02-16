package psu.lp.app.testConsole.message.server;

import java.io.*;
import java.net.ServerSocket;

public class ServerTest {

    private static ObjectInputStream messageInput;
    private static ObjectOutputStream messageOutput;

    public static void main(String[] args) {
        try (ServerSocket listener = new ServerSocket(4004)) {
            while (true) {
                new UserConnection(listener.accept()).start();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
