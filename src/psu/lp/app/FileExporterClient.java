package psu.lp.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import psu.lp.app.testConsole.message.client.MessageWorker;

public class FileExporterClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent loginForm = FXMLLoader.load(getClass().getResource("LoginTemplate.fxml"));
        //Parent root = FXMLLoader.load(getClass().getResource("FileExporterTemplate.fxml"));
        primaryStage.setTitle("Login | FileSender");
        primaryStage.setScene(new Scene(loginForm, 300, 90));
        //setUserAgentStylesheet(STYLESHEET_MODENA);
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
