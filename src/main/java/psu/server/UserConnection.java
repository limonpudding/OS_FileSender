package psu.server;

import psu.entities.ConnectionResult;
import psu.entities.Message;
import psu.entities.MessageType;
import psu.utils.FileSender;
import psu.utils.GlobalConstants;
import psu.utils.Utils;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UserConnection implements Runnable {

    private static List<UserConnection> userConnections = new ArrayList<>();

    private ConnectionResult connectionResult = ConnectionResult.NONE;

    private String userName;

    private Socket userSocket;

    private InputStream inputStream;
    private OutputStream outputStream;

    private ObjectInputStream messageInput;
    private ObjectOutputStream messageOutput;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public ConnectionResult getConnectionResult() {
        return connectionResult;
    }

    public String getUserName() {
        return userName;
    }

    public Socket getUserSocket() {
        return userSocket;
    }

    public static List<UserConnection> getUserConnections() {
        return userConnections;
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
            Message authResponse = Utils.createNewMessage();
            authResponse.setMessageType(MessageType.AUTH);
            authResponse.setSender(GlobalConstants.SERVER_NAME);
            setUserName(authRequest.getSender());

            if (isExistUsername(authRequest.getSender())
                    || GlobalConstants.SERVER_NAME.equals(authRequest.getSender())){
                connectionResult = ConnectionResult.USERNAME_NOT_AVAILABLE;
            } else {
                userConnections.add(this);
                connectionResult = ConnectionResult.SUCCESS;
            }
            authResponse.setAttachment(connectionResult);
            messageOutput.writeObject(authResponse);
            messageOutput.flush();

            if (authResponse.getAttachment().equals(ConnectionResult.SUCCESS)) {
                notifyUserConnected();
            }
        }
    }

    @Override
    public void run() {
        try {
            while (userSocket.isConnected()) {
                Message acceptedMessage = (Message) messageInput.readObject();
                Message messageForSend;
                System.out.println(acceptedMessage.getMessageType());
                switch (acceptedMessage.getMessageType()) {
                    case NEW_FILE_REQUEST:
                        messageForSend = Utils.createNewMessage();
                        messageForSend.setMessageType(MessageType.NEW_FILE_REQUEST);
                        messageForSend.setRecipient(acceptedMessage.getRecipient());
                        messageForSend.setSender(acceptedMessage.getSender());
                        messageForSend.setContent(acceptedMessage.getContent());
                        Utils.putInOutStreamToUser(messageForSend, acceptedMessage.getRecipient());

                        FileSender.redirectFile(acceptedMessage.getSender(), acceptedMessage.getRecipient());
                        break;
                    case MESSAGE:
                        messageForSend = Utils.createNewMessage();
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
                        Message errorMessage = Utils.createNewMessage();
                        errorMessage.setMessageType(MessageType.ERROR_SERVER);
                        errorMessage.setContent("Произошла ошибка, данные, полученные сервером, не прошли проверку.");
                        errorMessage.setSender(GlobalConstants.SERVER_NAME);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Сервер упал при ответе на какое-то сообщение");
        }
    }

    private synchronized void messageForAll(Message sendTestMessage) throws IOException {
        for (UserConnection currentUserConnection : userConnections) {
            sendTestMessage.setRecipient(currentUserConnection.getUserName());
            currentUserConnection.getMessageOutput().writeObject(sendTestMessage);
            currentUserConnection.getMessageOutput().flush();
        }
    }

    private synchronized void notifyUserConnected() throws IOException {
        Message messageForSend = Utils.createNewMessage();
        messageForSend.setMessageType(MessageType.USER_CONNECTED);
        messageForSend.setSender(GlobalConstants.SERVER_NAME);
        messageForSend.setContent(getUserName());
        messageForSend.setAttachment(createListNames());

        //для всех клиентов
        messageForAll(messageForSend);
    }

    public ObjectOutputStream getMessageOutput() {
        return messageOutput;
    }

    private List<String> createListNames() {
        List<String> result = new ArrayList<>();
        for (UserConnection connection : userConnections) {
            result.add(connection.getUserName());
        }

        return result;
    }

    boolean isExistUsername(String userName){
        for (UserConnection userConnection:userConnections) {
            if (userConnection.getUserName().equals(userName)){
                return true;
            }
        }
        return false;
    }
}