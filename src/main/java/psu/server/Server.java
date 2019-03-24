package psu.server;

import psu.entities.ConnectionResult;

import java.io.*;
import java.net.ServerSocket;

import static psu.utils.GlobalConstants.PORT;

public class Server {

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            IPSender ipSender = new IPSender();
            ipSender.start();
            while (true) {
                UserConnection userConnection = new UserConnection(serverSocket.accept());
                if (userConnection.getConnectionResult() == ConnectionResult.SUCCESS){
                    new Thread(userConnection).start();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            //TODO нормально обработать
        }
    }
}
