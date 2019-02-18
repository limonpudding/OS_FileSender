package psu.client;

import psu.entities.Message;
import psu.entities.MessageType;

import java.io.*;
import java.net.Socket;
import java.util.List;

import static psu.utils.GlobalConstants.PORT;
import static psu.utils.GlobalConstants.SERVER_IP;

public class MessageWorker extends Thread {

    private FileExporterClientController controller;
    private final static String SERVER_NAME = "SERVER_HOST";

    private static MessageWorker instance;

    private static Socket clientSocket;
    private static String clientName;

    private static InputStream inputStream;
    private static OutputStream outputStream;

    private static ObjectInputStream messageInput;
    private static ObjectOutputStream messageOutput;

    private MessageWorker(Socket socket) {
        try {
            clientSocket = socket;
            //clientName = name;
            outputStream = clientSocket.getOutputStream();
            messageOutput = new ObjectOutputStream(outputStream);
            inputStream = clientSocket.getInputStream();
            messageInput = new ObjectInputStream(inputStream);
        } catch (Exception ex) {

        }
    }

    public synchronized static MessageWorker getInstance() throws IOException {
        if (instance == null) {
            instance = new MessageWorker(new Socket(SERVER_IP, PORT));
        }
        return instance;
    }

    @Override
    public void run() {
        try {
            // TODO не очевидно зачем это нужно, возможно стоит подумать о том,
            //  как это можно переделать (про работу и создание MessageWorker'а в целом)
            //  P.S. я уже и сам забыл, зачем это нужно
            while (controller == null) {
                Thread.sleep(2000);
            }
            while (clientSocket.isConnected()) {
                Message message = (Message) messageInput.readObject();
                switch (message.getMessageType()) {
//                    case AUTH:
//                        // Пришел ответ на попытку подключения
//                        break;
                    case USER_CONNECTED:
                        // Обновить список пользователей
                        controller.setUsersList((List<String>) message.getAttachment());
                        break;
                    case USER_DISCONNECTED:
                        // Обновить список пользователей
                        break;
                    case ERROR_SERVER:
                        // Обработать ошибку с сервера
                        break;
                    case NEW_FILE_REQUEST:
                        // Обработать получение файла
                        break;
                    case MESSAGE:
                        // Обработать полученное сообщение
                        controller.pushToTextArea(message.getSender(), message.getContent());
                        break;
                    case ERROR_CLIENT:
                        // ?
                        break;
                        default: //whaaat
                            System.out.println("Неизвестный тип сообщения");
                }
            }
            System.out.print("lol");
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Метод для авторизации на сервере
     *
     * @return 1 - успешно, 0 - ник занят, -1 что-то пошло не так
     */
    public int tryCreateConnection(String name) throws IOException, ClassNotFoundException {
        clientName = name;

        Message authMessage = new Message();
        authMessage.setMessageType(MessageType.AUTH);
        authMessage.setSender(clientName);
        authMessage.setContent(clientName);
        authMessage.setRecipient(SERVER_NAME);
        messageOutput.writeObject(authMessage);
        messageOutput.flush();
        Message answer = (Message) messageInput.readObject();
        if (answer.getMessageType() == MessageType.AUTH
                && answer.getSender().equals(SERVER_NAME)) {
            if (answer.getContent().equals("success")) {
                start();
                return 1;
            } else {
                return 0;
            }
        }
        return -1;
    }

    public void setController(FileExporterClientController controller) {
        this.controller = controller;
    }

    public static void sendFile() {

    }

    public void sendMessage(String message) throws IOException {
        Message authMessage = new Message();
        authMessage.setMessageType(MessageType.NEW_FILE_REQUEST);
        authMessage.setSender(clientName);
        authMessage.setContent(message);
        authMessage.setRecipient(SERVER_NAME);
        messageOutput.writeObject(authMessage);
        messageOutput.flush();
    }
}
