package psu.server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import psu.entities.ConnectionResult;
import psu.utils.GlobalConstants;

import java.io.IOException;
import java.net.ServerSocket;

import static psu.utils.GlobalConstants.PORT;

public class ServerStarter extends Application {

    public static Stage initializeScoreboardStage;

    @Override
    public void start(Stage stage) throws Exception {

        createClientListener();

        Platform.setImplicitExit(false);
        initializeScoreboardStage = stage;
        Parent loginForm = FXMLLoader.load(getClass().getResource(GlobalConstants.SERVER_STARTER_FXML));
        initializeScoreboardStage.setTitle("Скорбоард сервер");
        initializeScoreboardStage.setScene(new Scene(loginForm));
        initializeScoreboardStage.setOnCloseRequest(event -> System.exit(0));
        initializeScoreboardStage.setResizable(false);
        initializeScoreboardStage.show();
    }

    private void createClientListener() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                IPSender ipSender = new IPSender();
                ipSender.start();
                while (true) {
                    UserConnection userConnection = new UserConnection(serverSocket.accept());
                    if (userConnection.getConnectionResult() == ConnectionResult.SUCCESS){
                        new Thread(userConnection).start();
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Алярм, всё рухнуло!!! (сервер почему-то больше не может принимать клиентов)");
            }
        }).start();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
