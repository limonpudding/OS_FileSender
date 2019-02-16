package psu.lp.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import psu.lp.app.testConsole.message.client.MessageWorker;

import java.io.IOException;

public class LoginFormController {

    @FXML
    private TextField userNameField;

    @FXML
    private Button continueButton;

    @FXML
    private void connectToServer() throws IOException, ClassNotFoundException {
        if (MessageWorker.getInstance().tryCreateConnection(userNameField.getText()) == 1) {
            Alert succesConnect = new Alert(Alert.AlertType.INFORMATION);
            succesConnect.setTitle("Подключение");
            succesConnect.setHeaderText("Статус");
            succesConnect.setContentText("Успешно подключен");
            succesConnect.showAndWait();
            new TestMainWindow();
        }
    }
}
