package psu.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import psu.entities.ConnectionResult;

import java.io.IOException;

import static psu.client.ClientMessageWorker.clientMessager;
import static psu.server.ServerStarter.initializeScoreboardStage;
import static psu.server.ServerStarterController.leftTeam;
import static psu.server.ServerStarterController.rightTeam;
import static psu.utils.Utils.showAlertMessage;

public class ClientController {

    public static Label globalTimer;

    private ObservableList<String> leftTeamStrikersList;
    private ObservableList<String> rightTeamStrikersList;

    @FXML
    private ListView leftTeamStrikers;

    @FXML
    private ListView rightTeamStrikers;

    @FXML
    private TextField leftTeamStriker;

    @FXML
    private TextField rightTeamStriker;

    @FXML
    public void initialize() {
        leftTeamName.setText(leftTeam);
        rightTeamName.setText(rightTeam);
        globalTimer = timeToEnd;
        leftTeamStrikersList = FXCollections.observableArrayList();
        rightTeamStrikersList = FXCollections.observableArrayList();
    }

    @FXML
    private Label leftTeamName;

    @FXML
    private Label rightTeamName;

    @FXML
    private Label timeToEnd;

    @FXML
    private Label leftTeamScore;

    @FXML
    private Label rightTeamScore;

    int leftScore = 0;
    int rightScore = 0;

    @FXML
    @SuppressWarnings("unchecked")
    private void goalLeft(){
        leftTeamStrikersList.add(globalTimer.getText()+", "+leftTeamStriker.getText());
        leftTeamStrikers.setItems(leftTeamStrikersList);
        leftTeamScore.setText(String.valueOf(++leftScore));
    }

    @FXML
    @SuppressWarnings("unchecked")
    private void goalRight(){
        rightTeamStrikersList.add(globalTimer.getText()+", "+rightTeamStriker.getText());
        rightTeamStrikers.setItems(rightTeamStrikersList);
        rightTeamScore.setText(String.valueOf(++rightScore));
    }

    @FXML
    private void stopMatch(){

    }

    @FXML
    private void connectToServer() throws IOException, ClassNotFoundException {
        ConnectionResult connectionResult = ClientMessageWorker.getInstance().tryCreateConnection();
        switch (connectionResult) {
            case SUCCESS:
                showAlertMessage("Подключение", "Статус", "Успешно подключен", Alert.AlertType.INFORMATION);
                //createMainForm();
                initializeScoreboardStage.close();
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
}
