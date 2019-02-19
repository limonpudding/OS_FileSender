package psu.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import psu.utils.GlobalConstants;

import java.io.IOException;

public class LoginFormController {

    @FXML
    private TextField userNameField;

    @FXML
    private Button continueButton;

    @FXML
    public void initialize() {
        continueButton.addEventHandler(KeyEvent.KEY_PRESSED, this::continueHandler);
        userNameField.addEventHandler(KeyEvent.KEY_PRESSED, this::continueHandler);
    }

    @FXML
    private void connectToServer() throws IOException, ClassNotFoundException {
        if (MessageWorker.getInstance().tryCreateConnection(userNameField.getText()) == 1) {
            Alert succesConnect = new Alert(Alert.AlertType.INFORMATION);
            succesConnect.setTitle("Подключение");
            succesConnect.setHeaderText("Статус");
            succesConnect.setContentText("Успешно подключен");
            succesConnect.showAndWait();
            createMainForm();
            FileExporterClient.loginFormStage.close();
        }
    }

    private void createMainForm(){
        Pane mainWindow = getMainFormResource();
        Scene mainScene = new Scene(mainWindow, 700, 400);
        Stage stage = new Stage();
        stage.setScene(mainScene);
        stage.setTitle("Main File Sender Windows");
        stage.show();
    }

    private Pane getMainFormResource() {
        Pane mainWindow;
        try {
            mainWindow = FXMLLoader.load(getClass().getResource(GlobalConstants.MAIN_WINDOW_FXML));
        } catch (IOException e) {
            throw new RuntimeException("Не найден шаблон " + GlobalConstants.MAIN_WINDOW_FXML);
        }
        return mainWindow;
    }

    private void continueHandler(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.ENTER) {
            continueButton.fire();
        }
    }
}
