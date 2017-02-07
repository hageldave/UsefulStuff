/* RorschachImages.java
 * 
 * Copyright (c) 2013 David Haegele
 *
 * (MIT License)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Class that provides a method for creating pseudo random images.
 * {@link #getRorschachImage(int, int, long)} and
 * {@link #getRorschachImage(int, int, long, Color, Color)} create images that
 * have some kind of rorschach characteristic since they are y-Axis symmetrical.
 * The clue to it all is that the methods will produce the exact same image
 * everytime when beeing fed same parameters.
 * 
 * @author David Haegele
 * @version 1.1 - 17.12.13
 * 
 */
public class RorschachImages {

	/**
	 * Creates a BufferedImage from given values. The buffered Image will be of
	 * size: <br>
	 * width x height <br>
	 * The pixels will be generated using all parameters. Fore- and
	 * backgroundcolor are also generated from hash value.
	 * 
	 * @param halfwidth of resulting image
	 * @param height of resulting image
	 * @param hash
	 *            value from which the algorithm generates pixels
	 * @return pseudo random rorschach image
	 */
	public static BufferedImage getRorschachImage(int width, int height, int hash) {
		Random r = new Random(hash);
		Color bg = new Color(r.nextInt());
		Color fg = new Color(r.nextInt());

		// make bg darker
		int rBG = bg.getRed() - 80;
		rBG = rBG < 0 ? 0 : rBG;
		int gBG = bg.getGreen() - 80;
		gBG = gBG < 0 ? 0 : gBG;
		int bBG = bg.getBlue() - 80;
		bBG = bBG < 0 ? 0 : bBG;
		
		// make fg brighter
		int rFG = fg.getRed() + 80;
		rFG = rFG > 255 ? 255 : rFG;
		int gFG = fg.getGreen() + 80;
		gFG = gFG > 255 ? 255 : gFG;
		int bFG = fg.getBlue() + 80;
		bFG = bFG > 255 ? 255 : bFG;
		
		int[] rgbBG = new int[]{rBG,gBG,bBG};
		int[] rgbFG = new int[]{rFG,gFG,bFG};
		
		// temporary colors
		bg = new Color(rgbBG[0], rgbBG[1], rgbBG[2]);
		fg = new Color(rgbFG[0], rgbFG[1], rgbFG[2]);
		
		
		// raise contrast of bg color in terms of fg
		rgbBG = contrastRGB(rgbBG, 0.2f, getGrey(fg));
		// lower saturation of bg
		rgbBG = saturateRGB(rgbBG, -0.5f);
		fixOutOfRangeRGB(rgbBG);
		// final bg color
		bg = new Color(rgbBG[0], rgbBG[1], rgbBG[2]);

		// lower contrast of fg in terms of bg
		rgbFG = contrastRGB(rgbFG, -0.5f, getGrey(bg));
		// raise saturation of fg
		rgbFG = saturateRGB(rgbFG, 0.5f);
		rgbFG = fixOutOfRangeRGB(rgbFG);
		// final fg color
		fg = new Color(rgbFG[0], rgbFG[1], rgbFG[2]);
		
		return getRorschachImage(width, height, hash, bg, fg);
	}
	
	/** 
	 * Averages red blue and green which results 
	 * in the grey value of the color 
	 */
	private static int getGrey(Color c){
		int grey = c.getRed()+c.getBlue()+c.getGreen();
		grey = grey / 3;
		return grey;
	}
	
	/** 
	 * Averages red blue and green which results 
	 * in the grey value of the color 
	 */
	private static int getGrey(int[] rgb){
		return (rgb[0]+rgb[1]+rgb[2])/3;
	}

	/**
	 * Saturates the rgb color by the given ammount.
	 * @param rgb
	 * @param saturation - 0 for no change, negative for desaturation and
	 * positive values for saturation.
	 * @return saturated rgb
	 */
	private static int[] saturateRGB(int[] rgb, float saturation){
		return saturateRGB(rgb, saturation, getGrey(rgb));
	}

	/**
	 * Saturates the rgb color by the given ammount relative to the gven
	 * grey value.
	 * @param rgb
	 * @param saturation - 0 for no change, negative for desaturation and
	 * positive values for saturation.
	 * @param grey - byte value of grey typically between 0 and 255 (#00 and #FF)
	 * @return saturated rgb
	 */
	private static int[] saturateRGB(int[] rgb, float saturation, int grey){
		rgb[0] = (int) (rgb[0] + ((rgb[0]-grey)*saturation));
		rgb[1] = (int) (rgb[1] + ((rgb[1]-grey)*saturation));
		rgb[2] = (int) (rgb[2] + ((rgb[2]-grey)*saturation));
		return rgb;
	}
	
	/**
	 * Increases or lowers contrast of a rgb color
	 * @param rgb
	 * @param intensity - 0 for no change, negative for decontrasting, positive
	 * values for increasing contrast
	 * @param threshold - byte value of grey typically between 0 and 255 (#00 and #FF)
	 * - determines towards which lightness level the contrast will be altered.
	 * @return
	 */
	private static int[] contrastRGB(int[] rgb, float intensity, int threshold){
		int grey = (rgb[0]+rgb[1]+rgb[2])/3;
		int dif = grey - threshold;
		rgb[0] = (int) (rgb[0] + (dif * intensity));
		rgb[1] = (int) (rgb[1] + (dif * intensity));
		rgb[2] = (int) (rgb[2] + (dif * intensity));
		return rgb;
	}
	
	private static int[] fixOutOfRangeRGB(int[] rgb){
		rgb[0] = rgb[0] < 0 ? 0: (rgb[0] > 255 ? 255: rgb[0]);
		rgb[1] = rgb[1] < 0 ? 0: (rgb[1] > 255 ? 255: rgb[1]);
		rgb[2] = rgb[2] < 0 ? 0: (rgb[2] > 255 ? 255: rgb[2]);
		return rgb;
	}

	/**
	 * Creates a BufferedImage from given values. The buffered Image will be of
	 * size: <br>
	 * width x height <br>
	 * The pixels will be generated using all parameters.
	 * 
	 * @param width of resulting image
	 * @param height of resulting image
	 * @param hash
	 *            value from which the algorithm generates pixels
	 * @param bg
	 *            backgroundcolor of image
	 * @param fg
	 *            foregroundcolor of image
	 * @return pseudo random rorschach image
	 */
	public static BufferedImage getRorschachImage(int width, int height, int hash, Color bg, Color fg) {
		int[] pixels = new int[((width+1)/2) * height];
		Random r = new Random(hash);
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = r.nextBoolean() ? bg.getRGB() : fg.getRGB();
		}

		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		int x, y;
		for (int i = 0; i < pixels.length; i++) {
			x = i % ((width+1)/2);
			y = i / ((width+1)/2);
			image.setRGB(x, y, pixels[i]);
			image.setRGB(width - x - 1, y, pixels[i]);
		}
		return image;
	}

	/**
	 * Scales the specified image to specified width and height.
	 * 
	 * @param srcImg
	 *            source image
	 * @param width
	 * @param height
	 * @return scaled image
	 */
	public static Image getScaledImage(Image srcImg, int width, int height) {
		BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2 = resizedImg.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		g2.drawImage(srcImg, 0, 0, width, height, null);
		g2.dispose();
		return resizedImg;
	}
	
	/** Interactive Example */
	public static void startDemo() {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		final javax.swing.JLabel label = new javax.swing.JLabel(new javax.swing.ImageIcon(getScaledImage(getRorschachImage(4, 8, 20), 200, 200)));
		final javax.swing.JSlider hashslider = new javax.swing.JSlider(0, 1024, 20);
		final javax.swing.JSlider sizeslider = new javax.swing.JSlider(javax.swing.JSlider.VERTICAL, 2, 15, 4);
		javax.swing.event.ChangeListener changelistenr = new javax.swing.event.ChangeListener() {
			@Override
			public void stateChanged(javax.swing.event.ChangeEvent arg0) {
				int hash = hashslider.getValue();
				int width = sizeslider.getValue();
				int height = width;
				label.setIcon(new javax.swing.ImageIcon(getScaledImage(getRorschachImage(width, height, hash), 200, 200)));
			}
		};
		hashslider.addChangeListener(changelistenr);
		sizeslider.addChangeListener(changelistenr);
		
		frame.getContentPane().setLayout(new java.awt.BorderLayout());
		frame.getContentPane().add(label, java.awt.BorderLayout.CENTER);
		frame.getContentPane().add(hashslider, java.awt.BorderLayout.SOUTH);
		frame.getContentPane().add(sizeslider, java.awt.BorderLayout.EAST);
		frame.setVisible(true);
	}


	public static void main(String[] args) {
		startDemo();
	}
}
