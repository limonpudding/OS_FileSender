package psu.lp.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class FileExporterClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        Parent root = FXMLLoader.load(getClass().getResource("FileExporterTemplate.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 600, 400));
        //setUserAgentStylesheet(STYLESHEET_MODENA);
        primaryStage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
