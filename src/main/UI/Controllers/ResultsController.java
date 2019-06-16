package UI.Controllers;

import UI.Infrastructure.ComplexNumber;
import UI.Infrastructure.Filters;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Scale;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ResultsController {

    public HBox noisedImages;
    public HBox filteredImages;
    public GridPane gridPane;
    public HBox dftImages;
    public HBox filteredDftImages;

    void setImage(Image noisedImage, WritableImage dftImage, WritableImage filteredImage, WritableImage filteredDftImage, boolean redChannel, boolean greenChannel, boolean blueChannel) {

        addImage(noisedImages, noisedImage);
        addImage(dftImages, dftImage);
        addImage(filteredImages, filteredImage);
        addImage(filteredDftImages, filteredDftImage);

        if (redChannel) {

            Image image = getRedChannel(noisedImage);
            addImage(noisedImages, image);

            image = getRedChannel(dftImage);
            addImage(dftImages, image);

            image = getRedChannel(filteredImage);
            addImage(filteredImages, image);

            image = getRedChannel(filteredDftImage);
            addImage(filteredDftImages, image);
        }

        if (greenChannel) {

            Image image = getGreenChannel(noisedImage);
            addImage(noisedImages, image);

            image = getGreenChannel(dftImage);
            addImage(dftImages, image);

            image = getGreenChannel(filteredImage);
            addImage(filteredImages, image);

            image = getGreenChannel(filteredDftImage);
            addImage(filteredDftImages, image);
        }

        if (blueChannel) {

            WritableImage image = getBlueChannel(noisedImage);
            addImage(noisedImages, image);

            image = getBlueChannel(dftImage);
            addImage(dftImages, image);

            image = getBlueChannel(filteredImage);
            addImage(filteredImages, image);

            image = getBlueChannel(filteredDftImage);
            addImage(filteredDftImages, image);
        }
    }

    private WritableImage getRedChannel(Image image) {

        PixelReader pixelReader = image.getPixelReader();
        int width = (int)image.getWidth();
        int height = (int)image.getHeight();

        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {

                Color color = pixelReader.getColor(i, j);

                pixelWriter.setColor(i, j, new Color(color.getRed(), 0, 0, 1.0));
            }
        }

        return writableImage;
    }

    private WritableImage getGreenChannel(Image image) {

        PixelReader pixelReader = image.getPixelReader();
        int width = (int)image.getWidth();
        int height = (int)image.getHeight();

        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {

                Color color = pixelReader.getColor(i, j);

                pixelWriter.setColor(i, j, new Color(0, color.getGreen(), 0, 1.0));
            }
        }

        return writableImage;
    }

    private WritableImage getBlueChannel(Image image) {

        PixelReader pixelReader = image.getPixelReader();
        int width = (int)image.getWidth();
        int height = (int)image.getHeight();

        WritableImage writableImage = new WritableImage(width, height);
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {

                Color color = pixelReader.getColor(i, j);

                pixelWriter.setColor(i, j, new Color(0, 0, color.getBlue(), 1.0));
            }
        }

        return writableImage;
    }

    private void addImage(HBox box, Image image) {
        Canvas can = new Canvas(image.getWidth(), image.getHeight());
        GraphicsContext gc = can.getGraphicsContext2D();
        gc.drawImage(image, 0, 0);

        FlowPane flowPanes = new FlowPane(can);
        flowPanes.setStyle("-fx-border-color: black");
        flowPanes.setAlignment(Pos.CENTER);
        box.getChildren().add(flowPanes);
    }

    public void saveHandler(ActionEvent event) throws IOException {

        ArrayList<Node> childrens = new ArrayList<Node>();
        childrens.addAll(noisedImages.getChildren());
        childrens.addAll(filteredImages.getChildren());

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Files", "*.png"));
        File file = fileChooser.showSaveDialog(gridPane.getScene().getWindow());

        for (Node children : childrens) {
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            WritableImage snapshot = children.snapshot(params, null);

            BufferedImage bImage = SwingFXUtils.fromFXImage(snapshot, null);

            File temp = new File(file.getParent() + "/" + childrens.indexOf(children) + "_" + file.getName());
            ImageIO.write(bImage, "png", temp);
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Сохранение");
        alert.setHeaderText("Уведомление об успешном сохранении");
        alert.setContentText("Сохранение успешно завершено");
        alert.showAndWait();
    }

    public void openSpectrumViewHandler() throws IOException {
        double[] signal = new double[1024];

        for (int i = 0; i <= 20; i++)
            signal[i] = 1;

        for (int i = 0; i <= 20; i++)
            signal[i] = signal[i] * ComplexNumber.step(i);

        ComplexNumber[] complexNumbers = Filters.discreteFourierTransform(signal, signal.length, signal.length);

        ComplexNumber[] reverseComplexNumbers = Filters.reverseDiscreteFourierTransform(complexNumbers, complexNumbers.length, complexNumbers.length);


        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Views/spectrumView.fxml"));
        Scene secondScene = new Scene(loader.load());

        Stage newWindow = new Stage();
        newWindow.setResizable(false);
        newWindow.setTitle("Спектры");
        newWindow.setScene(secondScene);

        SpectrumController controller = loader.getController();

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);

//        controller.testChart(signal, complexNumbers, reverseComplexNumbers);

        newWindow.show();

    }
}
