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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
 * @version 1.0 - 17.12.13
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
	public static BufferedImage getRorschachImage(int halfwidth, int height, int hash){
		Random r = new Random(hash);
		Color bg = new Color(r.nextInt());
		Color fg = new Color(r.nextInt());
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
//	    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
	    g2.drawImage(srcImg, 0, 0, width, height, null);
	    g2.dispose();
	    return resizedImg;
	}
	
	/** Interactive Example */
	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		int hashvalue = 45;
		final JLabel label = new JLabel(new ImageIcon(getScaledImage(getRorschachImage(6, 12, hashvalue), 200, 200)));
		final JSlider slider = new JSlider(20, 520, 20);
		slider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent arg0) {
				label.setIcon(new ImageIcon(getScaledImage(getRorschachImage(6, 12, slider.getValue()), 200, 200)));
			}
		});
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(label, BorderLayout.CENTER);
		frame.getContentPane().add(slider, BorderLayout.SOUTH);
		frame.setVisible(true);
	}
}
