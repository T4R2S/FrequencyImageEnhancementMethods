package UI.Controllers;

import UI.Infrastructure.ComplexNumber;
import UI.Infrastructure.DiscreteFourierTransformer;
import UI.Infrastructure.Filters;
import UI.Presenters.FilterPresenter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;

public class FilterController {

    @FXML
    public Canvas image;
    public Canvas filterImage;
    public Canvas recoveredImage;

    public RadioButton batterword;
    public RadioButton notch;
    public TextField cutoffFreq;

    private WritableImage origin;
    private ComplexNumber[][][] dftResult;
    private ComplexNumber[][][] dftRecoveredResult;

    private boolean returnsRedChannel = false;
    private boolean returnsBlueChannel = false;
    private boolean returnsGreenChannel = false;

    private FilterPresenter presenter;
    private DiscreteFourierTransformer dft;

    public FilterController() {
        this.presenter = new FilterPresenter();
        this.dft = new DiscreteFourierTransformer();
    }

    void setImage(WritableImage image) {
        this.image.setHeight(image.getHeight());
        this.image.setWidth(image.getWidth());

        this.filterImage.setHeight(image.getHeight());
        this.filterImage.setWidth(image.getWidth());

        this.recoveredImage.setHeight(image.getHeight());
        this.recoveredImage.setWidth(image.getWidth());

        this.origin = image;

        GraphicsContext gc = this.image.getGraphicsContext2D();
        gc.drawImage(image, 0, 0);
    }

    public void setRedHandler(ActionEvent actionEvent) {

        Button but = (Button) actionEvent.getSource();

        returnsRedChannel = !returnsRedChannel;

        if (returnsRedChannel)
            but.setStyle("-fx-background-color: red;");
        else
            but.setStyle("");
    }

    public void setGreenHandler(ActionEvent actionEvent) {

        Button but = (Button) actionEvent.getSource();
        returnsGreenChannel = !returnsGreenChannel;

        if (returnsGreenChannel)
            but.setStyle("-fx-background-color: #1eff0fff;");
        else
            but.setStyle("");
    }

    public void setBlueHandler(ActionEvent actionEvent) {

        Button but = (Button) actionEvent.getSource();

        returnsBlueChannel = !returnsBlueChannel;

        if (returnsBlueChannel)
            but.setStyle("-fx-background-color: #60a7ffff;");
        else
            but.setStyle("");
    }

    public void clearFilterHandler(ActionEvent actionEvent) {
        GraphicsContext gc = this.image.getGraphicsContext2D();

        gc.clearRect(0, 0, origin.getWidth(), origin.getHeight());
        gc.drawImage(origin, 0, 0);
    }

    public void applyFilterHandler(ActionEvent actionEvent) {

        //Берем снапшот изображения, чтобо прочитать пиксели
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage snapshot = image.snapshot(params, null);

        PixelReader pixelReader = snapshot.getPixelReader();

        int height = (int) image.getHeight();
        int width = (int) image.getWidth();

        //Читаем пиксели изображения
        double[][][] signal = getSignal(height, width, pixelReader);

        //Генерация белого изображения, для вывода фильтра
        ComplexNumber[][][] whiteImage = presenter.generateWhiteImage(height, width);

        // Перобразование фурье.
        // Для выбранного изображение достаточно один раз посчитать преобразование фурье.
        if (dftResult == null) {
            dftResult = dft.transform(signal, height, width);
            double[][][] dftPixels = dft.getTransformResult(dftResult, height, width);
            presenter.draw(dftPixels, height, width, this.filterImage);
        }

        // Фильтр "Пробка"
        if (notch.isSelected()) {

            // Считываем значение частоты среза
            int cutoffFreq = Integer.parseInt(this.cutoffFreq.getText());

            //Фильтруем белое изображение, чтобы продемонстрировать сам фильтр
            ComplexNumber[][][] filterResult = Filters.notch(whiteImage, height, width, cutoffFreq);
            double[][][] dftPixels = dft.getTransformResult(filterResult, height, width);
            presenter.draw(dftPixels, height, width, this.filterImage);

            // Фильтруем исходное изображение
            dftRecoveredResult = Filters.notch(dftResult, height, width, cutoffFreq);

            // Проводим обратное преобразование фурье, чтобы получить результаты фильтрации
            ComplexNumber[][][] rDftResult = dft.inverse(dftRecoveredResult, height, width);
            double[][][] pixels = dft.getInverseResult(rDftResult, height, width);
            presenter.draw(pixels, height, width, this.recoveredImage);
        }

        // Фильтр Баттерворта (2-го порядка)
        if (batterword.isSelected()) {

            // Считываем значение частоты среза
            int cutoffFreq = Integer.parseInt(this.cutoffFreq.getText());

            //Фильтруем белое изображение, чтобы продемонстрировать сам фильтр
            ComplexNumber[][][] filterResult = Filters.lowPassBatterword(whiteImage, height, width, cutoffFreq);
            double[][][] dftPixels = dft.getTransformResult(filterResult, height, width);
            presenter.draw(dftPixels, height, width, this.filterImage);

            // Фильтруем исходное изображение
            dftRecoveredResult = Filters.lowPassBatterword(dftResult, height, width, cutoffFreq);

            // Проводим обратное преобразование фурье, чтобы получить результаты фильтрации
            ComplexNumber[][][] rDftResult = dft.inverse(dftRecoveredResult, height, width);
            double[][][] pixels = dft.getInverseResult(rDftResult, height, width);
            presenter.draw(pixels, height, width, this.recoveredImage);
        }
    }

    public void openResultsViewHandler(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../Views/resultsView.fxml"));
        Scene secondScene = new Scene(loader.load());

        Stage newWindow = new Stage();
        newWindow.setResizable(false);
        newWindow.setTitle("Результат");
        newWindow.setScene(secondScene);

        ResultsController controller = loader.getController();

        int height = (int) image.getHeight();
        int width = (int) image.getWidth();

        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        WritableImage filteredImage = recoveredImage.snapshot(params, null);

        Canvas dftCanvas = new Canvas(width, height);
        double[][][] dftPixels = dft.getTransformResult(dftResult, height, width);
        presenter.draw(dftPixels, height, width, dftCanvas);
        WritableImage dftImage = dftCanvas.snapshot(params, null);

        Canvas filteredDftCanvas = new Canvas(width, height);
        double[][][] dftRecoveredPixels = dft.getTransformResult(dftRecoveredResult, height, width);
        presenter.draw(dftRecoveredPixels, height, width, filteredDftCanvas);
        WritableImage filteredDftImage = filteredDftCanvas.snapshot(params, null);

        // Передаем результаты работы фильтра, исходное изображение
        controller.setImage(origin, dftImage, filteredImage, filteredDftImage, returnsRedChannel, returnsGreenChannel, returnsBlueChannel);

        newWindow.show();
    }


    private double[][][] getSignal(int height, int width, PixelReader pixelReader) {

        double[][][] pixels = new double[3][height][width];

        //можно запараллелить
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                Color color = pixelReader.getColor(x, y);

                double redColor = color.getRed();
                double greenColor = color.getGreen();
                double blueColor = color.getBlue();

                pixels[0][y][x] = redColor * ComplexNumber.step(x + y);
                pixels[1][y][x] = greenColor * ComplexNumber.step(x + y);
                pixels[2][y][x] = blueColor * ComplexNumber.step(x + y);
            }
        }

        return pixels;
    }
}
