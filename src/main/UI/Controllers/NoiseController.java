package UI.Controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class NoiseController {

    @FXML
    public Slider noiseSlider;
    public Text noiseValue;
    public Canvas image;
    public RadioButton unipolarNoise;
    public RadioButton bipolarNoise;

    private Image origin;

    @FXML
    public void initialize() {

        noiseSlider.valueProperty()
                .addListener((observable, oldValue, newValue)
                        -> noiseValue.setText("Регулятор шума: " + newValue.intValue() + "%"));
    }

    void openImage(Image image) {

        this.image.setHeight(image.getHeight());
        this.image.setWidth(image.getWidth());

        this.origin = image;

        GraphicsContext gc = this.image.getGraphicsContext2D();
        gc.drawImage(image, 0, 0);
    }

    public void cleanNoiseHandler(ActionEvent event) {
        GraphicsContext gc = this.image.getGraphicsContext2D();

        gc.clearRect(0, 0, origin.getWidth(), origin.getHeight());
        gc.drawImage(origin, 0, 0);

        noiseSlider.setValue(0);
    }

    public void applyNoiseHandler(ActionEvent event) {
        GraphicsContext gc = this.image.getGraphicsContext2D();

        PixelWriter pw = gc.getPixelWriter();
        Random random = new Random();

        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {

                int n = random.nextInt(100);

                if (n < (int) noiseSlider.getValue()) {

                    if (bipolarNoise.isSelected()) {

                        n = random.nextInt(3);

                        if (n <= 1)
                            pw.setColor(j, i, Color.BLACK);
                        else
                            pw.setColor(j, i, Color.WHITE);
                    }

                    if (unipolarNoise.isSelected())
                        pw.setColor(j, i, Color.BLACK);

                }
            }
        }
    }

    public void openFiltersHandler(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Views/filterView.fxml"));
        Scene secondScene = new Scene(loader.load());

        Stage newWindow = new Stage();
        newWindow.setResizable(false);
        newWindow.setTitle("Фильтры");
        newWindow.setScene(secondScene);

        FilterController controller = loader.getController();

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage snapshot = image.snapshot(params, null);

        controller.setImage(snapshot);

        newWindow.show();
    }
}
