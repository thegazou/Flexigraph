package tools;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Tools {

	public static void showResult(Mat img) {
		//Imgproc.resize(img, img, new Size(img.width()/2, img.height() / 2));
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

	public static BufferedImage matToBufferedImage(Mat img) {
		MatOfByte matOfByte = new MatOfByte();
		Imgcodecs.imencode(".jpg", img, matOfByte);
		byte[] byteArray = matOfByte.toArray();
		BufferedImage bufImage = null;
		try {
			InputStream in = new ByteArrayInputStream(byteArray);
			bufImage = ImageIO.read(in);
			return bufImage;
		} catch (Exception e) {
			e.printStackTrace();
		}
			return null;
	}

	public static Mat bufferedImageToMat(BufferedImage image) {
		// REMOVE ALPHA LAYER
		BufferedImage convertedImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
		convertedImg.getGraphics().drawImage(image, 0, 0, null);
		convertedImg.getGraphics().dispose();

		byte[] data = ((DataBufferByte) convertedImg.getRaster().getDataBuffer()).getData();
		Mat mat = new Mat(convertedImg.getHeight(), convertedImg.getWidth(), CvType.CV_8UC3);
		mat.put(0, 0, data);
		return mat;
	}
}
