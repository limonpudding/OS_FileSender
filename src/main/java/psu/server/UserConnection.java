package psu.server;

import psu.entities.Message;
import psu.entities.MessageType;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class UserConnection extends Thread {
    private final static String SERVER_NAME = "SERVER_HOST";
    private static HashMap<String, Socket> connectionKeeper;
    private static ArrayList<UserConnection> userConnections = new ArrayList<>();

    private String userName;
    private Socket userSocket;

    private InputStream inputStream;
    private OutputStream outputStream;

    private ObjectInputStream messageInput;
    private ObjectOutputStream messageOutput;

    public UserConnection(Socket socket) throws IOException, ClassNotFoundException {
        userSocket = socket;

        // Инициализация потоков ввода/вывода
        outputStream = userSocket.getOutputStream();
        messageOutput = new ObjectOutputStream(outputStream);
        inputStream = userSocket.getInputStream();
        messageInput = new ObjectInputStream(inputStream);



        Message authRequest = (Message) messageInput.readObject();
        if (authRequest.getMessageType() == MessageType.AUTH) {
            userName = authRequest.getSender();
            //connectionKeeper.put(userName, userSocket);
            userConnections.add(this);

            Message authResponce = new Message();
            authResponce.setMessageType(MessageType.AUTH);
            authResponce.setSender(SERVER_NAME);
            authResponce.setContent("success");
            authResponce.setRecipient(userName);
            messageOutput.writeObject(authResponce);
            messageOutput.flush();

            notifyUserConnected();
        }
    }

    @Override
    public void run() {
        try {


            //Message tryAuth = (Message) messageInput.readObject();
            //проверка уникальности имени
            //первое сообщение всегда должно быть типа AUTH --> в content лежит имя клиента
            //userName = tryAuth.getContent();

            //System.out.println(tryAuth.getMessageType().getInfo() + " пользователя с ником " + tryAuth.getContent());
            while (userSocket.isConnected()) {
                Message test = (Message) messageInput.readObject();
                switch (test.getMessageType()) {
                    case NEW_FILE_REQUEST:
                        System.out.println("попытка отправки файла");
                        // попытка отправки файла

                        break;
                    case MESSAGE:
                        Message sendTestMessage = new Message();
                        sendTestMessage.setMessageType(MessageType.MESSAGE);
                        sendTestMessage.setSender(test.getSender());
                        sendTestMessage.setContent(test.getContent() + "\n");

                        //для всех клиентов
                        messageForAll(sendTestMessage);
                        break;
                    case ERROR_CLIENT:
                        break;
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

    private synchronized void messageForAll(Message sendTestMessage) throws IOException {
        Iterator users = userConnections.listIterator();
        while (users.hasNext()) {
            UserConnection currentUserConnection = (UserConnection)users.next();
            sendTestMessage.setRecipient(currentUserConnection.getName());
            currentUserConnection.getMessageOutput().writeObject(sendTestMessage);
            currentUserConnection.getMessageOutput().flush();
        }
    }

    private synchronized void notifyUserConnected() throws IOException {
        Message notify = new Message();
        notify.setMessageType(MessageType.USER_CONNECTED);
        notify.setSender(SERVER_NAME);
        notify.setContent(userName);
        messageForAll(notify);
    }

    private ObjectOutputStream getMessageOutput() {
        return messageOutput;
    }
}