package UI.Controllers;

import UI.Infrastructure.ComplexNumber;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

public class SpectrumController {

    public LineChart signalChart;
    public LineChart centSpetrChart;
    public LineChart ReChart;
    public LineChart ImChart;
    public LineChart recSignalChart;

    public void drawChart(double[][][] pixels, ComplexNumber[][][] complexNumbers, ComplexNumber[][][] reverseComplexNumbers) {

        XYChart.Series signal = new XYChart.Series();
        XYChart.Series recSignal = new XYChart.Series();
        XYChart.Series im = new XYChart.Series();
        XYChart.Series re = new XYChart.Series();
        XYChart.Series centSpetrum = new XYChart.Series();

//        for (int y = 0; y < height; y++) {
//            for (int x = 0; x < width; x++) {
//                for (int ch = 0; ch < 3; ch++) {
//                    signal.getData().add(new XYChart.Data(i, pixels[ch][y][x] * ComplexNumber.step(y * x)));
//
//                    re.getData().add(new XYChart.Data(i, complexNumbers[ch][y][x].getRe()));
//                    im.getData().add(new XYChart.Data(i, complexNumbers[ch][y][x].getIm()));
//                }
//            }
//        }

        for (int i = 0; i < 1024; i++) {


        }


        signalChart.getData().add(signal);
        recSignalChart.getData().add(recSignal);
        ImChart.getData().add(im);
        ReChart.getData().add(re);
        centSpetrChart.getData().add(centSpetrum);
    }
}
