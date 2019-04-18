package psu.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import java.util.TimerTask;

import static psu.server.ServerController.globalTimer;

public class CustomTimer extends TimerTask {

    public static int minute;
    public static int second;

    public CustomTimer(int minute, int second) {
        CustomTimer.minute = minute;
        CustomTimer.second = second;
    }

    @Override
    public void run() {

        Platform.runLater(() -> {
            if (second > 0) {
                second--;
            } else {
                second = 59;
                minute--;
            }
            if (minute == 0 && second == 0) {
                Utils.showAlertMessageWithExit("Match", "Info", "Time is over", Alert.AlertType.INFORMATION);
                return;
            }
            globalTimer.setText(String.format("%02d", minute) + ":" + String.format("%02d", second));
        });
    }

}
