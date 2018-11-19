import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * 
 * @author Ash ProtoType - need to tidy up and optimise
 */

class Image2RGB {
	private JFrame jFrame;
	private JMenu jmenu;
	private JMenuBar jbar;
	private JMenuItem jOpen, jExit, jRGB;
	private JPanel jpanel;
	private File imageFile = null;
	private JLabel jlabelImage;
	private BufferedImage image;
	private String path = "C:\\Users\\Ash\\Pictures";
	private Font font = new Font("Arial", Font.PLAIN, 18);

	Image2RGB() {
		jFrame = new JFrame("Convert Image to RGB Files");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// j.setExtendedState(Frame.MAXIMIZED_BOTH);
		// j.setLocationRelativeTo(null);
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

				File redImg = new File(path + "\\_red.png"); // in Windows
				File greenImg = new File(path + "\\_green.png"); // in Windows
				File blueImg = new File(path + "\\_blue.png"); // in Windows

				// If Image selected already
				if (image == null) {
					jOpen.doClick();
					jRGB.doClick();
				} else {

					// deal with file IO is exists ect
					if (redImg.exists() || greenImg.exists() || blueImg.exists()) {
						int inputOption = JOptionPane.showConfirmDialog(null, "Replace RGB files?",
								"RGB Files Already Exist!", JOptionPane.YES_NO_OPTION);

						if (inputOption == 0) {//Yes
							if (redImg.exists()) {
								redImg.delete();
							}
							if (greenImg.exists()) {
								greenImg.delete();
							}
							if (blueImg.exists()) {
								blueImg.delete();
							}
						} else if (inputOption == 1){//No
							System.out.println("seclected value: " + inputOption);
							redImg = new File(path + "\\_red_" + System.currentTimeMillis() + ".png");
							greenImg = new File(path + "\\_green_" + System.currentTimeMillis()  + ".png");
							blueImg = new File(path + "\\_blue_" + System.currentTimeMillis()  + ".png");
						} // end import option
						
					} // end if files exist
					
					// get RGB from image
					BufferedImage buffImage = image;

					int imageHeight = buffImage.getHeight();
					int imageWidth = buffImage.getWidth();
					Color individualPixel = null;
					Color tempPixel = null;

					BufferedImage buffRed = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
					BufferedImage buffGreen = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
					BufferedImage buffBlue = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);

					for (int i = 0; i < imageHeight; i++) {
						for (int j = 0; j < imageWidth; j++) {

							individualPixel = new Color(buffImage.getRGB(j, i));
							
							//TODO find a way to detect a empty pixel an keep perfect black
							if(individualPixel.getRed() == 0 
									&& individualPixel.getGreen() == 0
									&& individualPixel.getBlue() ==0
									) {
								System.out.println("empty space");
								//set pixel at location to empty
								buffRed.setRGB(j, i, getIntFromColor(0, 0, 0, 0));
								buffGreen.setRGB(j, i, getIntFromColor(0, 0, 0, 0));
								buffBlue.setRGB(j, i, getIntFromColor(0, 0, 0, 0));
							}else {
								// RED
								tempPixel = new Color(individualPixel.getRed(), 0, 0, individualPixel.getAlpha());
								buffRed.setRGB(j, i, getIntFromColor(tempPixel.getRed(), 0, 0, tempPixel.getAlpha()));

								// GREEN
								tempPixel = new Color(0, individualPixel.getGreen(), 0, individualPixel.getAlpha());
								buffGreen.setRGB(j, i, getIntFromColor(0, tempPixel.getGreen(), 0, tempPixel.getAlpha()));

								// BLUE
								tempPixel = new Color(0, 0, individualPixel.getBlue(), individualPixel.getAlpha());
								buffBlue.setRGB(j, i, getIntFromColor(0, 0, tempPixel.getBlue(), tempPixel.getAlpha()));
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
						Runtime.getRuntime().exec("explorer " + path);
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			}// end action performed
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