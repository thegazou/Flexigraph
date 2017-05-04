import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import processing.Processing;
import tools.Tools;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by loris on 5/3/17.
 */
public class GuiForm extends JPanel{
    private JButton btnOpenFile;
    private JLabel lblOriginal;
    private JLabel lblPreprocessed;
    private JLabel lblProcessed;
    private JButton btnProcess;
    private JButton btnSave;
    private JPanel main;
    private JSlider sldThresholdValue;
    private BufferedImage img;

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        JFrame frame = new JFrame("GuiForm");
        frame.setContentPane(new GuiForm().main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public GuiForm() {
        // Load file
        btnOpenFile.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

                JFileChooser fc = new JFileChooser();
                int returnVal = fc.showOpenDialog(main);


                if (returnVal == JFileChooser.APPROVE_OPTION) {

                    try {
                        File file = fc.getSelectedFile();
                        img = ImageIO.read(file);
                        lblOriginal.setIcon(new ImageIcon(img));
                        btnProcess.setEnabled(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        // Process file
        btnProcess.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);

                if(!btnProcess.isEnabled())
                    return;

                Processing p = new Processing();

                List<Mat> sources = new ArrayList<>();

                Mat original = Tools.bufferedImageToMat(img);

                // -------------
                // PREPROCESSING
                // -------------

                // todo Faire et afficher le resultat dans lblPreprocessed

                // -------------
                // PROCESSING
                // -------------

                Core.split(original, sources);

                List<Mat> results = new ArrayList<>();
                for (Mat m : sources) {

                    //m.convertTo(m, CvType.CV_32FC1);
                    p.fourierTransform(m);
                    Mat img = p.inverseFourierTransform();
                    results.add(img);
                }

                Mat image = new Mat();

                for (Mat m : results)
                {
                    Imgproc.threshold(m, m, sldThresholdValue.getValue(), 255, Imgproc.THRESH_BINARY);
                }

                Core.merge(results, image);

                Imgproc.threshold(image, image, sldThresholdValue.getValue(), 255, Imgproc.THRESH_BINARY);

                // show in label
                lblProcessed.setIcon(new ImageIcon(Tools.matToBufferedImage(image)));

                // Activate save button
                btnSave.setEnabled(true);
            }
        });

        btnSave.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                super.mouseClicked(mouseEvent);
                // todo save the processed file

                if(!btnSave.isEnabled())
                    return;
            }
        });


        // todo slider onchange event do click on btnprocess
    }
}
