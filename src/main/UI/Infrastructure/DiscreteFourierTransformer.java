package UI.Infrastructure;

import java.util.stream.IntStream;

public class DiscreteFourierTransformer {

    public DiscreteFourierTransformer() {
    }

    public ComplexNumber[][][] transform(double[][][] signal, int height, int width) {

        ComplexNumber[][][] recoveredSignal = new ComplexNumber[3][height][width];

        IntStream.range(0, height).parallel()
                .forEach(v -> IntStream.range(0, width)
                        .forEach(u -> {
                            try {
                                recoveredSignal[0][v][u] = calc(signal[0], height, width, v, u);
                                recoveredSignal[1][v][u] = calc(signal[1], height, width, v, u);
                                recoveredSignal[2][v][u] = calc(signal[2], height, width, v, u);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }));

        return recoveredSignal;
    }

    public ComplexNumber[][][] inverse(ComplexNumber[][][] signal, int height, int width) {
        ComplexNumber[][][] result = new ComplexNumber[3][height][width];

        IntStream.range(0, height).parallel()
                .forEach(v -> IntStream.range(0, width)
                        .forEach(u -> {
                            try {
                                result[0][v][u] = calc(signal[0], height, width, v, u);
                                result[1][v][u] = calc(signal[1], height, width, v, u);
                                result[2][v][u] = calc(signal[2], height, width, v, u);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }));

        return result;
    }

    public double[][][] getTransformResult(ComplexNumber[][][] signal, int height, int width) {

        double[][][] pixels = new double[3][height][width];

        //можно запараллелить
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int ch = 0; ch < 3; ch++) {

                    if (signal[ch][y][x] == null)
                        signal[ch][y][x] = new ComplexNumber();

                    double re = signal[ch][y][x].getRe();
                    double im = signal[ch][y][x].getIm();

                    // Логарифмическое преобразование
                    pixels[ch][y][x] = Math.log(1 + Math.sqrt(Math.pow(re, 2) + Math.pow(im, 2)));
                }
            }
        }

        return pixels;
    }

    public double[][][] getInverseResult(ComplexNumber[][][] signal, int height, int width) {

        double[][][] pixels = new double[3][height][width];

        //можно запараллелить
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                for (int ch = 0; ch < 3; ch++) {

                    if (signal[ch][y][x] == null)
                        signal[ch][y][x] = new ComplexNumber();

                    double re = signal[ch][y][x].getRe();
                    double im = signal[ch][y][x].getIm();

                    pixels[ch][y][x] = ((re + im) / (height * width)) * ComplexNumber.step(x + y);
                }

            }
        }

        return pixels;
    }

    private ComplexNumber calc(double[][] signal, int n, int m, int v, int u) throws InterruptedException {

        ComplexNumber result = new ComplexNumber();
        double re = 0;
        double im = 0;

        for (int y = 0; y < n; y++) {
            for (int x = 0; x < m; x++) {

                double angle = 2 * Math.PI * ((y * v) / (double) n + (x * u) / (double) m);

                re += signal[y][x] * Math.cos(angle);
                im += signal[y][x] * Math.sin(angle);
            }
        }

        Thread.sleep(5);

        result.setRe(re);
        result.setIm(im);

        return result;
    }

    private ComplexNumber calc(ComplexNumber[][] signal, int n, int m, int v, int u) throws InterruptedException {
        ComplexNumber result = new ComplexNumber();
        double re = 0;
        double im = 0;

        for (int y = 0; y < n; y++) {
            for (int x = 0; x < m; x++) {

                double angle = 2 * Math.PI * ((y * v) / (double) n + (x * u) / (double) m);
                re += signal[y][x].getRe() * Math.cos(angle);
                im += signal[y][x].getIm() * Math.sin(angle);
            }
        }

        Thread.sleep(5);

        result.setRe(re);
        result.setIm(im);

        return result;
    }
}
