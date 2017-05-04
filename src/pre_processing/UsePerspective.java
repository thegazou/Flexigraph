package pre_processing;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import tools.Tools;

public class UsePerspective {

	public static void main(String[] args) {
		Mat result = PerspectiveDemo.correctPerspective();
		Tools.rotate_90n(result, result, false);
		Tools.showResult(result, 4);
		Imgcodecs.imwrite("pictures/correctedPicture/corrected.jpg", result);
	}
}
