package psu.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import psu.entities.ConnectionResult;
import psu.server.ServerController;
import psu.utils.CustomTimer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import static psu.client.ClientMessageWorker.clientMessager;
import static psu.server.ServerStarter.initializeScoreboardStage;
import static psu.server.ServerStarterController.leftTeam;
import static psu.server.ServerStarterController.rightTeam;
import static psu.utils.Utils.showAlertMessage;

public class ClientController {

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
    private Label leftTeamName;

    @FXML
    private Label rightTeamName;

    @FXML
    private Label timeToEnd;

    @FXML
    private Label leftTeamScore;

    @FXML
    private Label rightTeamScore;

    @SuppressWarnings("unchecked")
    public void updateStatus(Map<String, Object> data) {
        Platform.runLater(() -> {
            leftTeamName.setText((String) data.get("leftTeamName"));
            rightTeamName.setText((String) data.get("rightTeamName"));
            leftTeamScore.setText((String) data.get("leftTeamScore"));
            rightTeamScore.setText((String) data.get("rightTeamScore"));

            ObservableList<String> tempLeftStrikers = FXCollections.observableArrayList();
            tempLeftStrikers.addAll((List) data.get("leftTeamStrikers"));

            ObservableList<String> tempRightStrikers = FXCollections.observableArrayList();
            tempRightStrikers.addAll((List) data.get("rightTeamStrikers"));

            rightTeamStrikers.setItems(tempRightStrikers);
            leftTeamStrikers.setItems(tempLeftStrikers);
            CustomTimer.minute = (Integer) data.get("minute");
            CustomTimer.second = (Integer) data.get("second");
        });
    }

    @FXML
    public void initialize() {
        ServerController.globalTimer = timeToEnd;
        new Timer().schedule(new CustomTimer(10,10),0,1000);
        try {
            connectToServer();
        } catch (IOException | ClassNotFoundException e) {
        }

        ClientMessageWorker.setController(this);
        ClientMessageWorker.getInstance().sendInitializeRequest();
    }

    @FXML
    private void connectToServer() throws IOException, ClassNotFoundException {
        ConnectionResult connectionResult = ClientMessageWorker.getInstance().tryCreateConnection();
        switch (connectionResult) {
            case SUCCESS:
                showAlertMessage("Подключение", "Статус", "Успешно подключен", Alert.AlertType.INFORMATION);
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
