package psu.server;

import psu.entities.ConnectionResult;
import psu.entities.Message;
import psu.entities.MessageType;
import psu.utils.CustomTimer;
import psu.utils.FileSender;
import psu.utils.GlobalConstants;
import psu.utils.Utils;

import java.io.*;
import java.net.Socket;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static psu.utils.GlobalConstants.SERVER_NAME;

public class UserConnection implements Runnable {

    private static List<UserConnection> userConnections = new ArrayList<>();

    private static ServerController controller;

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
            Message authResponse = Utils.createNewMessage(MessageType.AUTH);
            authResponse.setSender(SERVER_NAME);
            setUserName(authRequest.getSender());

            userConnections.add(this);
            connectionResult = ConnectionResult.SUCCESS;

            authResponse.setAttachment(connectionResult);
            messageOutput.writeObject(authResponse);
            messageOutput.flush();

            if (authResponse.getAttachment().equals(ConnectionResult.SUCCESS)) {
                notifyUserConnected();
            }
        }
    }

    private boolean isIncorrectUsername(Message authRequest) {
        String sender = authRequest.getSender();
        return sender == null
                || isExistUsername(sender)
                || SERVER_NAME.equals(sender)
                || sender.trim().equals("");
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
                        messageForSend = Utils.createNewMessage(MessageType.NEW_FILE_REQUEST);
                        messageForSend.setRecipient(acceptedMessage.getRecipient());
                        messageForSend.setSender(acceptedMessage.getSender());
                        messageForSend.setContent(acceptedMessage.getContent());
                        Utils.putInOutStreamToUser(messageForSend, acceptedMessage.getRecipient());

                        FileSender.redirectFile(acceptedMessage.getSender(), acceptedMessage.getRecipient());
                        break;
                    case MESSAGE:
                        messageForSend = Utils.createNewMessage(MessageType.MESSAGE);
                        messageForSend.setSender(acceptedMessage.getSender());
                        messageForSend.setContent(acceptedMessage.getContent());

                        //для всех клиентов
                        messageForAll(messageForSend);
                        break;
                    case INITIALIZE_REQUEST:
                        //для всех клиентов
                        sendStatusForAll();
                        break;
                    case ERROR_CLIENT:
                        break;
                    case USER_CONNECTED:
                        break;
                    case USER_DISCONNECTED:
                        break;
                    default:
                        System.out.println("Неверный тип сообщения");
                        Message errorMessage = Utils.createNewMessage(MessageType.ERROR_SERVER);
                        errorMessage.setContent("Произошла ошибка, данные, полученные сервером, не прошли проверку.");
                        errorMessage.setSender(SERVER_NAME);
                }
            }
        } catch (Exception ex) {
            deleteThisConnection();
            System.out.println(MessageFormat.format(GlobalConstants.CONNECTION_LOST, userName));
        }
    }

    private synchronized void deleteUserConnectionByUsername(String userName) {
        if (!userConnections.removeIf(userConnection -> userConnection.getUserName().equals(userName))) {
            throw new RuntimeException("Пользователь с таким именем не найтен");
        }
        Message message = Utils.createNewMessage(MessageType.USER_DISCONNECTED);
        message.setSender(SERVER_NAME);
        message.setContent(userName);
        message.setAttachment(createListNames());
        messageForAll(message);
    }

    private synchronized void deleteThisConnection() {
        deleteUserConnectionByUsername(userName);
    }

    private synchronized void messageForAll(Message sendTestMessage) {
        for (UserConnection userConnection : userConnections) {
            sendTestMessage.setRecipient(userConnection.getUserName());
            try {
                userConnection.getMessageOutput().writeObject(sendTestMessage);
                userConnection.getMessageOutput().flush();
            } catch (IOException e) {
                throw new RuntimeException(MessageFormat.format(GlobalConstants.MESSAGE_SEND_ERROR, userConnection.getUserName()));
            }
        }
    }

    public static synchronized void sendExitForAll(){
        Message messageForSend = Utils.createNewMessage(MessageType.STOP_MATCH);
        messageForSend.setSender(SERVER_NAME);

        for (UserConnection userConnection : userConnections) {
            messageForSend.setRecipient(userConnection.getUserName());
            try {
                userConnection.getMessageOutput().writeObject(messageForSend);
                userConnection.getMessageOutput().flush();
            } catch (IOException e) {
                throw new RuntimeException(MessageFormat.format(GlobalConstants.MESSAGE_SEND_ERROR, userConnection.getUserName()));
            }
        }
    }

    public static synchronized void sendStatusForAll() {

        Message messageForSend = Utils.createNewMessage(MessageType.INITIALIZE_REQUEST);
        messageForSend.setSender(SERVER_NAME);

        Map<String, Object> currentStatus = new HashMap<>();

        currentStatus.put("leftTeamName", controller.leftTeamName.getText());
        currentStatus.put("rightTeamName", controller.rightTeamName.getText());
        currentStatus.put("leftTeamScore", controller.leftTeamScore.getText());
        currentStatus.put("rightTeamScore", controller.rightTeamScore.getText());
        currentStatus.put("rightTeamStrikers", new ArrayList<String>(controller.rightTeamStrikers.getItems()));
        currentStatus.put("leftTeamStrikers", new ArrayList<String>(controller.leftTeamStrikers.getItems()));
        currentStatus.put("minute", CustomTimer.minute);
        currentStatus.put("second", CustomTimer.second);

        messageForSend.setAttachment(currentStatus);

        for (UserConnection userConnection : userConnections) {
            messageForSend.setRecipient(userConnection.getUserName());
            try {
                userConnection.getMessageOutput().writeObject(messageForSend);
                userConnection.getMessageOutput().flush();
            } catch (IOException e) {
                throw new RuntimeException(MessageFormat.format(GlobalConstants.MESSAGE_SEND_ERROR, userConnection.getUserName()));
            }
        }
    }

    private synchronized void notifyUserConnected() throws IOException {
        Message messageForSend = Utils.createNewMessage(MessageType.USER_CONNECTED);
        messageForSend.setSender(SERVER_NAME);
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

    boolean isExistUsername(String userName) {
        for (UserConnection userConnection : userConnections) {
            if (userConnection.getUserName().equals(userName)) {
                return true;
            }
        }
        return false;
    }

    public ServerController getController() {
        return controller;
    }

    public static void setController(ServerController serverController) {
        controller = serverController;
    }
}