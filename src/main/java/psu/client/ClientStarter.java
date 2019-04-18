package psu.client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import psu.utils.GlobalConstants;

public class ClientStarter extends Application {

    public static Stage loginFormStage;

    @Override
    public void start(Stage stage) throws Exception {
        Platform.setImplicitExit(false);
        loginFormStage = stage;
        Parent loginForm = FXMLLoader.load(getClass().getResource(GlobalConstants.CLIENT_MAIN_TEMPLATE_FXML));
        loginFormStage.setTitle(GlobalConstants.CLIENT_NAME);
        loginFormStage.setScene(new Scene(loginForm));
        loginFormStage.setOnCloseRequest(event -> System.exit(0));
        loginFormStage.setResizable(false);
        loginFormStage.show();
    }
}
