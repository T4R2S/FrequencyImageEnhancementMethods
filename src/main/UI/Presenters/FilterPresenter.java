package UI.Presenters;

import UI.Infrastructure.ComplexNumber;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.Objects;

public class FilterPresenter {

    public FilterPresenter() {
    }

    public void draw(double[][][] pixels, int height, int width, Canvas image) {

        GraphicsContext gc = image.getGraphicsContext2D();
        PixelWriter pixelWriter = gc.getPixelWriter();

        pixels = normalize(height, width, pixels);

        //можно запараллелить
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                double red = pixels[0][y][x];
                double green = pixels[1][y][x];
                double blue = pixels[2][y][x];

                Color color = new Color(red, green, blue, 1);

                pixelWriter.setColor(x, y, color);
            }
        }
    }

    public ComplexNumber[][][] generateWhiteImage(int height, int width) {

        ComplexNumber[][][] result = new ComplexNumber[3][height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int ch = 0; ch < 3; ch++) {
                    result[ch][y][x] = new ComplexNumber();
                    result[ch][y][x].setRe(1.0);
                    result[ch][y][x].setIm(1.0);
                }
            }
        }

        return result;
    }

    private double[][][] normalize(int height, int width, double[][][] pixels) {

        double[][][] result = new double[3][height][width];

        for (int ch = 0; ch < 3; ch++) {
            DoubleSummaryStatistics stats = Arrays.stream(pixels[ch])
                    .flatMapToDouble(Arrays::stream)
                    .filter(Objects::nonNull)
                    .summaryStatistics();

            double max = stats.getMax();
            double min = stats.getMin();

            //можно запараллелить
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {

                    double val = pixels[ch][y][x];

                    result[ch][y][x] = (val - min) / (max - min);
                }
            }
        }

        return result;
    }


}
