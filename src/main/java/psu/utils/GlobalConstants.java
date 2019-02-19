package psu.utils;

public class GlobalConstants {
    public final static String LOGIN_WINDOW_FXML = "/layouts/LoginTemplate.fxml";
    public final static String SERVER_NAME = "SERVER_HOST";
    public final static String SERVER_IP = "localhost";
    public final static int PORT = 25565;
    public final static String MAIN_WINDOW_FXML = "/layouts/FileExporterTemplate.fxml";
    public static final String FILE_SIZE_PATTERN = "Размер файла: {0} байт";
    public static final int BUF_SIZE = 1024;
    public static final String ACCEPTED_FILES_PATH = "acceptedFiles/{0}";
    public static final byte[] EOF_GUID = "314fce52-7aed-4a75-884b-39e86b128fa1".getBytes();
}
