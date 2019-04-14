package psu.client;

import javafx.scene.control.Alert;
import psu.entities.ConnectionResult;
import psu.entities.Message;
import psu.entities.MessageType;
import psu.utils.FileSender;
import psu.utils.GlobalConstants;
import psu.utils.Utils;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import static psu.utils.GlobalConstants.*;
import static psu.utils.Utils.createNewMessage;
import static psu.utils.Utils.showAlertMessage;

public class ClientMessageWorker implements Runnable {

    private Controller controller;
    private final static String SERVER_NAME = "SERVER_HOST";

    public static Thread clientMessager;

    private static ClientMessageWorker instance;

    private static Socket clientSocket;
    private static String clientName;

    private static InputStream inputStream;
    private static OutputStream outputStream;

    private static ObjectInputStream messageInput;
    private static ObjectOutputStream messageOutput;

    public static Socket getClientSocket() {
        return clientSocket;
    }

    private ClientMessageWorker() {
    }

    public synchronized static ClientMessageWorker getInstance() {
        if (instance == null) {
            instance = new ClientMessageWorker();
        }
        return instance;
    }

    public ConnectionResult tryCreateConnection() throws IOException, ClassNotFoundException {
        String received = getServerIP(); //получаем IP адрес сервера

        createNewConnection(received);

        Message authMessage = new Message();
        authMessage.setMessageType(MessageType.AUTH);
        authMessage.setRecipient(SERVER_NAME);
        messageOutput.writeObject(authMessage);
        messageOutput.flush();
        Message answer = (Message) messageInput.readObject();
        if (answer.getMessageType() == MessageType.AUTH
                && answer.getSender().equals(SERVER_NAME)) {
            return ConnectionResult.SUCCESS;
        }
        return ConnectionResult.ERROR;
    }

    private static String getServerIP() throws IOException {
        byte[] buf = GlobalConstants.GET_SERVER_IP.getBytes();
        DatagramSocket socketUDP = new DatagramSocket();
        InetAddress address = InetAddress.getByName("192.168." + Utils.getThirdIpPart() + ".255");
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, GlobalConstants.PORT);
        socketUDP.send(packet);
        packet = new DatagramPacket(buf, buf.length);
        socketUDP.receive(packet);
        return new String(packet.getData(), 0, packet.getLength());
    }

    private void createNewConnection(String serverIP) {
        try {
            clientSocket = new Socket(serverIP, PORT);
            outputStream = clientSocket.getOutputStream();
            messageOutput = new ObjectOutputStream(outputStream);
            inputStream = clientSocket.getInputStream();
            messageInput = new ObjectInputStream(inputStream);
        } catch (Exception ex) {
            showAlertMessage("Подключение", "Статус", "Сервер недоступен", Alert.AlertType.WARNING);
        }
    }

    @Override
    public void run() {
        try {
            while (clientSocket.isConnected()) {
                Message message = (Message) messageInput.readObject();
                System.out.println("Пришёл MessageType: " + message.getMessageType().name());
                switch (message.getMessageType()) {
                    case USER_CONNECTED:
                        // Обновить список пользователей
                        controller.setUsersList((List<String>) message.getAttachment());
                        break;
                    case USER_DISCONNECTED:
                        // Обновить список пользователей
                        controller.setUsersList((List<String>) message.getAttachment());
                        break;
                    case ERROR_SERVER:
                        // Обработать ошибку с сервера
                        break;
                    case NEW_FILE_REQUEST:
                        FileSender.acceptFile(
                                new File(Controller.destinationFolder + "/" + message.getContent())
                        );
                        break;
                    case MESSAGE:
                        // Обработать полученное сообщение
                        controller.pushToTextArea(message.getSender(), message.getContent());
                        break;
                    case ERROR_CLIENT:
                        // ?
                        break;
                    default:
                        System.out.println("Неизвестный тип сообщения");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            //TODO нормально обработать
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void sendFileNotification() {
        Message notification = createNewMessage(MessageType.NEW_FILE_REQUEST);
        notification.setSender(clientName);
        notification.setContent(Controller.openedFile.getName());
        notification.setRecipient(Controller.selectedUser.toString());
        try {
            messageOutput.writeObject(notification);
            messageOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();//TODO нормально обработать
        }
    }

    public void sendMessage(String message) {
        Message authMessage = createNewMessage(MessageType.MESSAGE);
        authMessage.setSender(clientName);
        authMessage.setContent(message);
        authMessage.setRecipient(SERVER_NAME);
        try {
            messageOutput.writeObject(authMessage);
            messageOutput.flush();
        } catch (IOException e) {
            e.printStackTrace();//TODO нормально обработать
        }
    }
}
