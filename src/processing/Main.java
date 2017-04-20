package processing;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import tools.Tools;

public class Main {


    public static void main(String[] args) {


        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);


        Processing p = new Processing();
        Mat fft = p.fourierTransform("pictures/image_scan.png");
        Mat image = p.inverseFourierTransform();


        Imgproc.threshold(image, image, 200, 255, Imgproc.THRESH_BINARY);
        Tools.showResult(image);
    }
}