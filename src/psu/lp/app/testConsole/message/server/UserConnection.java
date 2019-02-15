package psu.lp.app.testConsole.message.server;

import psu.lp.app.testConsole.message.Message;
import psu.lp.app.testConsole.message.MessageType;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class UserConnection extends Thread {
    private final static String SERVER_NAME = "host";

    private String userName;
    private Socket userSocket;

    private InputStream inputStream;
    private OutputStream outputStream;

    private ObjectInputStream messageInput;
    private ObjectOutputStream messageOutput;

    public UserConnection(Socket socket) {
        this.userSocket = socket;
    }

    @Override
    public void run() {
        try {
            outputStream = userSocket.getOutputStream();
            messageOutput = new ObjectOutputStream(outputStream);
            inputStream = userSocket.getInputStream();
            messageInput = new ObjectInputStream(inputStream);

            Message tryAuth = (Message) messageInput.readObject();
            //проверка уникальности имени
            //первое сообщение всегда должно быть типа AUTH --> в content лежит имя клиента
            userName = tryAuth.getContent();

            System.out.println(tryAuth.getMessageType().getInfo() + " пользователя с ником " + tryAuth.getContent());
            while (userSocket.isConnected()) {
                Message test = (Message) messageInput.readObject();
                switch (test.getMessageType()) {
                    case AUTH:
                        // не может быть вызван дважды
                        break;
                    case NEW_FILE_REQUEST:
                        // попытка отправки файла
                    default:
                        System.out.println("Неверный тип сообщения");
                        Message errorMessage = new Message();
                        errorMessage.setMessageType(MessageType.ERROR_SERVER);
                        errorMessage.setContent("Произошла ошибка, данные, полученные сервером, не прошли проверку.");
                        errorMessage.setSender(SERVER_NAME);
                }
            }
        } catch (Exception ex) {

        }
    }
}