import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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
 * @author Ash
 * ProtoType - need to tidy up and optimise
 */

class Main {
	private JFrame jFrame;
	private JMenu jmenu;
	private JMenuBar jbar;
	private JMenuItem jOpen, jExit, jRGB;
	private JPanel jpanel, jpanelbar;
	// private JButton jpre, jnext;
	// private JOptionPane optionPane;
	private File imageFile = null;
	JLabel image;
	ImageIcon ic;
	Image img;
	String path = "C:\\Users\\Ash\\Pictures";

	Main() {
		jFrame = new JFrame("Image Viewer");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// j.setExtendedState(Frame.MAXIMIZED_BOTH);
		// j.setLocationRelativeTo(null);
		jFrame.setLocationByPlatform(true);
		jFrame.setLayout(new GridBagLayout());
		jFrame.setMinimumSize(new Dimension(400, 400));

		GridBagConstraints gridBag = new GridBagConstraints();
		jpanel = new JPanel();
		jpanel.setLayout(new BorderLayout());
		image = new JLabel(" ");
		jpanel.add(image, BorderLayout.CENTER);
		// jpanel.setPreferredSize(new Dimension(400,400));

		gridBag.anchor = GridBagConstraints.PAGE_START;
		gridBag.fill = GridBagConstraints.HORIZONTAL;
		gridBag.gridx = gridBag.gridy = 0;
		gridBag.gridwidth = 2;
		// c.weightx=0.1;
		gridBag.weighty = 0.1;
		gridBag.ipady = 0;
		gridBag.insets = new Insets(5, 5, 10, 5);
		// jpanel.setBackground(Color.BLACK);
		jFrame.add(jpanel, gridBag);

		// jpanelbar = new JPanel();
		// jpanelbar.setLayout(new GridBagLayout());
		// jpanelbar.setBackground(Color.red);

//        GridBagConstraints x = new GridBagConstraints();
//        jpre = new JButton("Previous");
//        x.gridx = 0;
//        x.gridy = 0;
//        x.gridwidth = 1;
//        x.weightx = 0.1;
//        // x.insets=new Insets(5,5,5,5);
//        // x.fill=GridBagConstraints.NONE;
//        jpanelbar.add(jpre, x);
//
//        jnext = new JButton("Next");
//        x.gridx = 1;
//        jpanelbar.add(jnext, x);

//        gridBag.weightx = 0.1;
//        gridBag.gridx = 0;
//        gridBag.gridy = 1;
//        gridBag.fill = GridBagConstraints.HORIZONTAL;
//        gridBag.insets = new Insets(5, 5, 5, 5);
//        gridBag.ipady = 150;
//        jFrame.add(jpanelbar, gridBag);

		// Creating Menu
		jbar = new JMenuBar();
		jmenu = new JMenu("File");
		jOpen = new JMenuItem("Open");
		jOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				JFileChooser fc = new JFileChooser(path);
				int result = fc.showOpenDialog(null);

				if (result == JFileChooser.APPROVE_OPTION) {
					imageFile = fc.getSelectedFile();
					try {
						image.setIcon(new ImageIcon(ImageIO.read(imageFile)));
						// jFrame.setSize(new Dimension(400,400));
						// jpanel.setSize(new Dimension(400,400));
						// jpanel.setPreferredSize(jpanel.getPreferredSize());
						jFrame.pack();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		jExit = new JMenuItem("Exit");
		jExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				System.exit(0);
			}
		});
		// -----------------------------------------------------------------
		jRGB = new JMenuItem("RGB");
		jRGB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				File redImg = new File(path + "\\_red.png"); // in Windows
				File greenImg = new File(path + "\\_green.png"); // in Windows
				File blueImg = new File(path + "\\_blue.png"); // in Windows

				// deal with file IO is exists ect

				if (redImg.exists() || greenImg.exists() || blueImg.exists()) {
					int inputOption = JOptionPane.showConfirmDialog(null, "Replace RGB files?", "YesNoCancel",
							JOptionPane.YES_NO_CANCEL_OPTION);
//            	    switch (inputOption) {
//            	        case 0:
//            	            JOptionPane.showConfirmDialog(null, "You pressed YES\n"+"Pressed value is = "+inputOption);
//            	            break;
//            	        case 1:
//            	            JOptionPane.showConfirmDialog(null, "You pressed NO\n"+"Pressed value is = "+inputOption);
//            	            break;
//            	        case 2:
//            	            JOptionPane.showConfirmDialog(null, "You pressed CANCEL\n"+"Pressed value is = "+inputOption);
//            	            break;
//            	        case -1:
//            	            JOptionPane.showConfirmDialog(null, "You pressed X\n"+"Pressed value is = "+inputOption);
//            	            break;
//            	        default:
//            	            break;
//            	    }

					if (inputOption == 1) {
						if (redImg.exists()) {
							redImg.delete();
						}
						//redImg.createNewFile();

						if (greenImg.exists()) {
							greenImg.delete();
						}
						//greenImg.createNewFile();

						if (blueImg.exists()) {
							blueImg.delete();
						}
						//blueImg.createNewFile();

						JOptionPane.showMessageDialog(null, "Created RGB files");
					}

				} 
//				else {
//					try {
//						
//						redImg.createNewFile();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//					try {
//						greenImg.createNewFile();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//					try {
//						blueImg.createNewFile();
//					} catch (IOException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//
//					JOptionPane.showMessageDialog(null, "Created RGB files");
//				}

				// get RGB from image

				BufferedImage buffImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
				System.out.println("path: " + imageFile.getAbsolutePath());
				try {
					buffImage = ImageIO.read(imageFile);
				} catch (IOException e) {
				}
				
				int imageHeight = buffImage.getHeight();
				int imageWidth = buffImage.getWidth();
				//PixelGrabber pixelGrabber = null;
				Color individualPixel = null;
				Color tempPixel = null;
				//int[] pixels = new int[1];
			    //pixels[0] = -1;
				int alpha = 0;
				int red = 0;
				int green = 0;
				int blue = 0;

				BufferedImage buffRed = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
				BufferedImage buffGreen = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
				BufferedImage buffBlue = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
				
				
				
				int i = 0;
				int j = 0;
				
				try {
					for (i = 0; i < imageHeight; i++) {
						for (j = 0; j < imageWidth; j++) {
							individualPixel = new Color(buffImage.getRGB(j,i));
							//System.out.println("individualPixel: "+individualPixel.toString());
							alpha = individualPixel.getAlpha();
							red = individualPixel.getRed();
							green = individualPixel.getGreen();
							blue = individualPixel.getBlue();
							
							//RED
							tempPixel = new Color(individualPixel.getRed(),0,0,individualPixel.getAlpha());						
							buffRed.setRGB(j, i, getIntFromColor(tempPixel.getRed(),0,0,tempPixel.getAlpha()));
							//buffRed.setRGB(j, i, tempPixel.getRed());
							
							//GREEN
							tempPixel = new Color(0,individualPixel.getGreen(),0,individualPixel.getAlpha());						
							buffGreen.setRGB(j, i, getIntFromColor(0,tempPixel.getGreen(),0,tempPixel.getAlpha()));
							//buffRed.setRGB(j, i, tempPixel.getGreen());
							
							//BLUE
							tempPixel = new Color(0,0,individualPixel.getBlue(),individualPixel.getAlpha());						
							buffBlue.setRGB(j, i, getIntFromColor(0,0,tempPixel.getBlue(),tempPixel.getAlpha()));
							//buffRed.setRGB(j, i, tempPixel.getBlue());
							
						}
					}
				} catch (Exception e) {

					System.out.println("Failed at index : " + j + ", " + i);
					System.out.println("Image Height : " + imageWidth + ", " + imageHeight);
					System.out.println("bufferedImage: "+ buffImage.getWidth() + ", " + buffImage.getHeight());
					System.out.println("bufferedRed: "+ buffRed.getWidth() + ", " + buffRed.getHeight());

				}
				
				
				
//				 public void printPixelARGB(int pixel) {
//				    int alpha = (pixel >> 24) & 0xff;
//				    int red = (pixel >> 16) & 0xff;
//				    int green = (pixel >> 8) & 0xff;
//				    int blue = (pixel) & 0xff;
//				    System.out.println("argb: " + alpha + ", " + red + ", " + green + ", " + blue);
//				  }
				
				try {
					ImageIO.write(buffRed, "png", redImg);
					ImageIO.write(buffGreen, "png", greenImg);
					ImageIO.write(buffBlue, "png", blueImg);
				} catch (IOException e) {
					
					JOptionPane.showMessageDialog(null, "Failed to create RGB files");
					e.printStackTrace();
				}
				
				JOptionPane.showMessageDialog(null, "All Done");
			}
		});
		// -----------------------------------------------------------------
		jmenu.add(jOpen);
		jmenu.add(jRGB);
		jmenu.add(jExit);
		jbar.add(jmenu);
		jFrame.setJMenuBar(jbar);

		// jFrame.setSize(new Dimension(300, 300));
		jFrame.pack();
		jFrame.setResizable(true);
		jFrame.setVisible(true);
	}

	public int getIntFromColor(int R, int G, int B, int A){
	    
	    A = (A << 24) & 0xFF000000;
	    R = (R << 16) & 0x00FF0000;
	    G = (G << 8) & 0x0000FF00;
	    B = B & 0x000000FF;

	    return  A | R | G | B;
	}
	
	//Main Function
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Main();
			}
		});
	}
}