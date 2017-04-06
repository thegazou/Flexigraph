import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Example {
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat image = Imgcodecs.imread("pictures/good_picture.JPG");
		showResult(image);
		fourierTransform("pictures/good_picture.JPG");
	}

	public static void showResult(Mat img) {
		// Imgproc.resize(img, img, new Size(640, 480));
		MatOfByte matOfByte = new MatOfByte();
		Imgcodecs.imencode(".jpg", img, matOfByte);
		byte[] byteArray = matOfByte.toArray();
		BufferedImage bufImage = null;
		try {
			InputStream in = new ByteArrayInputStream(byteArray);
			bufImage = ImageIO.read(in);
			JFrame frame = new JFrame();
			frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
			frame.pack();
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int fourierTransform(String img) {
		Mat complexImage = new Mat();

		Mat image = Imgcodecs.imread(img, 0);
		if (image.empty())
			return -1;

		Mat padded = new Mat(); // expand input image to optimal size
		int addPixelRows = Core.getOptimalDFTSize(image.rows());
		int addPixelCols = Core.getOptimalDFTSize(image.cols()); // on the
																	// border
																	// add zero
																	// values
		Core.copyMakeBorder(image, padded, 0, addPixelRows - image.rows(), 0, addPixelCols - image.cols(),
				Core.BORDER_CONSTANT, Scalar.all(0));

		padded.convertTo(padded, CvType.CV_32F);
		List<Mat> planes = new ArrayList();
		planes.add(padded);
		planes.add(Mat.zeros(padded.size(), CvType.CV_32F));

		Core.merge(planes, complexImage);
		Core.dft(complexImage, complexImage);
		Core.split(complexImage, planes);
		Mat mag = new Mat();
		Core.magnitude(planes.get(0), planes.get(1), mag);

		Core.add(Mat.ones(mag.size(), CvType.CV_32F), mag, mag);
		Core.log(mag, mag);

		System.out.println("imWidth: " + image.size().width);
		System.out.println("imHeight: " + image.size().height);

		System.out.println("paddedWith: " + padded.size().width);
		System.out.println("Col: " + (mag.cols() - 4));
		System.out.println("Raw: " + (mag.rows() - 55));

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
		showResult(mag);
		return 0;
	}
}