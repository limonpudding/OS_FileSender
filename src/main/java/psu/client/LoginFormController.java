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
import psu.entities.ConnectionResult;
import psu.utils.GlobalConstants;

import java.io.IOException;
import java.text.MessageFormat;

import static psu.client.ClientMessageWorker.clientMessager;
import static psu.utils.Utils.showAlertMessage;

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
        ConnectionResult connectionResult = ClientMessageWorker.getInstance().tryCreateConnection(userNameField.getText());
        switch (connectionResult) {
            case SUCCESS:
                showAlertMessage("Подключение", "Статус", "Успешно подключен", Alert.AlertType.INFORMATION);
                createMainForm();
                FileExporterClient.loginFormStage.close();
                clientMessager = new Thread(ClientMessageWorker.getInstance());
                clientMessager.start();
                break;
            case USERNAME_NOT_AVAILABLE:
                showAlertMessage("Подключение", "Статус", "Это имя занято, попробуйте другое", Alert.AlertType.WARNING);
                break;
            case ERROR:
                showAlertMessage("Подключение", "Статус", "Неизвестная ошибка", Alert.AlertType.ERROR);
                break;
            default:
                throw new RuntimeException("Неизвестный тип сообщения при попытке подключения к серверу");
        }
    }

    private void createMainForm() {
        Pane mainWindow = getMainFormResource();
        Scene mainScene = new Scene(mainWindow, 700, 400);
        Stage stage = new Stage();
        stage.setScene(mainScene);
        stage.setTitle(MessageFormat.format(GlobalConstants.FILE_SENDER_TITLE, userNameField.getText()));
        stage.setOnCloseRequest(event -> System.exit(0));
        stage.setResizable(false);//isResizable()
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
