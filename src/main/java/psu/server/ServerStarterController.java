package psu.server;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import psu.utils.CustomTimer;
import psu.utils.GlobalConstants;

import java.io.IOException;
import java.util.Timer;

import static psu.server.ServerStarter.initializeScoreboardStage;

public class ServerStarterController {

    @FXML
    private TextField leftTeamTextField;

    @FXML
    private TextField rightTeamTextField;

    @FXML
    private TextField minuteTextField;

    @FXML
    private TextField secondTextField;

    public static String leftTeam;
    public static String rightTeam;
    public static int minute;
    public static int second;

    @FXML
    private void createServerMainForm() {
        leftTeam = leftTeamTextField.getText();
        rightTeam = rightTeamTextField.getText();
        minute = Integer.parseInt(minuteTextField.getText());
        second = Integer.parseInt(secondTextField.getText());

        Pane mainWindow = getFromResource(GlobalConstants.SERVER_MAIN_TEMPLATE_FXML);
        Scene mainScene = new Scene(mainWindow);
        Stage stage = new Stage();
        stage.setScene(mainScene);
        stage.setTitle(GlobalConstants.SCOREBOARD_SERVER_TITLE);
        stage.setOnCloseRequest(event -> System.exit(0));
        stage.setResizable(false);
        initializeScoreboardStage.close();
        stage.show();

        new Timer().schedule(new CustomTimer(minute,second),0,1000);
    }

    private Pane getFromResource(String resource) {
        Pane mainWindow;
        try {
            mainWindow = FXMLLoader.load(getClass().getResource(resource));
        } catch (IOException e) {
            throw new RuntimeException("Не найден шаблон " + resource);
        }
        return mainWindow;
    }
}
