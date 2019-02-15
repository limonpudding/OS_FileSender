package psu.lp.andrey;

import java.io.*;
import java.net.Socket;

public class FileSender {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 4004);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        socket.getInputStream()));
        System.out.println("создали читающий с сервера стрим");
        while (true) {
            System.out.println(reader.readLine());
        }
    }
}