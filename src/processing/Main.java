package processing;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import static tools.Tools.showResult;

public class Main {
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat image = Imgcodecs.imread("pictures/good_picture.JPG");

		// EXACTEMENT CE QUE L'ON VEUT
        // http://stackoverflow.com/questions/39580656/opencv-c-remove-grid-from-captcha

		showResult(image);
	}


}