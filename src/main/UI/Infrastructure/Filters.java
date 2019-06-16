package UI.Infrastructure;

public class Filters {

    public static ComplexNumber[][][] lowPassBatterword(ComplexNumber[][][] signal, int height, int width, int cutoffFreq) {

        ComplexNumber[][][] result = new ComplexNumber[3][height][width];

        int n = 2;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                double D = Math.pow(Math.pow(x - width / 2, 2) + Math.pow(y - height / 2, 2), 0.5);
                double val = 1 / (1 + Math.pow(D / cutoffFreq, 2 * n));

                for (int ch = 0; ch < 3; ch++) {

                    result[ch][y][x] = new ComplexNumber();

                    double re = val * signal[ch][y][x].getRe();
                    double im = val * signal[ch][y][x].getIm();

                    result[ch][y][x].setRe(re);
                    result[ch][y][x].setIm(im);
                }
            }
        }

        return result;
    }

    public static ComplexNumber[][][] notch(ComplexNumber[][][] signal, int height, int width, int cutoffFreq) {

        ComplexNumber[][][] result = new ComplexNumber[3][height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {

                double D = Math.pow(Math.pow(x - width / 2, 2) + Math.pow(y - height / 2, 2), 0.5);

                double val;

                if (D < cutoffFreq)
                    val = 0;
                else
                    val = 1;

                for (int ch = 0; ch < 3; ch++) {

                    result[ch][y][x] = new ComplexNumber();

                    double re = val * signal[ch][y][x].getRe();
                    double im = val * signal[ch][y][x].getIm();

                    result[ch][y][x].setRe(re);
                    result[ch][y][x].setIm(im);
                }
            }
        }

        return result;
    }

    public static ComplexNumber[] discreteFourierTransform(double[] signal, int n, int m) {

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

    public static ComplexNumber[] reverseDiscreteFourierTransform(ComplexNumber[] complexNumbers, int n, int m) {

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
