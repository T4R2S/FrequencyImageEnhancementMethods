package UI.Infrastructure;

import javafx.scene.canvas.Canvas;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.Objects;
import java.util.stream.IntStream;

public class Filters {

    public static void midpoint(Canvas image, int n, int m, PixelReader pixelReader, PixelWriter pixelWriter) {

        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {

                double maxRed = 0.0;
                double minRed = 0.0;
                double maxGreen = 0.0;
                double minGreen = 0.0;
                double maxBlue = 0.0;
                double minBlue = 0.0;

                for (int k = 0; k < n; k++) {
                    for (int l = 0; l < m; l++) {

                        int x = j + (l - (n / 2));
                        int y = i + (k - (m / 2));

                        if (isOutOfBounds(x, y, image.getWidth(), image.getHeight()))
                            continue;

                        Color color = pixelReader.getColor(x, y);

                        double red = color.getRed() * 255.0;
                        double green = color.getGreen() * 255.0;
                        double blue = color.getBlue() * 255.0;

                        maxRed = Math.max(maxRed, red);
                        minRed = Math.max(minRed, red);
                        maxGreen = Math.max(maxGreen, green);
                        minGreen = Math.max(minGreen, green);
                        maxBlue = Math.max(maxBlue, blue);
                        minBlue = Math.max(minBlue, blue);
                    }
                }

                double redResult = 0.5 * (maxRed + minRed);
                double greenResult = 0.5 * (maxGreen + minGreen);
                double blueResult = 0.5 * (maxBlue + minBlue);

                pixelWriter.setColor(j, i, new Color(redResult / 255.0, greenResult / 255.0, blueResult / 255.0, 1.0));
            }
        }
    }

    public static void geometricMean(Canvas image, int n, int m, PixelReader pixelReader, PixelWriter pixelWriter) {

        for (int i = 0; i <= image.getHeight(); i++) {
            for (int j = 0; j <= image.getWidth(); j++) {

                double redMulResult = 1.0;
                double greenMulResult = 1.0;
                double blueMulResult = 1.0;
                int nm = n * m;

                for (int k = 0; k < n; k++) {
                    for (int l = 0; l < m; l++) {

                        int x = j + (l - (n / 2));
                        int y = i + (k - (m / 2));

                        if (isOutOfBounds(x, y, image.getWidth(), image.getHeight())) {
                            nm--;
                            continue;
                        }

                        Color color = pixelReader.getColor(x, y);

                        double red = color.getRed() * 255.0;
                        double green = color.getGreen() * 255.0;
                        double blue = color.getBlue() * 255.0;

                        redMulResult *= red;
                        greenMulResult *= green;
                        blueMulResult *= blue;
                    }
                }

                double exp = 1.0 / nm;

                double redResult = Math.pow(redMulResult, exp);
                double greenResult = Math.pow(greenMulResult, exp);
                double blueResult = Math.pow(blueMulResult, exp);

                pixelWriter.setColor(j, i, new Color(redResult / 255.0, greenResult / 255.0, blueResult / 255.0, 1.0));
            }
        }
    }

    public static void lowPassBatterword(Canvas image, PixelReader pixelReader, PixelWriter pixelWriter) throws InterruptedException {

        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        double[][] pixels = new double[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                Color color = pixelReader.getColor(x, y);

                double redColor = color.getRed();
//                double greenColor = color.getGreen();
//                double blueColor = color.getBlue();

                pixels[y][x] = redColor * ComplexNumber.step(x + y);
            }
        }

//        ComplexNumber[][] result = discreteFourierTransform2D(pixels, height, width);
        ComplexNumber[][] result = fastDiscreteFourierTransform2D(pixels, height, width, height, width);

        double[][] res = new double[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                if (result[y][x] == null)
                    result[y][x] = new ComplexNumber();

                double re = result[y][x].getRe();
                double im = result[y][x].getIm();

                // Логарифмическое преобразование
                res[y][x] = Math.log(1 + Math.sqrt(Math.pow(re, 2) + Math.pow(im, 2) / (width * height)));
            }
        }

        DoubleSummaryStatistics stats = Arrays.stream(res)
                .flatMapToDouble(Arrays::stream)
                .filter(Objects::nonNull)
                .summaryStatistics();

        double max = stats.getMax();
        double min = stats.getMin();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                double val = res[y][x];

                val = (val - min) / (max - min) * 255.0;

                Color color = new Color(val / 255.0, 0, 0, 1);

                pixelWriter.setColor(x, y, color);
            }
        }
    }

    public static void notch(Canvas image, int n, int m, PixelReader pixelReader, PixelWriter pixelWriter) {
    }

    private static boolean isOutOfBounds(int x, int y, double width, double height) {
        return x < 0 || y < 0 || x >= width || y >= height;
    }

    public static ComplexNumber[] discreteFourierTransform(double[] signal, int m, int n) {

        ComplexNumber[] result = new ComplexNumber[m];

        for (int u = 0; u < m; u++) {
            result[u] = new ComplexNumber();
            double re = 0;
            double im = 0;
            for (int x = 0; x < n; x++) {
                double angle = (2 * Math.PI * u * x) / m;
                re += signal[x] * Math.cos(angle);
                im += signal[x] * Math.sin(angle);
            }
            result[u].setRe(re);
            result[u].setIm(im);
        }

        return result;
    }

    public static ComplexNumber[][] discreteFourierTransform2D(double[][] signal, int n, int m) {

        ComplexNumber[][] result = new ComplexNumber[m][n];

        IntStream.range(0, n).parallel()
                .forEach(v -> IntStream.range(0, m)
                        .forEach(u -> {
                            try {
                                result[v][u] = calc(signal, n, m, v, u);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }));

        return result;
    }

    public static ComplexNumber[][] fastDiscreteFourierTransform2D(double[][] signal, int n, int m, int v, int u) throws InterruptedException {

        ComplexNumber[][] result = new ComplexNumber[m][n];

        if (n == 1 && m == 1)
            result[0][0] = calc(signal, n, m, v, u);
        else {

            double[][] part1 = new double[n / 4][m / 4];
            double[][] part2 = new double[n / 4][m / 4];
            double[][] part3 = new double[n / 4][m / 4];
            double[][] part4 = new double[n / 4][m / 4];

            for (int y = 0; y < n / 4; y++) {
                for (int x = 0; x < m / 4; x++) {
                    part1[y][x] = signal[y][x];
                    part2[y][x] = signal[y][x + m / 4];
                    part3[y][x] = signal[y + n / 4][x];
                    part4[y][x] = signal[y + n / 4][x + m / 4];
                }
            }

            ComplexNumber[][] result1 = fastDiscreteFourierTransform2D(part1, n / 4, m / 4, v - 1, u - 1);
            ComplexNumber[][] result2 = fastDiscreteFourierTransform2D(part2, n / 4, m / 4, v - 1, u - 1);
            ComplexNumber[][] result3 = fastDiscreteFourierTransform2D(part3, n / 4, m / 4, v - 1, u - 1);
            ComplexNumber[][] result4 = fastDiscreteFourierTransform2D(part4, n / 4, m / 4, v - 1, u - 1);

            for (int y = 0; y < n / 4; y++) {
                for (int x = 0; x < m / 4; x++) {
                    result[y][x] = result1[y][x];
                    result[y][x + m / 4] = result2[y][x];
                    result[y + n / 4][x] = result3[y][x];
                    result[y + n / 4][x + m / 4] = result4[y][x];
                }
            }
        }

        return result;
    }

    private static ComplexNumber calc(double[][] signal, int n, int m, int v, int u) throws InterruptedException {

        ComplexNumber result = new ComplexNumber();
        double re = 0;
        double im = 0;

        for (int y = 0; y < n; y++) {
            for (int x = 0; x < m; x++) {

                double angle = 2 * Math.PI * ((y * v) / (double) n + (x * u) / (double) m);
                re += signal[y][x] * Math.cos(angle) + signal[y][x] * Math.sin(angle);
                im += -signal[y][x] * Math.sin(angle) + signal[y][x] * Math.cos(angle);
            }
        }

        Thread.sleep(5);

        result.setRe(re);
        result.setIm(im);

        return result;
    }

    public static ComplexNumber[] reverseDiscreteFourierTransform(ComplexNumber[] complexNumbers, int m, int n) {

        ComplexNumber[] result = new ComplexNumber[m];

        for (int u = 0; u < m; u++) {

            result[u] = new ComplexNumber();
            double re = 0;
            double im = 0;

            for (int x = 0; x < n; x++) {

                double angle = (2 * Math.PI * u * x) / m;
                re += complexNumbers[x].getRe() * Math.cos(angle);
                im += complexNumbers[x].getIm() * Math.sin(angle);
            }

            result[u].setRe(re);
            result[u].setIm(im);
        }

        return result;
    }
}
