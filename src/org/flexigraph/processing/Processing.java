package org.flexigraph.processing;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

/**
 * Created by loris on 4/20/17.
 */
public class Processing {

	List<Mat> planes = new ArrayList();
	Mat complexImage = new Mat();

	public Mat fourierTransform(Mat image) {

		planes = new ArrayList();
		complexImage = new Mat();

		Mat padded = new Mat(); // expand input image to optimal size
		int addPixelRows = Core.getOptimalDFTSize(image.rows());
		int addPixelCols = Core.getOptimalDFTSize(image.cols()); // on the
		// border
		// add zero
		// values
		
		Imgproc.copyMakeBorder(image, padded, (addPixelRows - image.height()) / 2, (addPixelRows - image.height()) / 2,
				(addPixelCols - image.width()) / 2, (addPixelCols - image.width()) / 2, Imgproc.BORDER_CONSTANT,
				Scalar.all(0));

		padded.convertTo(padded, CvType.CV_32F);
		planes.add(padded);
		planes.add(Mat.zeros(padded.size(), CvType.CV_32F));
		Core.merge(planes, complexImage);
		Core.dft(complexImage, complexImage);
		Core.split(complexImage, planes);
		Mat mag = new Mat();
		Core.magnitude(planes.get(0), planes.get(1), mag);

		Core.add(Mat.ones(mag.size(), CvType.CV_32F), mag, mag);
		Core.log(mag, mag);

		padded = padded.submat(new Rect(0, 0, mag.cols(), mag.rows()));
		int cx = padded.cols() / 2;
		int cy = padded.rows() / 2;

		Mat q0 = new Mat(mag, new Rect(0, 0, cx, cy));
		Mat q1 = new Mat(mag, new Rect(cx, 0, cx, cy));
		Mat q2 = new Mat(mag, new Rect(0, cy, cx, cy));
		Mat q3 = new Mat(mag, new Rect(cx, cy, cx, cy));

		Mat tmp = new Mat();
		q0.copyTo(tmp);
		q3.copyTo(q0);
		tmp.copyTo(q3);

		q1.copyTo(tmp);
		q2.copyTo(q1);
		tmp.copyTo(q2);

		Core.normalize(mag, mag, 0, 255, Core.NORM_MINMAX);

		return mag;
	}

	public Mat inverseFourierTransform() {

		Scalar val = new Scalar(0, 0);
		Mat mask = Mat.zeros(complexImage.size(), CvType.CV_8U);

		int centerX = complexImage.width() / 2;
		int centerY = complexImage.height() / 2;

		// Dessiner le masque
		// L'image est coupée en 4 le milleu se retrouve sur les bords

		int marge = 0;
		int largeur = 3;
		// Hauteur

		for (int i = marge; i < complexImage.height() - marge; i++) {

			for (int j = 0; j < largeur; j++) {
				mask.put(i, j, 255);
				mask.put(i, complexImage.width() - j, 255);
			}
		}

		// Largeur

		for (int i = marge; i < complexImage.width() - marge; i++) {

			for (int j = 0; j < largeur; j++) {
				mask.put(j, i, 255);
				mask.put(complexImage.height() - j, i, 255);
			}
		}

		complexImage.setTo(val, mask);

		Core.idft(complexImage, complexImage);
		Mat restoredImage = new Mat();
		Core.split(complexImage, planes);
		Core.normalize(planes.get(0), restoredImage, 0, 255, Core.NORM_MINMAX);

		return restoredImage;
	}
}
