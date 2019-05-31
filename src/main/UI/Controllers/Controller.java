package UI.Controllers;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class Controller {

    @FXML
    public GridPane gridPane;

    public void openImageHandler(ActionEvent event) throws Exception {
        File file = loadImage();
        Task task2 = openNoiseView(file);

        Thread thread2 = new Thread(task2);
        thread2.run();
    }

    private Task openNoiseView(File file) {
        return new Task() {
            @Override
            protected Object call() throws IOException {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("../Views/noiseView.fxml"));
                Scene secondScene = new Scene(loader.load());

                Stage newWindow = new Stage();
                newWindow.setResizable(false);
                newWindow.setTitle("Шумы");
                newWindow.setScene(secondScene);

                NoiseController controller = loader.getController();
                Image image = new Image(new FileInputStream(file.getAbsoluteFile()), 100, 100, false, false);

                controller.openImage(image);

                newWindow.show();

                return null;
            }
        };
    }

    private File loadImage() throws Exception {

        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(gridPane.getScene().getWindow());

        if (file == null)
            throw new Exception("Не указан файл");

        return file;
    }

}
