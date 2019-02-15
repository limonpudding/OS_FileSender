package psu.lp.andrey;

import java.net.Socket;

public class NamedSocked {
    private Socket socket;
    private String name;

    public NamedSocked(Socket socket, String name) {
        this.socket = socket;
        this.name = name;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
