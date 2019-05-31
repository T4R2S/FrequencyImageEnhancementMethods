package UI.Infrastructure;

public class ComplexNumber {

    public ComplexNumber() {
        Re = 0;
        Im = 0;
    }

    private double Re;
    private double Im;

    public double getRe() {
        return Re;
    }

    public void setRe(double re) {
        Re = re;
    }

    public double getIm() {
        return Im;
    }

    public void setIm(double im) {
        Im = im;
    }

    public static double step(int n) {

        int scale = n % 2 != 0 ? 1 : 0;

        return 1 - 2 * scale;
    }
}
