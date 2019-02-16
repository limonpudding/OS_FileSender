package psu.lp.app;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import psu.lp.app.testConsole.message.client.MessageWorker;

import java.io.IOException;

public class FileExporterClientController {

    @FXML
    private Button fileDialogButton;

    @FXML
    private Pane root;

    @FXML
    private TextField filePathTextField;

    @FXML
    private TextArea textArea;

    @FXML
    private Button sendTestButton;

    @FXML
    private void sendTestMessage() throws IOException {
        MessageWorker.getInstance().sendMessage(filePathTextField.getText());
    }

    public FileExporterClientController() throws IOException {
        MessageWorker.getInstance().setController(this);
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

}
