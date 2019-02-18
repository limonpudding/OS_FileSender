package psu.lp.app;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import psu.lp.app.testConsole.message.client.MessageWorker;

import java.io.IOException;

import static psu.lp.app.FileExporterClient.loginFormStage;
import static psu.lp.app.GlobalConstants.MAIN_WINDOW_FXML;

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
            createMainForm();
            loginFormStage.close();
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
            mainWindow = FXMLLoader.load(getClass().getResource(MAIN_WINDOW_FXML));
        } catch (IOException e) {
            throw new RuntimeException("Не найден шаблон " + MAIN_WINDOW_FXML);
        }
        return mainWindow;
    }
}
