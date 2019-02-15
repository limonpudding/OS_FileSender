package psu.lp.app;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;

public class FileExporterClientController {

    @FXML
    private Button fileDialogButton;

    @FXML
    private Pane root;

    @FXML
    private TextField filePathTextField;

    @FXML
    private void openFileDialog() {
//        Alert kek = new Alert(Alert.AlertType.INFORMATION);
//        kek.setTitle("Тест");
//        kek.setHeaderText("Оу маааай");
//        kek.setContentText("Вот ето новости");
//        kek.showAndWait();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Укажите файл для отправки");
        fileChooser.showOpenDialog(root.getScene().getWindow());
    }
}
