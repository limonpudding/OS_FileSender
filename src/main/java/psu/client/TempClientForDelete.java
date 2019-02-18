package psu.client;

import psu.entities.Message;
import psu.entities.MessageType;

import java.io.*;
import java.net.Socket;

public class TempClientForDelete {
    private static InputStream inputStream;
    private static OutputStream outputStream;

    private static ObjectInputStream messageInput;
    private static ObjectOutputStream messageOutput;

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 4004);
        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        messageInput = new ObjectInputStream(inputStream);
        messageOutput = new ObjectOutputStream(outputStream);
        Message message = new Message();
        message.setMessageType(MessageType.AUTH);
        message.setContent("Аркадий");
        messageOutput.writeObject(message);
        messageOutput.flush();
    }
}
