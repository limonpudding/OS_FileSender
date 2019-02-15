package psu.lp.andrey;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Server {

    private static List<NamedSocked> users = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        ServerSocket serverSocket;
        serverSocket = new ServerSocket(4004);
        Thread connectionListener = new Thread(() -> {
            try {
                while (true) {
                    NamedSocked user = new NamedSocked(
                            serverSocket.accept(),
                            UUID.randomUUID().toString());
                    users.add(user);
                    System.out.println("+ клиент");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        connectionListener.setDaemon(true);
        connectionListener.start();
        while (true) {
            List<String> uuids = getUUIDs();
            for (NamedSocked user : users) {
                System.out.println("попытка разослать юзеру " + user.getName() + " список всех юзеров");
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                        user.getSocket().getOutputStream()));
                writer.write(uuids.toString() + "\n");
                writer.flush();
            }

            Thread.sleep(500);
        }
    }

    private static List<String> getUUIDs() {
        ArrayList<String> userUUIDS = new ArrayList<>();
        for (NamedSocked user : users) {
            userUUIDS.add(user.getName());
        }
        return userUUIDS;
    }

}
