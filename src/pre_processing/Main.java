package pre_processing;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import tools.Tools;

public class Main {

	public static void main(String[] args) {
		Mat result = Perspective.correctPerspective();
		Tools.showResult(result);
		Imgcodecs.imwrite("pictures/correctedPicture/corrected.jpg", result);
	}
}
