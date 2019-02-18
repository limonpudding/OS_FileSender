package psu.lp.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static psu.lp.app.GlobalConstants.LOGIN_WINDOW_FXML;

public class FileExporterClient extends Application {

    public static Stage loginFormStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        loginFormStage = primaryStage;
        Parent loginForm = FXMLLoader.load(getClass().getResource(LOGIN_WINDOW_FXML));
        //Parent root = FXMLLoader.load(getClass().getResource("FileExporterTemplate.fxml"));
        loginFormStage.setTitle("Login | FileSender");
        loginFormStage.setScene(new Scene(loginForm, 300, 90));
        //setUserAgentStylesheet(STYLESHEET_MODENA);
        loginFormStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
