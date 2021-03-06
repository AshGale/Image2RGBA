package main;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.NumberFormatter;

/**
 * 
 * @author Ash
 */

class Image2RGB {
	private JFrame jFrame;
	private JMenu jmenuFile, jmenuSettings, jmenuStitch;
	private JMenuBar jbar;
	private JMenuItem jOpen, jExit, jRGB, jScale, jGreyScale, jEdge, jviewOnFinshed, jreplaceWithLastJob, jstitchMode,jnewStitch,jstitchTogether;
	private JPanel jpanelImage,jpanelStitch;
	private File imageFile = null;
	private JLabel jlabelImage;
	private BufferedImage image;
	private String path = System.getProperty("user.home") + "\\Pictures";
	private Font font = new Font("Arial", Font.PLAIN, 18);

	int stitchWide = 3;
	int stitchHigh = 2;
	Boolean viewOnJobFinished = false;
	Boolean replaceWithLastJob = true;
	Boolean inStitchMode = false;


	Image2RGB() {		
		
		jFrame = new JFrame("Convert Image to RGB Files");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.setLocationByPlatform(true);
		jFrame.setLayout(new BorderLayout());
		jFrame.setMinimumSize(new Dimension(320, 320));

		jpanelImage = new JPanel();
		jpanelImage.setLayout(new BorderLayout());
		jlabelImage = new JLabel(" ");

		jpanelImage.add(jlabelImage, BorderLayout.CENTER);
		jpanelStitch = new JPanel(new GridLayout(stitchWide,stitchHigh));
		JScrollPane scrollFrame = new JScrollPane(jpanelStitch);
		jFrame.add(jpanelImage);

		// Creating Menu
		jbar = new JMenuBar();
		jmenuFile = new JMenu("File");
		jmenuSettings = new JMenu("Settings");
		jmenuStitch = new JMenu("Stitch");

		jmenuFile.setFont(font);
		jmenuSettings.setFont(font);
		jmenuStitch.setFont(font);
		
		//Border border = BorderFactory.createLineBorder(Color.BLUE, 1);

		jpanelStitch.setAutoscrolls(true);		
		// *****************************************************************************************

		/**
		 * 
		 * add other menu items for new stitch options
		 * - new stitch
		 * - stitch image
		 * - adjust stitch amount
		 * - small stitch
		 * 
		 * Features to add:
		 * set amount of images to stitch together
		 * - be able to set eg 2x3 or 6x1
		 * support small images, ie set min screen size to imageW*2ximageH*3 or imageW*6ximageH*1 respectively
		 * put scroll on each image for large images
		 * 
		 * Add rotate feature
		 * 
		 */
		
		
		// -----------------------------------------------------------------
		jnewStitch = new JMenuItem("Start new Stitch Grid");
		jnewStitch.setFont(font);
		jnewStitch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				if (inStitchMode) {
					if (jpanelStitch.getComponentCount() == 0) {
						// new stitch grid

						// Get Desired Width and Height
						NumberFormat numberFormat = NumberFormat.getInstance();
						numberFormat.setGroupingUsed(false);
						NumberFormatter formatter = new NumberFormatter(numberFormat);
						formatter.setValueClass(Integer.class);
						formatter.setMaximum(65535);
						formatter.setAllowsInvalid(false);
						formatter.setCommitsOnValidEdit(true);

						JTextField jColumbs = new JFormattedTextField(formatter);
						JTextField jRows = new JFormattedTextField(formatter);

						// default
						jColumbs.setText("3");
						jRows.setText("2");

						final JComponent[] inputs = new JComponent[] { new JLabel("Columbs"), jColumbs,
								new JLabel("Rows"), jRows };

						int result = JOptionPane.showConfirmDialog(null, inputs, "Ammount of Rows & Columbs",
								JOptionPane.PLAIN_MESSAGE);

						if (result == JOptionPane.OK_OPTION) {
							if (jColumbs.getText().equals("") || jRows.getText().equals("")) {
								stitchWide = 3;
								stitchHigh = 2;
							} else {
								stitchWide = Integer.parseInt(jColumbs.getText());// ensured number format with
																						// formatter
								stitchHigh = Integer.parseInt(jRows.getText());// ensured number format with formatter
								jpanelStitch.setLayout(new GridLayout(stitchHigh, stitchWide));
							}
						} else {
							System.out.println("User canceled / closed the dialog row & columbs, result = " + result);
						}

						// Generate the Stitch Field
						for (int y = 0; y < stitchHigh; y++) {
							for (int x = 0; x < stitchWide; x++) {
								JLabel jlebel = new JLabel(x + ", " + y);

								jlebel.setName(x + "," + y);
								// jlebel.setBorder(border);
								System.out.println(stitchHigh + " x " + stitchWide + ": " + x + "," + y);
								jlebel.addMouseListener(new MouseAdapter() {
									@Override
									public void mouseClicked(MouseEvent event) {
										System.out.println("Yay you clicked me: " + jlebel.getName());
										JFileChooser fileChooser = new JFileChooser(path);
										int result = fileChooser.showOpenDialog(null);

										if (result == JFileChooser.APPROVE_OPTION) {
											try {
												jlebel.setIcon(
														new ImageIcon(ImageIO.read(fileChooser.getSelectedFile())));
												jlebel.setText(null);
											} catch (Exception e) {
												JOptionPane.showMessageDialog(null,
														"Please ensure your selection is an image!");
												e.printStackTrace();
											}
										}
									}
								});
								jpanelStitch.add(jlebel);
							}
						}
						jFrame.pack();// this processes the render
					} else {
						// reset grid
						jpanelStitch.removeAll();
						jFrame.pack();// this processes the render
						jnewStitch.doClick();
					}

				} else {
					final JComponent[] inputs = new JComponent[] { new JLabel("Enable StitchMode?") };
					int result = JOptionPane.showConfirmDialog(null, inputs, "Enable StitchMode?",
							JOptionPane.PLAIN_MESSAGE);

					if (result == JOptionPane.OK_OPTION) {

						jstitchMode.doClick();
					} else {
						System.out.println("User canceled / closed the dialog row & columbs, result = " + result);
					}
				}
				jFrame.pack();// this processes the render
			}
		});		
		
		// -----------------------------------------------------------------

		jstitchMode = new JMenuItem("Toggle StitchMode (" + inStitchMode + ")");
		jstitchMode.setFont(font);
		jstitchMode.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				inStitchMode = !inStitchMode;
				jstitchMode.setText("Toggle StitchMode (" + inStitchMode + ")");

				if(inStitchMode){

					//scrollFrame.setPreferredSize(jFrame.getSize());
					//scrollFrame.set
					jFrame.remove(jpanelImage);
					jFrame.add(scrollFrame);
					jFrame.pack();// this processes the render

					//dialog for row & columbs
					
					if(jpanelStitch.getComponentCount() == 0) {
					
						jnewStitch.doClick();						
					}
					else{
						//Stitch field has components
						JLabel gridItem = (JLabel)jpanelStitch.getComponent(0);
		                Icon gridItemIcon = gridItem.getIcon();
		                Dimension requiredMin = new Dimension(gridItemIcon.getIconWidth()*(stitchWide+1),gridItemIcon.getIconHeight()*(stitchHigh+1));
		                if( requiredMin.getWidth() < jFrame.getMinimumSize().getWidth() && requiredMin.getHeight() < jFrame.getMinimumSize().getHeight()) {
		                	jFrame.setMinimumSize(requiredMin);
							jFrame.setSize(jFrame.getMinimumSize());
		                }
		                else {
		            		jFrame.setMinimumSize(new Dimension(320, 320));
		                }
						
					}
				}else{
					System.out.println("here");
					//toggle main view
					jFrame.remove(scrollFrame);
					jFrame.add(jpanelImage);
					jFrame.pack();// this processes the render
				}
			}
		});
		
		// -----------------------------------------------------------------
		
		jstitchTogether = new JMenuItem("Stitch Image Together");
		jstitchTogether.setFont(font);
		jstitchTogether.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				
				
				
				// If no images in stitch grid
				if (jpanelStitch.getComponentCount() == 0) {
					final JComponent[] inputs = new JComponent[] { new JLabel("Enable StitchMode?") };
					int result = JOptionPane.showConfirmDialog(null, inputs, "Enable StitchMode?",
							JOptionPane.PLAIN_MESSAGE);

					if (result == JOptionPane.OK_OPTION) {

						jstitchMode.doClick();
					} else {
						System.out.println("User canceled / closed the dialog row & columbs, result = " + result);
					}
				} else {
 
					setupfileStructure();
					
					//TO
					String name = stitchWide + "x" + stitchHigh;
					
					File ouputStitchImage = new File(path + "\\ImageProcessing\\StitchedImages\\" + name + ".png");

					JLabel gridItem = (JLabel)jpanelStitch.getComponent(0);
	                Icon gridItemIcon = gridItem.getIcon();
	                	                
					int imageHeight = gridItemIcon.getIconHeight();
					int imageWidth = gridItemIcon.getIconWidth();
					int countItems = 0;

					BufferedImage outBuffImage = new BufferedImage(imageWidth*stitchWide, imageHeight*stitchHigh, BufferedImage.TYPE_INT_ARGB);
					BufferedImage itemBuffImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);					
					
					Graphics g = itemBuffImage.createGraphics();
					int[] data = new int[imageWidth*imageHeight];
					
					// loop through all images in jpanelStitch
					for (int high = 0; high < stitchHigh; high++) {
						for (int wide = 0; wide < stitchWide; wide++) {
							System.out.println("---" + wide + " : " + high);
							gridItem = (JLabel) jpanelStitch.getComponent(countItems++);
							gridItemIcon = gridItem.getIcon();
							if (gridItemIcon != null) {
								System.out.println("Image at " + wide + "," + high + " was null");

								// paint the Icon to the BufferedImage.
								g = itemBuffImage.createGraphics();
								gridItemIcon.paintIcon(null, g, 0, 0);
								g.dispose();

								WritableRaster imageRaster = itemBuffImage.getRaster();
								int[] pixelRGBA = new int[4];

								for (int i = 0; i < imageHeight; i++) {
									for (int j = 0; j < imageWidth; j++) {
										imageRaster.getPixel(j, i, pixelRGBA);
										// System.out.println(imageRaster.toString());
										itemBuffImage.setRGB(j, i, getIntFromColor(pixelRGBA[0], pixelRGBA[1],
												pixelRGBA[2], pixelRGBA[3]));
									}
								}
								data = itemBuffImage.getRGB(0, 0, imageWidth, imageHeight, null, 0, imageWidth);

								// System.out.println("*--"+items + " " + items*imageWidth + " " +
								// itemBuffImage.getWidth() +" "+ outBuffImage.getWidth());
								// System.out.println("*--"+items + " " + items*imageHeight + " " +
								// itemBuffImage.getHeight() +" "+ outBuffImage.getHeight());

								outBuffImage.setRGB(wide * imageWidth, high * imageHeight, imageWidth, imageHeight,
										data, 0, imageWidth);
							}
						}
					}
					
					jstitchMode.doClick();
					savImageFile(outBuffImage, "png", ouputStitchImage);
					JOptionPane.showMessageDialog(null, "All Done");
					openNewFileLocation("Rgb");
					// TODO here :D
				}
				
				//save and export
			}
		});
		
		// -----------------------------------------------------------------

		// *****************************************************************************************
		jviewOnFinshed = new JMenuItem("View on Finished (" + viewOnJobFinished + ")");
		jviewOnFinshed.setFont(font);
		jviewOnFinshed.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				viewOnJobFinished = !viewOnJobFinished;
				jviewOnFinshed.setText("View on Finished (" + viewOnJobFinished + ")");
			}
		});

		// -----------------------------------------------------------------
		jreplaceWithLastJob = new JMenuItem("Load last processed image (" + replaceWithLastJob + ")");
		jreplaceWithLastJob.setFont(font);
		jreplaceWithLastJob.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				replaceWithLastJob = !replaceWithLastJob;
				jreplaceWithLastJob.setText("Load last processed image (" + replaceWithLastJob + ")");
			}
		});

		// *****************************************************************************************

		jOpen = new JMenuItem("Open");
		jOpen.setFont(font);
		jOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				
				JFileChooser fileChooser = new JFileChooser(path);
				int result = fileChooser.showOpenDialog(null);

				// Ensure File structure set up 
				setupfileStructure();

				if (result == JFileChooser.APPROVE_OPTION) {
					
					imageFile = fileChooser.getSelectedFile();
					try {
						jlabelImage.setIcon(new ImageIcon(ImageIO.read(imageFile)));
						image = ImageIO.read(imageFile);
						jFrame.pack();
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null, "Please ensure your selection is an image!");
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

					int imageWidth = image.getWidth();
					int imageHeight = image.getHeight();
					
					// Get Desired Width and Height
					NumberFormat numberFormat = NumberFormat.getInstance();
					numberFormat.setGroupingUsed(false);
					NumberFormatter formatter = new NumberFormatter(numberFormat);
					formatter.setValueClass(Integer.class);
					formatter.setMaximum(65535);
					formatter.setAllowsInvalid(false);
					formatter.setCommitsOnValidEdit(true);						

					JTextField newWidth = new JFormattedTextField(formatter);
					JTextField newHeight = new JFormattedTextField(formatter);
					
					newWidth.setText(""+imageWidth);
					newHeight.setText(""+imageHeight);
					
					JTextField percentWidth = new JFormattedTextField(formatter);
					JTextField percentHeight = new JFormattedTextField(formatter);
					
					percentWidth.setText("100");
					percentHeight.setText("100");

					//ToDo add listeners for outputsizes to update percentage

					//Listeners for Percentage
					percentWidth.getDocument().addDocumentListener(new DocumentListener() {
						public void changedUpdate(DocumentEvent e) {
							changed();
						}

						public void removeUpdate(DocumentEvent e) {
							changed();
						}

						public void insertUpdate(DocumentEvent e) {
							changed();
						}

						public void changed() {
							try {
								float wp = Float.parseFloat(percentWidth.getText());
								if (wp >= 1) {
									newWidth.setText("" + Math.round(imageWidth * (wp / 100)));
								}
							} catch (NumberFormatException e) {					
								newWidth.setText("" + imageWidth);								
							}
						}
					});
					
					percentHeight.getDocument().addDocumentListener(new DocumentListener() {
						public void changedUpdate(DocumentEvent e) {
							changed();
						}

						public void removeUpdate(DocumentEvent e) {
							changed();
						}

						public void insertUpdate(DocumentEvent e) {
							changed();
						}

						public void changed() {
							try {
								float hp = Float.parseFloat(percentHeight.getText());
								if (hp >= 1) {
									newHeight.setText("" + Math.round(imageHeight * (hp / 100)));
								}
							} catch (NumberFormatException e) {						
								newHeight.setText("" + imageHeight);								
							}
						}
					});
					
					//newWidth.requestFocusInWindow();
					
					final JComponent[] inputs = new JComponent[] {
							new JLabel("Source Image size: " + image.getWidth() + " x " + image.getHeight()),
							new JLabel(""),
							new JLabel("Output Width"), newWidth,
							new JLabel("Output Height"), newHeight,							
							new JLabel("Percent Width"), percentWidth,
							new JLabel("Percent Height"), percentHeight
					};
					
					int result = JOptionPane.showConfirmDialog(null, inputs, "Output Image size",
							JOptionPane.PLAIN_MESSAGE);
					
					if (result == JOptionPane.OK_OPTION) {

						outputWidth = Integer.parseInt(newWidth.getText());// ensured number format with formatter
						outputHeight = Integer.parseInt(newHeight.getText());// ensured number format with formatter
					} else {
						System.out.println("User canceled / closed the dialog scale, result = " + result);
					}

					if (outputHeight != 0 || outputWidth != 0) {							
					
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
					} // end if output dimensions 0

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

					// convert image to Greyscale version
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

		jEdge = new JMenuItem("Edge Detection");
		jEdge.setFont(font);
		jEdge.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {

				// If Image selected already
				if (image == null) {
					jOpen.doClick();
					jEdge.doClick();
				} else {

					File edgeImage = generateFile("Edges", "Edges");

					// convert image to Edges version
					BufferedImage inputBuffImage = getMainImageBuffered();

					int[][] filter1 = { { -1, 0, 1 },
										{ -2, 0, 2 },
										{ -1, 0, 1 } };

					int[][] filter2 = { { 1, 2, 1 },
										{ 0, 0, 0 },
										{ -1, -2, -1 } };

					int width = inputBuffImage.getWidth();
					int height = inputBuffImage.getHeight();
					BufferedImage outBuffImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

					WritableRaster imageRaster = inputBuffImage.getRaster();
					int[] pixelRGBA = new int[4];

					for (int y = 1; y < height - 1; y++) {
						for (int x = 1; x < width - 1; x++) {

							// get 3-by-3 array of colors in neighborhood
							int[][] gray = new int[3][3];
							for (int i = 0; i < 3; i++) {
								for (int j = 0; j < 3; j++) {

									// Magic
									imageRaster.getPixel(x - 1 + i, y - 1 + j, pixelRGBA);
									int r = pixelRGBA[0];
									int g = pixelRGBA[1];
									int b = pixelRGBA[2];

									// int lumSum= (int) (.299*r + .587*g + .114*b);
									int lumSum = (int) (0.2126f * r + 0.7152f * g + 0.0722f * b);// luminosity method

									gray[i][j] = (int) (Math.round(lumSum));
								}
							}

							// apply filter
							int gray1 = 0, gray2 = 0;
							for (int i = 0; i < 3; i++) {
								for (int j = 0; j < 3; j++) {
									gray1 += gray[i][j] * filter1[i][j];
									gray2 += gray[i][j] * filter2[i][j];
								}
							}

							int magnitude = 255 - truncate((int) Math.sqrt(gray1 * gray1 + gray2 * gray2));// ensure
																											// 0-255

							outBuffImage.setRGB(x, y, getIntFromColor(magnitude, magnitude, magnitude, 255));
						}
					}

					savImageFile(outBuffImage, "png", edgeImage);

					JOptionPane.showMessageDialog(null, "All Done");

					openNewFileLocation("Edges");
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

		// *****************************************************************************************



		jmenuFile.add(jOpen);
		jmenuFile.add(jRGB);
		jmenuFile.add(jGreyScale);
		jmenuFile.add(jEdge);
		jmenuFile.add(jScale);
		jmenuFile.add(jExit);
		jmenuSettings.add(jreplaceWithLastJob);
		jmenuSettings.add(jviewOnFinshed);
		jmenuStitch.add(jstitchMode);
		jmenuStitch.add(jnewStitch);
		jmenuStitch.add(jstitchTogether);
		jbar.add(jmenuFile);
		jbar.add(jmenuSettings);
		jbar.add(jmenuStitch);
		jFrame.setJMenuBar(jbar);

		jFrame.pack();// this processes the render
		jFrame.setResizable(true);
		jFrame.setVisible(true);
	}// End Image2RGB

	private static int truncate(int a) {
		if (a < 0)
			return 0;
		else if (a > 255)
			return 255;
		else
			return a;
	}

	private void openNewFileLocation(String fileFolderLocation) {

		if (viewOnJobFinished) {
			try {
				Runtime.getRuntime().exec("explorer " + path + "\\ImageProcessing\\" + fileFolderLocation);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void savImageFile(BufferedImage bufferReader, String fileFormat, File file) {
		try {
			ImageIO.write(bufferReader, "png", file);
		} catch (IOException e) {

			JOptionPane.showMessageDialog(null, "Failed to create RGB files");
			e.printStackTrace();
		}
		if (replaceWithLastJob) {
			jlabelImage.setIcon(new ImageIcon(bufferReader));
			imageFile = file;
			image = bufferReader;
			jFrame.pack();
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
		name += "_";

		return new File(path + "\\ImageProcessing\\" + folderPrefix + "\\" + name + fileName + ".png");

	}

	private void setupfileStructure() {

		File imageProcessing = new File(path + "\\ImageProcessing");
		File rgb = new File(path + "\\ImageProcessing\\Rgb");
		File scaled = new File(path + "\\ImageProcessing\\Scaled");
		File greyScale = new File(path + "\\ImageProcessing\\Grey_Scale");
		File edges = new File(path + "\\ImageProcessing\\Edges");
		File stitch = new File(path + "\\ImageProcessing\\StitchedImages");

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
		if (!edges.exists()) {
			edges.mkdir();
		}
		if (!stitch.exists()) {
			stitch.mkdir();
		}

	}

	private int getIntFromColor(int R, int G, int B, int A) {
		// System.out.print(A + " " + R + " " + G + " " + B + " = " );
		A = (A << 24) & 0xFF000000;
		R = (R << 16) & 0x00FF0000;
		G = (G << 8) & 0x0000FF00;
		B = B & 0x000000FF;
		// System.out.println((A | R | G | B));
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