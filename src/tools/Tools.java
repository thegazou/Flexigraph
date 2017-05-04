package tools;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Tools {

	public static void showResult(Mat img) {
		showResult(img, 2);
	}

	public static void showResult(Mat img, int zoomfactor) {
		Mat dst = new Mat();
		Imgproc.resize(img, dst, new Size(img.width() / zoomfactor, img.height() / zoomfactor));
		MatOfByte matOfByte = new MatOfByte();
		Imgcodecs.imencode(".jpg", dst, matOfByte);
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

	public static void rotate_90n(Mat src, Mat dst, boolean isClockwise) {
		if (isClockwise) {
			Core.transpose(src, dst);
			Core.flip(dst, dst, 1);
		} else {
			Core.transpose(src, dst);
			Core.flip(dst, dst, 0);
		}
	}
}
