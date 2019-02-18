package psu.entities;

public enum MessageType {
    USER_CONNECTED("Подключился новый пользователь"),
    USER_DISCONNECTED("Пользователь отключился"),
    NEW_FILE_REQUEST("Запрос на отправку файла"),
    AUTH("Попытка аутентификации"),
    ERROR_SERVER("Ошибка на стороне сервера"),
    ERROR_CLIENT("Ошибка на стороне клиента"),
    MESSAGE("Сообщение");

    private String info;

    MessageType(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }
}
