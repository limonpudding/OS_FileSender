package psu.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import psu.utils.FileSender;
import psu.utils.GlobalConstants;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import static psu.utils.Utils.getFileSize;
import static psu.utils.Utils.showAlertMessage;

public class FileExporterClientController {

    public static Object selectedUser;

    public static File openedFile;

    public static ObservableList<String> connectedUsers;

    @FXML
    private Button fileDialogButton;

    @FXML
    private Pane root;

    @FXML
    private TextField filePathTextField;

    @FXML
    private TextArea textArea;

    @FXML
    private Button sendMessageButton;

    @FXML
    private Button sendFileButton;

    @FXML
    private ListView usersList;

    @FXML
    private Label fileSize;

    @FXML
    private void sendMessage() throws IOException {
        ClientMessageWorker.getInstance().sendMessage(filePathTextField.getText());
    }

    @FXML
    private void sendFile() {
        selectedUser = usersList.getSelectionModel().getSelectedItem();

        if (selectedUser == null) {
            showAlertMessage("Отправка файла", "Статус", "Не выбран получатель файла", Alert.AlertType.ERROR);
            return;
        }

        if (openedFile == null) {
            showAlertMessage("Отправка файла", "Статус", "Не выбран файл", Alert.AlertType.ERROR);
            return;
        }

        ClientMessageWorker.getInstance().sendFileNotification();
        FileSender.sendFile(openedFile);
    }

    public FileExporterClientController() throws IOException {
        connectedUsers = FXCollections.observableArrayList();
        usersList = new ListView<String>();
        usersList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        usersList.setItems(connectedUsers);
        ClientMessageWorker.getInstance().setController(this);
    }

    @FXML
    private void openFileDialog() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Укажите файл для отправки");
        openedFile = fileChooser.showOpenDialog(root.getScene().getWindow());
        if (openedFile != null) {
            fileSize.setText(MessageFormat.format(GlobalConstants.FILE_SIZE_PATTERN, getFileSize(openedFile)));
            filePathTextField.setText(openedFile.getAbsolutePath());
        }
    }

    public synchronized void pushToTextArea(String sender, String message) {
        textArea.appendText(sender + ": " + message);
    }

    public synchronized void addUserToListView(String name) {
        //connectedUsers = FXCollections.observableArrayList();
        connectedUsers.add(name);
        //usersList.setItems(connectedUsers);
    }

    public synchronized void setUsersList(List<String> users) {
        Platform.runLater(() -> connectedUsers.setAll(users));
        usersList.setItems(connectedUsers);
    }
}
