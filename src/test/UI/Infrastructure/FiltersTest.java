package UI.Infrastructure;

import UI.Controllers.ResultsController;
import UI.Controllers.SpectrumController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;

class FiltersTest {

    @Test
    void lowPassBatterword() {
    }

    @Test
    void notch() {
    }

    @Test
    void discreteFourierTransform() throws IOException {

        //arrange
        double[] signal = new double[1024];

        for (int i = 0; i <= 20; i++)
            signal[i] = 1;

        for (int i = 0; i <= 20; i++)
            signal[i] = signal[i] * ComplexNumber.step(i);

        //act
        ComplexNumber[] complexNumbers = Filters.discreteFourierTransform(signal, signal.length, signal.length);

        ComplexNumber[] complexNumbersRecerse = Filters.reverseDiscreteFourierTransform(complexNumbers, complexNumbers.length, complexNumbers.length);

        //assert
        for (ComplexNumber complexNumber : complexNumbersRecerse) {
            System.out.println(complexNumber.getRe());
            System.out.println(complexNumber.getIm());
            System.out.println();
        }

    }

    @Test
    void reverseDiscreteFourierTransform() {
    }
}