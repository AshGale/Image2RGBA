package main;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.PixelGrabber;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.NumberFormatter;

/**
 * 
 * @author Ash ProtoType - need to tidy up and optimise
 */

class Image2RGB {
	private JFrame jFrame;
	private JMenu jmenu;
	private JMenuBar jbar;
	private JMenuItem jOpen, jExit, jRGB, jScale;
	private JPanel jpanel;
	private File imageFile = null;
	private JLabel jlabelImage;
	private BufferedImage image;
	private String path = System.getProperty("user.home") + "\\Pictures";
	private Font font = new Font("Arial", Font.PLAIN, 18);

	Image2RGB() {
		jFrame = new JFrame("Convert Image to RGB Files");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLocationByPlatform(true);
		jFrame.setLayout(new GridBagLayout());
		jFrame.setMinimumSize(new Dimension(640, 640));

		GridBagConstraints gridBag = new GridBagConstraints();
		jpanel = new JPanel();
		jpanel.setLayout(new BorderLayout());
		jlabelImage = new JLabel(" ");
		jpanel.add(jlabelImage, BorderLayout.CENTER);
		// jpanel.setPreferredSize(new Dimension(400,400));

		gridBag.anchor = GridBagConstraints.PAGE_START;
		gridBag.fill = GridBagConstraints.HORIZONTAL;
		gridBag.gridx = gridBag.gridy = 0;
		gridBag.gridwidth = 2;
		// c.weightx=0.1;
		gridBag.weighty = 0.1;
		gridBag.ipady = 0;
		gridBag.insets = new Insets(5, 5, 10, 5);
		// jpanel.setBackground(Color.BLACK);//no suitable for images with transparency
		jFrame.add(jpanel, gridBag);

		// Creating Menu
		jbar = new JMenuBar();
		jmenu = new JMenu("File");

		jmenu.setFont(font);

		jOpen = new JMenuItem("Open");
		jOpen.setFont(font);
		jOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser fc = new JFileChooser(path);
				int result = fc.showOpenDialog(null);

				if (result == JFileChooser.APPROVE_OPTION) {
					imageFile = fc.getSelectedFile();
					try {
						jlabelImage.setIcon(new ImageIcon(ImageIO.read(imageFile)));
						image = ImageIO.read(imageFile);
						jFrame.pack();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});

		// -----------------------------------------------------------------
		jRGB = new JMenuItem("RGB");
		jRGB.setFont(font);
		jRGB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				// If Image selected already
				if (image == null) {
					jOpen.doClick();
					jRGB.doClick();
				} else {

					// TODO make jar or class to be reused
					// requires getIntFromColor function
					// pass in (String image-file)(String outputFolder)(boolean
					// file-replace-or-withTimeStamp)
					// TODO support JPEG and non alpha file formats

					File imageProcessing = new File(path + "\\ImageProcessing\\"); // in Windows

					if (!imageProcessing.exists()) {
						imageProcessing.mkdir();
					}

					File redImg = new File(path + "\\ImageProcessing\\_red.png"); // in Windows
					File greenImg = new File(path + "\\ImageProcessing\\_green.png"); // in Windows
					File blueImg = new File(path + "\\ImageProcessing\\_blue.png"); // in Windows

					// deal with file IO is exists ect
					if (redImg.exists() || greenImg.exists() || blueImg.exists()) {
						int inputOption = JOptionPane.showConfirmDialog(null, "Replace RGB files?",
								"RGB Files Already Exist!", JOptionPane.YES_NO_OPTION);

						if (inputOption == 0) {// Yes
							if (redImg.exists()) {
								redImg.delete();
							}
							if (greenImg.exists()) {
								greenImg.delete();
							}
							if (blueImg.exists()) {
								blueImg.delete();
							}
						} else if (inputOption == 1) {// No

							redImg = new File(path + "\\ImageProcessing\\" + System.currentTimeMillis() + "_red.png");
							greenImg = new File(
									path + "\\ImageProcessing\\" + System.currentTimeMillis() + "_green.png");
							blueImg = new File(path + "\\ImageProcessing\\" + System.currentTimeMillis() + "_blue.png");
						} // end import option

					} // end if files exist

					// get RGB from image
					BufferedImage buffImage = new BufferedImage(image.getWidth(), image.getHeight(),
							BufferedImage.TYPE_INT_ARGB);

					try {
						buffImage = ImageIO.read(imageFile);
					} catch (IOException e) {
					}

					int imageHeight = buffImage.getHeight();
					int imageWidth = buffImage.getWidth();

					BufferedImage buffRed = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
					BufferedImage buffGreen = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
					BufferedImage buffBlue = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

					WritableRaster imageRaster = buffImage.getRaster();
					int[] pixelRGBA = new int[4];

					for (int i = 0; i < imageHeight; i++) {
						for (int j = 0; j < imageWidth; j++) {

							imageRaster.getPixel(j, i, pixelRGBA);

							// System.out.println(j+","+i+" =>"+pixelRGBA[0]+" "+pixelRGBA[1]+"
							// "+pixelRGBA[2]+" "+pixelRGBA[3]);

							if (pixelRGBA[0] == 0 && pixelRGBA[1] == 0 && pixelRGBA[2] == 0 && pixelRGBA[3] == 0) {

								// set pixel at location to empty
								buffRed.setRGB(j, i, getIntFromColor(0, 0, 0, 0));
								buffGreen.setRGB(j, i, getIntFromColor(0, 0, 0, 0));
								buffBlue.setRGB(j, i, getIntFromColor(0, 0, 0, 0));
							} else if (pixelRGBA[3] == 0) {// check if file supports alpha
								// RED
								buffRed.setRGB(j, i, getIntFromColor(pixelRGBA[0], 0, 0, 255));

								// GREEN
								buffGreen.setRGB(j, i, getIntFromColor(0, pixelRGBA[1], 0, 255));

								// BLUE
								buffBlue.setRGB(j, i, getIntFromColor(0, 0, pixelRGBA[2], 255));
							} else {
								// RED
								buffRed.setRGB(j, i, getIntFromColor(pixelRGBA[0], 0, 0, pixelRGBA[3]));

								// GREEN
								buffGreen.setRGB(j, i, getIntFromColor(0, pixelRGBA[1], 0, pixelRGBA[3]));

								// BLUE
								buffBlue.setRGB(j, i, getIntFromColor(0, 0, pixelRGBA[2], pixelRGBA[3]));
							}
						}
					}

					// Save Images to path of selected file
					try {
						ImageIO.write(buffRed, "png", redImg);
						ImageIO.write(buffGreen, "png", greenImg);
						ImageIO.write(buffBlue, "png", blueImg);
					} catch (IOException e) {

						JOptionPane.showMessageDialog(null, "Failed to create RGB files");
						e.printStackTrace();
					}

					JOptionPane.showMessageDialog(null, "All Done");
					try {
						Runtime.getRuntime().exec("explorer " + path + "\\ImageProcessing");
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}// end action performed
		});
		// -----------------------------------------------------------------

		jScale = new JMenuItem("Scale");
		jScale.setFont(font);
		jScale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				// If Image selected already
				if (image == null) {
					jOpen.doClick();
					jScale.doClick();
				} else {

					int outputWidth = 0;
					int outputHeight = 0;

					if (outputHeight == 0 || outputWidth == 0) {

						// Get Desired Width and Height
						NumberFormat format = NumberFormat.getInstance();
						format.setGroupingUsed(false);
						NumberFormatter formatter = new NumberFormatter(format);
						formatter.setValueClass(Integer.class);
						formatter.setMaximum(65535);
						formatter.setAllowsInvalid(false);
						formatter.setCommitsOnValidEdit(true);

						JTextField newWidth = new JFormattedTextField(formatter);
						JTextField newHeight = new JFormattedTextField(formatter);
						final JComponent[] inputs = new JComponent[] { new JLabel("Please enter in number format!"),
								new JLabel("new Width"), newWidth, new JLabel("new Height"), newHeight,
								new JLabel("current size: " + image.getWidth() + " x " + image.getHeight()) };
						int result = JOptionPane.showConfirmDialog(null, inputs, "Output Image size",
								JOptionPane.PLAIN_MESSAGE);
						if (result == JOptionPane.OK_OPTION) {
							System.out.println(
									"Output image dimentions: " + newWidth.getText() + " x " + newHeight.getText());
							outputWidth = Integer.parseInt(newWidth.getText());// ensured number format with formatter
							outputHeight = Integer.parseInt(newHeight.getText());// ensured number format with formatter
						} else {
							System.out.println("User canceled / closed the dialog, result = " + result);
						}

					} // end if output dimensions 0

					// make file structure
					File imageProcessing = new File(path + "\\ImageProcessing"); // in Windows
					File scaled = new File(path + "\\ImageProcessing\\Scaled"); // in Windows

					if (!imageProcessing.exists()) {
						imageProcessing.mkdir();
					}
					if (!scaled.exists()) {
						scaled.mkdir();
					}
					
					//Make file and determine file name
					String fileName = imageFile.getName();
					if (fileName.contains("."))
						fileName = fileName.substring(0, fileName.indexOf("."));
					File outputfile = new File(path + "\\ImageProcessing\\Scaled\\" + fileName + "_scaled_"
							+ outputWidth + "x" + outputHeight + ".png");

					// convert image to scaled version
			        BufferedImage buffInputImage = null;
					try {
						buffInputImage = ImageIO.read(imageFile);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			 
			        BufferedImage buffOutputImage = new BufferedImage(outputWidth, outputHeight,BufferedImage.TYPE_INT_ARGB);
			 
			        // scales the input image to the output image
			        Graphics2D g2d = buffOutputImage.createGraphics();
			        g2d.drawImage(buffInputImage, 0, 0, outputWidth, outputHeight, null);
			        g2d.dispose();			       

					try {
						ImageIO.write(buffOutputImage, "png", outputfile);
					} catch (IOException e) {

						JOptionPane.showMessageDialog(null, "Failed to create RGB files");
						e.printStackTrace();
					}

					JOptionPane.showMessageDialog(null, "All Done");
					try {
						Runtime.getRuntime().exec("explorer " + path + "\\ImageProcessing\\Scaled");
					} catch (IOException e) {
						e.printStackTrace();
					}

				} // end if image null
			}
		});
		// -----------------------------------------------------------------

		jExit = new JMenuItem("Exit");
		jExit.setFont(font);
		jExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.exit(0);
			}
		});

		jmenu.add(jOpen);
		jmenu.add(jRGB);
		jmenu.add(jScale);
		jmenu.add(jExit);
		jbar.add(jmenu);
		jFrame.setJMenuBar(jbar);

		jFrame.pack();// this processes the render
		jFrame.setResizable(true);
		jFrame.setVisible(true);
	}

	public int getIntFromColor(int R, int G, int B, int A) {

		A = (A << 24) & 0xFF000000;
		R = (R << 16) & 0x00FF0000;
		G = (G << 8) & 0x0000FF00;
		B = B & 0x000000FF;

		return A | R | G | B;
	}

	// Main Function
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Image2RGB();
			}
		});
	}
}