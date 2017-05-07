package org.flexigraph.pre_processing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.opencv.core.Core;
import org.opencv.core.Mat;

import org.flexigraph.tools.Tools;

public class UsePerspective {

	public static void main(String[] args) {
		
		nu.pattern.OpenCV.loadLibrary();

		
		String fileName = "pictures/image4.JPG";
		File file = new File(fileName);

		BufferedImage img;
		try {
			img = ImageIO.read(file);
			Mat imgSource = Tools.bufferedImageToMat(img);
			Perspective.correctPerspective(imgSource, true);
		} catch (IOException e) {
			System.err.println("Erreur � la lecture du fichier: " + fileName + "!");
		}

	}
}
