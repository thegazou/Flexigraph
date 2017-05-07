package org.flexigraph;
import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;

import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JSlider;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import org.flexigraph.pre_processing.Perspective;
import org.flexigraph.processing.Processing;
import org.flexigraph.tools.Tools;

import java.awt.BorderLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MainUI {

	private JFrame frame;
	private BufferedImage originalImg;
	private BufferedImage preprocessedImg;
	private BufferedImage processedImg;
	private JButton btnOpen;
	private JButton btnProcess;
	private JButton btnSave;
	private JSlider sldThresholdValue;
	private JLabel lblPreprocessed;
	private JLabel lblOriginal;
	private JLabel lblProcessed;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		nu.pattern.OpenCV.loadLibrary();
		
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainUI window = new MainUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 629, 479);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel panel = new JPanel();
		frame.getContentPane().add(panel, BorderLayout.NORTH);
		panel.setLayout(new GridLayout(0, 3, 0, 0));
		
		btnOpen = new JButton("Open");
		panel.add(btnOpen);
		
		btnProcess = new JButton("Process");
		panel.add(btnProcess);
		
		btnSave = new JButton("Save");
		panel.add(btnSave);
		
		JPanel panel_1 = new JPanel();
		frame.getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new GridLayout(0, 3, 0, 0));
		
		lblOriginal = new JLabel();
		panel_1.add(lblOriginal);
		
		lblPreprocessed = new JLabel();
		panel_1.add(lblPreprocessed);
		
		JPanel panel_2 = new JPanel();
		panel_1.add(panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		
		sldThresholdValue = new JSlider();
		sldThresholdValue.setValue(180);
		sldThresholdValue.setMaximum(255);
		panel_2.add(sldThresholdValue, BorderLayout.NORTH);
		
		lblProcessed = new JLabel("");
		panel_2.add(lblProcessed, BorderLayout.CENTER);
		
		// Listeners
		// Buttons
		btnOpen.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(frame);

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					try {
						File file = fc.getSelectedFile();
						originalImg = ImageIO.read(file);
						preprocessedImg = null;
						processedImg = null;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		
		btnProcess.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				Processing p = new Processing();

				List<Mat> sources = new ArrayList<>();

				Mat original = Tools.bufferedImageToMat(originalImg);

				// -------------
				// PREPROCESSING
				// -------------
				if (preprocessedImg == null)
				{
					original = Perspective.correctPerspective(original, false);
					preprocessedImg = Tools.matToBufferedImage(original);
					lblPreprocessed.setIcon(new ImageIcon(Tools.resize(preprocessedImg, lblPreprocessed.getWidth(), lblPreprocessed.getHeight())));
				}

				// -------------
				// PROCESSING
				// -------------

				Core.split(original, sources);

				List<Mat> results = new ArrayList<>();
				for (Mat m : sources) {

					// m.convertTo(m, CvType.CV_32FC1);
					p.fourierTransform(m);
					Mat img = p.inverseFourierTransform();
					results.add(img);
				}

				Mat image = new Mat();

				for (Mat m : results) {
					Imgproc.threshold(m, m, sldThresholdValue.getValue(), 255, Imgproc.THRESH_BINARY);
				}

				Core.merge(results, image);

				Imgproc.threshold(image, image, sldThresholdValue.getValue(), 255, Imgproc.THRESH_BINARY);

				// show in label
                processedImg = Tools.matToBufferedImage(image);
				lblProcessed.setIcon(new ImageIcon(Tools.resize(processedImg, lblProcessed.getWidth(), lblProcessed.getHeight())));

			}
		});
		
		btnSave.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				JFileChooser fc = new JFileChooser();
				int returnVal = fc.showSaveDialog(frame);

				if (returnVal == JFileChooser.APPROVE_OPTION) {

					File outputFile = fc.getSelectedFile();
					try {
						ImageIO.write(processedImg, "jpg", outputFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		
		// Labels

		lblOriginal.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {

				lblOriginal.setIcon(new ImageIcon(Tools.resize(originalImg, lblOriginal.getWidth(), lblOriginal.getHeight())));
			}
		});

		lblPreprocessed.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {

				lblPreprocessed.setIcon(new ImageIcon(Tools.resize(preprocessedImg, lblPreprocessed.getWidth(), lblPreprocessed.getHeight())));
			}
		});
		
		lblProcessed.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {

				lblProcessed.setIcon(new ImageIcon(Tools.resize(processedImg, lblProcessed.getWidth(), lblProcessed.getHeight())));
			}
		});
		
	}
}
