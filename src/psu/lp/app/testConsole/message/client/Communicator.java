package psu.lp.app.testConsole.message.client;

import java.net.Socket;

public class Communicator implements Runnable {

    private Socket socket;
    private String host;
    private int port;

    Communicator(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            socket = new Socket(host, port);
        } catch (Exception ex) {

        }
    }
}
