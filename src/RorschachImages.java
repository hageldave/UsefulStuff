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
 * {@link #getRorschachImage(int, int, long, Color, Color)}
 * create images that have some kind of rorschach characteristic
 * since they are y-Axis symmetrical.
 * The clue to it all is that the methods will produce the exact same
 * image everytime when beeing fed same parameters.
 * 
 * @author David Haegele
 * @version 1.1 - 17.12.13
 * 
 */
public class RorschachImages {

	/**
	 * Creates a BufferedImage from given values.
	 * The buffered Image will be of size: <br>
	 * (2*halfwidth) x height <br>
	 * The pixels will be generated using all parameters.
	 * Fore- and backgroundcolor are also generated from hash value.
	 * @param halfwidth half width of resulting image
	 * @param height of resukting image
	 * @param hash value from which the algorithm generates pixels
	 * @return pseude random rorschach image
	 */
	static BufferedImage getRorschachImage(int halfwidth, int height, int hash){
		Random r = new Random(hash);
		Color bg = new Color(r.nextInt());
		Color fg = new Color(r.nextInt());
		
		// make bg darker
		int rBG = bg.getRed() -80; 		rBG = rBG < 0 ? 0:rBG;
		int gBG = bg.getGreen() -80; 	gBG = gBG < 0 ? 0:gBG;
		int bBG = bg.getBlue() -80; 	bBG = bBG < 0 ? 0:bBG;
		bg = new Color(rBG, gBG, bBG);
		
		// make fg brighter
		int rFG = fg.getRed() +80; 		rFG = rFG > 255 ? 255: rFG;
		int gFG = fg.getGreen() +80; 	gFG = gFG > 255 ? 255: gFG;
		int bFG = fg.getBlue() +80; 	bFG = bFG > 255 ? 255: bFG;
		fg = new Color(rFG, gFG, bFG);
		
		return getRorschachImage(halfwidth, height, hash, bg, fg);
	}
	
	
	/**
	 * Creates a BufferedImage from given values.
	 * The buffered Image will be of size: <br>
	 * (2*halfwidth) x height <br>
	 * The pixels will be generated using all parameters.
	 * @param halfwidth half width of resulting image
	 * @param height of resukting image
	 * @param hash value from which the algorithm generates pixels
	 * @param bg backgroundcolor of image
	 * @param fg foregroundcolor of image
	 * @return pseudo random rorschach image
	 */
	public static BufferedImage getRorschachImage(int halfwidth, int height, int hash, Color bg, Color fg){
		int[] pixels = new int[halfwidth*height];
		Random r = new Random(hash);
		for(int i = 0; i < pixels.length; i++){
			pixels[i] = r.nextBoolean() ? bg.getRGB():fg.getRGB();
		}
		
		BufferedImage image = new BufferedImage(halfwidth*2, height, BufferedImage.TYPE_4BYTE_ABGR);
		int x,y;
		for(int i = 0; i < pixels.length; i++){
			x = i % halfwidth;
			y = i / halfwidth;
			image.setRGB(x, y, pixels[i]);
			image.setRGB(halfwidth*2 -x -1, y, pixels[i]);
		}
		return image;
	}
	
	/**
	 * Scales the specified image to specified width and height.
	 * @param srcImg source image
	 * @param width
	 * @param height
	 * @return scaled image
	 */
	public static Image getScaledImage(Image srcImg, int width, int height){
	    BufferedImage resizedImg = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
	    Graphics2D g2 = resizedImg.createGraphics();
	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
	    g2.drawImage(srcImg, 0, 0, width, height, null);
	    g2.dispose();
	    return resizedImg;
	}
	
	/** Interactive Example */
	public static void main(String[] args) {
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
				int halfwidth = sizeslider.getValue();
				int height = 2*halfwidth;
				label.setIcon(new javax.swing.ImageIcon(getScaledImage(getRorschachImage(halfwidth, height, hash), 200, 200)));
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
}
