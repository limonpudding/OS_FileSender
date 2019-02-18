package psu.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import psu.client.MessageWorker;

import java.io.IOException;
import java.util.List;

public class FileExporterClientController {

    private ObservableList<String> connectedUsers;

    @FXML
    private Button fileDialogButton;

    @FXML
    private Pane root;

    @FXML
    private TextField filePathTextField;

    @FXML
    private TextArea textArea;

    @FXML
    private Button sendButton;

    @FXML
    private ListView usersList;

    @FXML
    private void sendMessage() throws IOException {
        MessageWorker.getInstance().sendMessage(filePathTextField.getText());
    }

    public FileExporterClientController() throws IOException {
        MessageWorker.getInstance().setController(this);
        connectedUsers = FXCollections.observableArrayList();
        usersList = new ListView<String>();
        usersList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        usersList.setItems(connectedUsers);
    }

    @FXML
    private void openFileDialog() {
//        Alert kek = new Alert(Alert.AlertType.INFORMATION);
//        kek.setTitle("Тест");
//        kek.setHeaderText("Оу маааай");
//        kek.setContentText("Вот ето новости");
//        kek.showAndWait();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Укажите файл для отправки");
        fileChooser.showOpenDialog(root.getScene().getWindow());
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
        connectedUsers.setAll(users);
        usersList.setItems(connectedUsers);
    }
}
