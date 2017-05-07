package org.flexigraph.processing;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import org.flexigraph.tools.Tools;

public class Main {

	public static void main(String[] args) {

		nu.pattern.OpenCV.loadLibrary();

		Processing p = new Processing();
		List<Mat> sources = new ArrayList<>();
		Mat original = Highgui.imread("pictures/image_scan.png", Highgui.CV_LOAD_IMAGE_COLOR);

		// original = Perspective.correctPerspective();

		Core.split(original, sources);

		List<Mat> results = new ArrayList<>();
		for (Mat m : sources) {

			m.convertTo(m, CvType.CV_32FC1);
			Mat fft = p.fourierTransform(m);

			Mat img = p.inverseFourierTransform();
			results.add(img);
		}

		Mat image = new Mat();

		for (Mat m : results) {
			Imgproc.threshold(m, m, 200, 255, Imgproc.THRESH_BINARY);
			Tools.showResult(m);
		}

		Core.merge(results, image);

		Tools.showResult(image);
		Imgproc.threshold(image, image, 190, 255, Imgproc.THRESH_BINARY);
		// Imgproc.threshold(image, image, 115, 255, Imgproc.THRESH_BINARY);
		// Tools.showResult(image);
	}
}