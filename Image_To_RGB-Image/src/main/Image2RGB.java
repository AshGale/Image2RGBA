package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
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
	private JMenuItem jOpen, jExit, jRGB, jScale, jGreyScale;
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

				// Ensure File structure set up
				setupfileStructure();

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

					// set up output files
					File redImg = generateFile("Rgb", "red");
					File greenImg = generateFile("Rgb", "green");
					File blueImg = generateFile("Rgb", "blue");

					// get RGB from image
					BufferedImage buffImage = getMainImageBuffered();

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

							if (pixelRGBA[3] == 0 && pixelRGBA[0] == 0 && pixelRGBA[1] == 0 && pixelRGBA[2] == 0) {

								// set pixel at location to empty
								buffRed.setRGB(j, i, getIntFromColor(0, 0, 0, 0));
								buffGreen.setRGB(j, i, getIntFromColor(0, 0, 0, 0));
								buffBlue.setRGB(j, i, getIntFromColor(0, 0, 0, 0));
							} else if (pixelRGBA[3] == 0) {// check if file supports alpha

								buffRed.setRGB(j, i, getIntFromColor(pixelRGBA[0], 0, 0, 255));
								buffGreen.setRGB(j, i, getIntFromColor(0, pixelRGBA[1], 0, 255));
								buffBlue.setRGB(j, i, getIntFromColor(0, 0, pixelRGBA[2], 255));
							} else {

								buffRed.setRGB(j, i, getIntFromColor(pixelRGBA[0], 0, 0, pixelRGBA[3]));
								buffGreen.setRGB(j, i, getIntFromColor(0, pixelRGBA[1], 0, pixelRGBA[3]));
								buffBlue.setRGB(j, i, getIntFromColor(0, 0, pixelRGBA[2], pixelRGBA[3]));
							}
						}
					} // end nested loop

					// Save Images to path of selected file
					savImageFile(buffRed, "png", redImg);
					savImageFile(buffGreen, "png", greenImg);
					savImageFile(buffBlue, "png", blueImg);

					JOptionPane.showMessageDialog(null, "All Done");

					openNewFileLocation("Rgb");

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

							outputWidth = Integer.parseInt(newWidth.getText());// ensured number format with formatter
							outputHeight = Integer.parseInt(newHeight.getText());// ensured number format with formatter
						} else {
							System.out.println("User canceled / closed the dialog, result = " + result);
						}

					} // end if output dimensions 0

					// Make file and determine file name
					File outputfile = generateFile("Scaled", "scaled_" + outputWidth + "x" + outputHeight);

					// convert image to scaled version
					BufferedImage buffInputImage = getMainImageBuffered();

					BufferedImage buffOutputImage = new BufferedImage(outputWidth, outputHeight,
							BufferedImage.TYPE_INT_ARGB);

					// scales the input image to the output image
					Graphics2D g2d = buffOutputImage.createGraphics();
					g2d.drawImage(buffInputImage, 0, 0, outputWidth, outputHeight, null);
					g2d.dispose();

					savImageFile(buffOutputImage, "png", outputfile);

					JOptionPane.showMessageDialog(null, "All Done");

					openNewFileLocation("Scaled");

				} // end if image null
			}
		});
		// -----------------------------------------------------------------

		jGreyScale = new JMenuItem("Grey Scale");
		jGreyScale.setFont(font);
		jGreyScale.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				// If Image selected already
				if (image == null) {
					jOpen.doClick();
					jGreyScale.doClick();
				} else {

					File greyScaleImage = generateFile("Grey_Scale", "Greyscale");

					// convert image to Greyscal version
					BufferedImage buffInputImage = getMainImageBuffered();

					BufferedImage buffOutputImage = new BufferedImage(image.getWidth(), image.getHeight(),
							BufferedImage.TYPE_BYTE_GRAY);

					Graphics g = buffOutputImage.getGraphics();
					g.drawImage(buffInputImage, 0, 0, null);
					g.dispose();

					savImageFile(buffOutputImage, "png", greyScaleImage);

					JOptionPane.showMessageDialog(null, "All Done");

					openNewFileLocation("Grey_Scale");
				}
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
		jmenu.add(jGreyScale);
		jmenu.add(jScale);
		jmenu.add(jExit);
		jbar.add(jmenu);
		jFrame.setJMenuBar(jbar);

		jFrame.pack();// this processes the render
		jFrame.setResizable(true);
		jFrame.setVisible(true);
	}

	protected void openNewFileLocation(String fileFolderLocation) {
		try {
			Runtime.getRuntime().exec("explorer " + path + "\\ImageProcessing\\" + fileFolderLocation);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	protected void savImageFile(BufferedImage bufferReader, String fileFormat, File file) {
		try {
			ImageIO.write(bufferReader, "png", file);
		} catch (IOException e) {

			JOptionPane.showMessageDialog(null, "Failed to create RGB files");
			e.printStackTrace();
		}

	}

	private BufferedImage getMainImageBuffered() {

		BufferedImage buffImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		try {
			buffImage = ImageIO.read(imageFile);
		} catch (IOException e) {
		}
		return buffImage;
	}

	private File generateFile(String folderPrefix, String fileName) {
		String name = imageFile.getName();
		if (name.contains("."))
			name = name.substring(0, name.indexOf("."));
		name += name + "_";

		return new File(path + "\\ImageProcessing\\" + folderPrefix + "\\" + name + fileName + ".png");

	}

	private void setupfileStructure() {

		File imageProcessing = new File(path + "\\ImageProcessing");
		File rgb = new File(path + "\\ImageProcessing\\Rgb");
		File scaled = new File(path + "\\ImageProcessing\\Scaled");
		File greyScale = new File(path + "\\ImageProcessing\\Grey_Scale");

		if (!imageProcessing.exists()) {
			imageProcessing.mkdir();
		}
		if (!rgb.exists()) {
			rgb.mkdir();
		}
		if (!greyScale.exists()) {
			greyScale.mkdir();
		}
		if (!scaled.exists()) {
			scaled.mkdir();
		}

	}

	private int getIntFromColor(int R, int G, int B, int A) {

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