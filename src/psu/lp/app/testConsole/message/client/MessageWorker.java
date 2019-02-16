package psu.lp.app.testConsole.message.client;

import psu.lp.app.FileExporterClientController;
import psu.lp.app.testConsole.message.Message;
import psu.lp.app.testConsole.message.MessageType;

import java.io.*;
import java.net.Socket;

public class MessageWorker extends Thread {

    private FileExporterClientController fileExporterClientController;
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

    public static MessageWorker getInstance() throws IOException {
        if (instance == null) {
            instance = new MessageWorker(new Socket("localhost", 4004));
        }
        return instance;
    }

    @Override
    public void run() {
        try {
            while (clientSocket.isConnected()) {
                Message message = (Message) messageInput.readObject();
                switch (message.getMessageType()) {
//                    case AUTH:
//                        // Пришел ответ на попытку подключения
//                        break;
                    case USER_CONNECTED:
                        // Обновить список пользователей
                        break;
                    case USER_DISCONNECTED:
                        // Обновить список пользователей
                        break;
                    case ERROR_SERVER:
                        // Обработать ошибку с сервера
                        break;
                    case NEW_FILE_REQUEST:
                        // Обработать получение файла
                        fileExporterClientController.pushToTextArea(message.getSender(), message.getContent());
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
        fileExporterClientController = controller;
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
