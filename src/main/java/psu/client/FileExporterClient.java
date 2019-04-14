package psu.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import psu.utils.GlobalConstants;

public class FileExporterClient extends Application {

    public static Stage loginFormStage;

    @Override
    public void start(Stage stage) throws Exception {
        Platform.setImplicitExit(false);
        loginFormStage = stage;
        Parent loginForm = FXMLLoader.load(getClass().getResource(GlobalConstants.SERVER_STARTER_FXML));
        loginFormStage.setTitle("FileSender | Авторизация");
        loginFormStage.setScene(new Scene(loginForm));
        loginFormStage.setOnCloseRequest(event -> System.exit(0));
        loginFormStage.setResizable(false);
        loginFormStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
