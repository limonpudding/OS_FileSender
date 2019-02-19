package psu.server;

import psu.entities.Message;
import psu.entities.MessageType;
import psu.utils.GlobalConstants;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class UserConnection extends Thread {

    private static List<UserConnection> userConnections = new ArrayList<>();

    private String userName;

    private Socket userSocket;

    private InputStream inputStream;
    private OutputStream outputStream;

    private ObjectInputStream messageInput;
    private ObjectOutputStream messageOutput;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public UserConnection(Socket socket) throws IOException, ClassNotFoundException {
        userSocket = socket;

        // Инициализация потоков ввода/вывода
        outputStream = userSocket.getOutputStream();
        messageOutput = new ObjectOutputStream(outputStream);
        inputStream = userSocket.getInputStream();
        messageInput = new ObjectInputStream(inputStream);


        Message authRequest = (Message) messageInput.readObject();
        if (authRequest.getMessageType() == MessageType.AUTH) {
            setUserName(authRequest.getSender());
            //connectionKeeper.put(userName, userSocket);
            userConnections.add(this);

            Message authResponce = createNewMessage();
            authResponce.setMessageType(MessageType.AUTH);
            authResponce.setSender(GlobalConstants.SERVER_NAME);
            authResponce.setContent("success");
            authResponce.setRecipient(getUserName());
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
                Message acceptedMessage = (Message) messageInput.readObject();
                Message messageForSend;
                switch (acceptedMessage.getMessageType()) {
                    case NEW_FILE_REQUEST:
                        System.out.println("попытка отправки файла");
                        // попытка отправки файла

                        break;
                    case MESSAGE:
                        messageForSend = createNewMessage();
                        messageForSend.setMessageType(MessageType.MESSAGE);
                        messageForSend.setSender(acceptedMessage.getSender());
                        messageForSend.setContent(acceptedMessage.getContent() + "\n");

                        //для всех клиентов
                        messageForAll(messageForSend);
                        break;
                    case ERROR_CLIENT:
                        break;
                    case USER_CONNECTED:
                        break;
                    default:
                        System.out.println("Неверный тип сообщения");
                        Message errorMessage = createNewMessage();
                        errorMessage.setMessageType(MessageType.ERROR_SERVER);
                        errorMessage.setContent("Произошла ошибка, данные, полученные сервером, не прошли проверку.");
                        errorMessage.setSender(GlobalConstants.SERVER_NAME);
                }
            }
        } catch (Exception ex) {

        }
    }

    private Message createNewMessage() {
        return new Message();
    }

    private synchronized void messageForAll(Message sendTestMessage) throws IOException {
        Iterator users = userConnections.listIterator();
        while (users.hasNext()) {
            UserConnection currentUserConnection = (UserConnection) users.next();
            sendTestMessage.setRecipient(currentUserConnection.getName());
            currentUserConnection.getMessageOutput().writeObject(sendTestMessage);
            currentUserConnection.getMessageOutput().flush();
        }
    }

    private synchronized void notifyUserConnected() throws IOException {
        Message messageForSend = createNewMessage();
        messageForSend.setMessageType(MessageType.USER_CONNECTED);
        messageForSend.setSender(GlobalConstants.SERVER_NAME);
        messageForSend.setContent(getUserName());
        messageForSend.setAttachment(createListNames());

        //для всех клиентов
        messageForAll(messageForSend);
    }

    private ObjectOutputStream getMessageOutput() {
        return messageOutput;
    }

    private List<String> createListNames() {
        List<String> result = new ArrayList<>();
        for (UserConnection connection : userConnections) {
            result.add(connection.getUserName());
        }

        return result;
    }
}