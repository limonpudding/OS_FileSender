package psu.utils;

public class GlobalConstants {
    public final static int PORT = 25565;
    public static final int BUF_SIZE_FILE = 1024;
    public static final int BUF_SIZE_IP = 32;
    public static final int TIMEOUT = 500;

    public final static String SERVER_NAME = "SERVER_HOST";
    public static final String GET_SERVER_IP = "GET_SERVER_IP";

    //GUI messages
    public static final String SCOREBOARD_SERVER_TITLE = "Скоребоард сервер";
    public static final String FILE_SIZE_PATTERN = "Размер файла: {0} байт";
    public static final String SEND_FILE_SUCCESS = "Файл успешно отправлен";
    public static final String ACCEPT_FILE_SUCCESS = "Файл {0} успешно получен и доступен по пути ''{1}''";
    public static final String CONNECTION_LOST = "Соединение с пользователем ''{0}'' утеряно";
    public static final String MESSAGE_SEND_ERROR = "Ошибка при отправке сообщения клиенту ''{0}''";

    //layouts
    public final static String SERVER_STARTER_FXML = "/layouts/ServerStarter.fxml";
    public final static String CLIENT_MAIN_TEMPLATE_FXML = "/layouts/ClientMainTemplate.fxml";
    public final static String SERVER_MAIN_TEMPLATE_FXML = "/layouts/ServerMainTemplate.fxml";
}
