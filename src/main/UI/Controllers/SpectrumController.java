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

    public void testChart(double[] values, ComplexNumber[] complexNumbers, ComplexNumber[] reverseComplexNumbers) {


        XYChart.Series signal = new XYChart.Series();
        XYChart.Series recSignal = new XYChart.Series();
        XYChart.Series im = new XYChart.Series();
        XYChart.Series re = new XYChart.Series();
        XYChart.Series centSpetrum = new XYChart.Series();

        for (int i = 0; i < 1024; i++) {
            signal.getData().add(new XYChart.Data(i, values[i] * ComplexNumber.step(i)));

            re.getData().add(new XYChart.Data(i, complexNumbers[i].getRe()));
            im.getData().add(new XYChart.Data(i, complexNumbers[i].getIm()));

            double result = Math.sqrt(Math.pow(complexNumbers[i].getRe(), 2) + Math.pow(complexNumbers[i].getIm(), 2)) / values.length;
            centSpetrum.getData().add(new XYChart.Data(i, result));

            result = (reverseComplexNumbers[i].getRe() + reverseComplexNumbers[i].getIm()) / values.length * ComplexNumber.step(i);
            recSignal.getData().add(new XYChart.Data(i, result));
        }

        signalChart.getData().add(signal);
        recSignalChart.getData().add(recSignal);
        ImChart.getData().add(im);
        ReChart.getData().add(re);
        centSpetrChart.getData().add(centSpetrum);
    }
}
