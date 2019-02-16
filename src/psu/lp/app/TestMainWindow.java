package psu.lp.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class TestMainWindow {

    TestMainWindow() throws IOException {
        Pane mainWindow = FXMLLoader.load(getClass().getResource("FileExporterTemplate.fxml"));
        Scene mainScene = new Scene(mainWindow, 600, 400);
        Stage stage = new Stage();
        stage.setScene(mainScene);
        stage.setTitle("Main File Sender Windows");
        stage.show();
    }
}
