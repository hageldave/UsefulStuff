package old.gui;
/*
 * CoordinateSystemDisplay.java
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
import static java.lang.Math.*;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

/**
 * <b>
 * This Panel displays a 3 dimensional coordinate system.
 * It is capable of displaying a set of points as well as a line along 
 * a set of points in 3D space. 
 * </b><p>
 * The Class uses a directional vector for each axis that determines
 * the angle and direction of the corresponding axis. 
 * ( {@link #x0vec}, {@link #x1vec}, {@link #x2vec} ) <br>
 * {@link #origin} determines the position of the origin on the panel.
 * <p>
 * This Panel also comes with a predefined MouseAdapter allowing
 * multiple interactions with the coordinate system using the mouse.
 * You can disable it using {@link #enableMouseControl(boolean)} and
 * apply your own MouseAdapter as MouseListeners to this panel or you
 * can even try overriding {@link MouseControl} which is the 
 * implementation of this panels MouseAdapter.
 * </p><p>
 * This Class paints points contained in {@link #points} and draws a
 * line along the points contained in {@link #linepoints}. <br>
 * This behavior can of course be changed by extending this class and
 * overriding the different paint methods. <br>
 * ( {@link #paintStuff(Graphics)}, {@link #paintPoints(Graphics)},
 * {@link #paintPolyline(Graphics)}, {@link #paintPointTraces(Graphics)},
 * {@link #paintPointLabels(Graphics)} )
 * </p><p>
 * For translating a 3d Point to 2D for painting it use: <br>
 * {@link #translatePoint_getX(double[])} and 
 * {@link #translatePoint_getY(double[])}.
 * </p><p>
 * There is also a method {@link #startDemo()} included that starts a
 * little demonstration for you to see how this panel can look and how
 * it can be interacted with.
 * </p>
 * 
 * @author David Hägele
 * @version 1.0
 * @since 14.05.2013
 *
 */
@SuppressWarnings("serial")
public class CoordinateSystemDisplay extends JPanel {
	
	/** 2D directional vector of the X-Axis (by default a unit vector) */
	public final double[] x0vec = new double[]{1.0, 0.0};
	/** 2D directional vector of the Y-Axis (by default a unit vector) */
	public final double[] x1vec = new double[]{0.0, -1.0};
	/** 2D directional vector of the Z-Axis (by default a unit vector) */
	public final double[] x2vec = new double[]{0.0, 1.0};
	/** 2D point of the origins position */
	public final int[] origin = new int[]{0,0};
	
	/** size at which points get rendered at. 0 = 1px, 2 = 3px */
	private int dotSize = 2;
	/** switch for toggling trace lines to points */
	private boolean pointTracesEnabled = false;
	/** switch for toggling labels of points */
	private boolean pointLabelsEnabled = false;
	/** switch for enabling Antialiasing */
	private boolean antialiasingEnabled = false;
	/** switch for toggling scales on axis */
	private boolean axisScaleEnabled = false;
	/** zoom factor at which the coordinate system gets displayed */
	private double currentZoom = 1.0;
	/** {@link MouseAdapter} that listens to mouse events and enables 
	 * interactivity on this coordinate system. By default this is an
	 * instance of {@link MouseControl}. */
	protected MouseAdapter mouseControl = new MouseControl();
	
	/* color definitions for items of the coordinate system */
	protected Color colorX0Axis = new Color(0x568f56);
	protected Color colorX1Axis = new Color(0x9f5c9f);
	protected Color colorX2Axis = new Color(0x00A7A7);
	protected Color colorPoint = Color.black;
	protected Color colorLine = Color.black;
	
	/** Array of double Arrays of size 3. <p><pre>
	 * double[] point = points[i]
	 * double xCoordinate = points[i][0]  
	 * double zCoordinate = points[i][2]
	 * </pre></p>
	 */
	protected double[][] points = new double[0][0];
	/** Array of points similar to {@link #points} but defining a polyline */
	protected double[][] linepoints = new double[0][0];
	
	/** 
	 * default constructor for a coordinate system
	 * @see JPanel#JPanel()
	 */
	public CoordinateSystemDisplay() {
	}
	
	/** 
	 * constructor for a coordinate system
	 * @param isDoubleBuffered
	 * @see JPanel#JPanel(boolean)
	 */
	public CoordinateSystemDisplay(boolean isDoubleBuffered){
		super(isDoubleBuffered);
	}
	
	/**
	 * resets all Axes. <br>
	 * default is: <pre>
	 * xAxis = (1,  0) -> points right
	 * yAxis = (0,  1) -> points up
	 * zAxis = (0, -1) -> points down
	 * </pre>
	 */
	public void resetAxes(){
		x2vec[1] = x0vec[0] = 1.0; 
		x2vec[0] = x1vec[0] = x0vec[1] = 0.0;
		x1vec[1] = -1.0;
	}
	
	/**
	 * resets the position of the origin. <br>
	 * default is the center of the panel
	 */
	public void resetOrigin(){
		origin[0] = getWidth()/2; origin[1] = getHeight()/2;
	}
	
	/**
	 * resets zoom to 1.0
	 */
	public void resetZoom(){
		currentZoom = 1.0;
	}
	
	/** 
	 * zooms into coordinate system.
	 * doubles zoom value.
	 */
	public void zoomIn(){
		currentZoom *= 2.0;
	}
	
	/**
	 * zooms out of coordinate system.
	 * halves zoom values.
	 */
	public void zoomOut(){
		currentZoom /= 2.0;
	}
	
	public double getCurrentZoom() {
		return currentZoom;
	}
	
	/**
	 * sets the color, points are rendered with, to the specified color.
	 * @param c color of points
	 */
	public void setPointColor(Color c){
		this.colorPoint = c;
	}
	
	/**
	 * sets the color the polyline is rendered with to the specified color.
	 * @param c color of the polyline
	 */
	public void setLineColor(Color c){
		this.colorLine = c;
	}
	
	/**
	 * enables Mouse interactivity for this coordinate system.
	 * for detailed information about the possibilities see 
	 * {@link #mouseControl} or even {@link MouseControl} which is
	 * the default MouseAdapter for this coordinate system.
	 * @param enable if true -> is enabled
	 */
	public void enableMouseControl(boolean enable){
		removeMouseListener(mouseControl);
		removeMouseMotionListener(mouseControl);
		removeMouseWheelListener(mouseControl);
		if(enable){
			addMouseListener(mouseControl);
			addMouseMotionListener(mouseControl);
			addMouseWheelListener(mouseControl);
		}
	}
	
	/**
	 * enables Antialiasing for this coordinate system.
	 * when enabled lines are smooth. <br>
	 * call {@link #repaint()} in order to see the change
	 * @param enable if true -> AA is on
	 */
	public void enableAntiAliasing(boolean enable){
		this.antialiasingEnabled = enable;
	}
	
	public boolean isAntialiasingEnabled() {
		return antialiasingEnabled;
	}
	
	/**
	 * enables trace lines to points for better comprehension 
	 * of location in 3D Space. <br>
	 * call {@link #repaint()} in order to see the change
	 * @param enable if true -> lines are shown
	 */
	public void enablePointTraces(boolean enable){
		this.pointTracesEnabled = enable;
	}
	
	public boolean arePointTracesEnabled() {
		return pointTracesEnabled;
	}
	
	/** 
	 * enables labels for points. If enabled each point gets
	 * a label next to it displaying the coordinates of the point.
	 * <br>
	 * call {@link #repaint()} in order to see the change
	 * @param enable if true -> labels are shown
	 */
	public void enablePointLabels(boolean enable){
		this.pointLabelsEnabled = enable;
	}
	
	public boolean arePointLabelsEnabled() {
		return pointLabelsEnabled;
	}
	
	/** 
	 * enables Scales for the Axes.
	 * <br>
	 * call {@link #repaint()} in order to see the change
	 * @param enable if true -> each axis gets a scale
	 */
	public void enableAxisScales(boolean enable){
		this.axisScaleEnabled = enable;
	}

	public boolean areAxisScalesEnabled() {
		return axisScaleEnabled;
	}
	
	/** 
	 * Rotates specified axis around origin by specified angle.
	 * @param axis X-Axis = 0 | Y-Axis = 1 | Z-Axis = 2
	 * @param angle by which axis gets rotated (in radians)
	 */
	public void rotate_Axis(int axis, double angle){
		/* a rotation matrix is used for rotating the axis vector
		 * Rotation Matrix:
		 *   cos(a)  -sin(a)
		 *   sin(a)   cos(a)
		 */
		double cos = cos(angle);
		double sin = sin(angle);
		double x;
		
		switch (axis) {
		case 0:
			x = x0vec[0]; // backup
			x0vec[0] = cos * x + (-sin) * x0vec[1];
			x0vec[1] = sin * x +   cos  * x0vec[1];
			break;
		case 1:
			x = x1vec[0]; // backup
			x1vec[0] = cos * x + (-sin) * x1vec[1];
			x1vec[1] = sin * x +   cos  * x1vec[1];
			break;
		case 2:
			x = x2vec[0]; // backup
			x2vec[0] = cos * x + (-sin) * x2vec[1];
			x2vec[1] = sin * x +   cos  * x2vec[1]; 
		default:
			break;
		}
	}

	/**
	 * Don't Override this Method! <br> 
	 * If you want to change this panels way of painting 
	 * override {@link #paintStuff(Graphics)} instead.
	 * <br>
	 * (This method calls super.paint(g) and paintStuff(g) )
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		paintStuff(g);
	}
	
	/**
	 * Paints the Stuff. <br>
	 * Here's what it does exactly:
	 * <pre>
	 * if(antialiasingEnabled){
	 *		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	 * }
	 * paintXYZcolorExplanation(g);
	 * paintPointTraces(g);
	 * paintAxis(g);
	 * paintAxisScale(g);
	 * paintPoints(g);
	 * paintPointLabels(g);
	 * paintPolyline(g);
	 * </pre>
	 * @param g
	 */
	protected void paintStuff(Graphics g){
		if(antialiasingEnabled){
			((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		}
		paintXYZcolorExplanation(g);
		paintPointTraces(g);
		paintAxes(g);
		paintAxisScale(g);
		paintPoints(g);
		paintPointLabels(g);
		paintPolyline(g);
	}
	
	/**
	 * draws "XYZ" in the top left corner, with each character
	 * colored like its corresponding Axis. <br>
	 * X=x0, Y=x1 , Z=x2
	 * @param g
	 */
	protected void paintXYZcolorExplanation(Graphics g){
		Color c = g.getColor();
		g.setColor(colorX0Axis);
		g.drawString("X", 5, 12);
		g.setColor(colorX1Axis);
		g.drawString("Y", 15, 12);
		g.setColor(colorX2Axis);
		g.drawString("Z", 25, 12);
		g.setColor(c);
	}
	
	/**
	 * draws all points contained in {@link #points} to the
	 * coordinate system with {@link #currentZoom} applied.
	 * <p>
	 * A points coordinates are translated to 2D using
	 * {@link #translatePoint_getX(double[])} and
	 * {@link #translatePoint_getY(double[])}
	 * @param g
	 */
	protected void paintPoints(Graphics g){
		Color c = g.getColor();
		g.setColor(colorPoint);
		// iterate over all points
		for (double[] point : points) {
			g.drawRect(	translatePoint_getX(point) - dotSize/2, // x pos
						translatePoint_getY(point) - dotSize/2, // y pos
						dotSize, 
						dotSize);
		}
		g.setColor(c);
	}
	
	/**
	 * draws coordinates for each point next to the point
	 * @param g
	 */
	protected void paintPointLabels(Graphics g){
		if(!pointLabelsEnabled){
			return;
		}
		Color c = g.getColor();
		g.setColor(colorPoint);
		for(double[] point : points){
			int x = translatePoint_getX(point);
			int y = translatePoint_getY(point);
			
			g.drawString("(" + point[0] + "|" + point[1] + "|" + point[2] + ")", x+dotSize, y+dotSize);
		}
		g.setColor(c);
	}
	
	/**
	 * draws trace lines to each point for better comprehension 
	 * of location in 3D Space.
	 * @param g
	 */
	protected void paintPointTraces(Graphics g){
		if(!pointTracesEnabled) {
			return;
		}
		double z = currentZoom;
		Color c = g.getColor();
		for (double[] point : points) {
			// draw zAxis part
			g.setColor(Color.cyan);
			g.drawLine(	origin[0], 
						origin[1], 
						(int) (point[2] * x2vec[0]*z + origin[0]), 
						(int) (point[2] * x2vec[1]*z + origin[1]));
			// draw xAxis part
			g.setColor(Color.green);
			g.drawLine(	(int)(point[2] * x2vec[0]*z + origin[0]),
						(int)(point[2] * x2vec[1]*z + origin[1]),
						(int)((point[0] * x0vec[0] + point[2] * x2vec[0])*z + origin[0]),
						(int)((point[0] * x0vec[1] + point[2] * x2vec[1])*z + origin[1]) );
			g.setColor(Color.magenta);
			// draw yAxis part
			g.drawLine(	(int) ((point[0] * x0vec[0] + point[2] * x2vec[0])*z + origin[0]), 
						(int) ((point[0] * x0vec[1] + point[2] * x2vec[1])*z + origin[1]), 
						(int) ((point[1] * x1vec[0] + point[0] * x0vec[0] + point[2] * x2vec[0])*z + origin[0]), 
						(int) ((point[1] * x1vec[1] + point[0] * x0vec[1] + point[2] * x2vec[1])*z + origin[1]) );		
			}
		g.setColor(c);
	}
	
	/**
	 * draws a polyline along the points contained in {@link #linepoints}
	 * <p>
	 * A points coordinates are translated to 2D using
	 * {@link #translatePoint_getX(double[])} and
	 * {@link #translatePoint_getY(double[])}
	 * @param g
	 */
	protected void paintPolyline(Graphics g){
		if (linepoints.length > 1) {

			Color c = g.getColor();
			g.setColor(colorLine);
			for (int i = 1; i < linepoints.length; i++) {
				g.drawLine(	translatePoint_getX(linepoints[i - 1]),
							translatePoint_getY(linepoints[i - 1]),
							translatePoint_getX(linepoints[i]),
							translatePoint_getY(linepoints[i]));
			}
//			g.drawLine(	translatePoint_getX(linepoints[linepoints.length - 1]),
//						translatePoint_getX(linepoints[linepoints.length - 1]),
//						translatePoint_getX(linepoints[0]),
//						translatePoint_getX(linepoints[0]));

			g.setColor(c);
		}
	}
	
	
	/**
	 * draws all Axes in their respective colors with a length of
	 * 2000px
	 * @param g
	 */
	protected void paintAxes(Graphics g){
		Color backup = g.getColor();
		final int size = 1000;
		//x-Axis
		g.setColor(colorX0Axis);
		g.drawLine((int)(origin[0] - x0vec[0]*size), (int)(origin[1] - x0vec[1]*size), (int)(origin[0] + x0vec[0]*size), (int)(origin[1] + x0vec[1]*size));
		//y-Axis
		g.setColor(colorX1Axis);
		g.drawLine((int)(origin[0] - x1vec[0]*size), (int)(origin[1] - x1vec[1]*size), (int)(origin[0] + x1vec[0]*size), (int)(origin[1] + x1vec[1]*size));
		//z-Axis
		g.setColor(colorX2Axis);
		g.drawLine((int)(origin[0] - x2vec[0]*size), (int)(origin[1] - x2vec[1]*size), (int)(origin[0] + x2vec[0]*size), (int)(origin[1] + x2vec[1]*size));
	
		g.setColor(backup);
	}
	
	/**
	 * draws a scale on each axis with value labels
	 * @param g
	 */
	protected void paintAxisScale(Graphics g){
		if(!axisScaleEnabled){
			return;
		}
		
		Color backup = g.getColor();
		Font f = g.getFont();
		g.setFont(f.deriveFont(10f));

		double x = 0;
		double y = 0;
		int i = -7;
		
		g.setColor(colorX0Axis);
		for(i = -7; i <= 7; i++){
			x = (origin[0]+i*x0vec[0]*128);
			y = (origin[1]+i*x0vec[1]*128);
			g.drawLine((int)(x+x0vec[1]*3.0), (int)(y-x0vec[0]*3.0), (int)(x-x0vec[1]*3.0), (int)(y+x0vec[0]*3.0));
			g.drawString("  "+i*128/currentZoom, (int)x, (int)y);
		}
		g.setColor(colorX1Axis);
		for(i = -7; i <= 7; i++){
			x = (origin[0]+i*x1vec[0]*128);
			y = (origin[1]+i*x1vec[1]*128);
			g.drawLine((int)(x+x1vec[1]*3.0), (int)(y-x1vec[0]*3.0), (int)(x-x1vec[1]*3.0), (int)(y+x1vec[0]*3.0));
			g.drawString("  "+i*128/currentZoom, (int)x, (int)y);
		}
		if(x2vec[0] != 0.0 && x2vec[1] != 0.0){
		g.setColor(colorX2Axis);
		for(i = -7; i <= 7; i++){
			x = (origin[0]+i*x2vec[0]*128);
			y = (origin[1]+i*x2vec[1]*128);
			g.drawLine((int)(x+x2vec[1]*3.0), (int)(y-x2vec[0]*3.0), (int)(x-x2vec[1]*3.0), (int)(y+x2vec[0]*3.0));
			g.drawString("  "+i*128/currentZoom, (int)x, (int)y);
		}
		}
		g.setColor(backup);
		g.setFont(f);
	}
	
	/**
	 * not used in this implementation. <br>
	 * draws a line from the origin to the cursor when dragging
	 * occurs.
	 * @param g
	 */
	protected void paintMouseDrag(Graphics g){
		int[] dragPoint = ((MouseControl)mouseControl).dragPoint;
		if(dragPoint.length != 0){
			g.drawLine(origin[0], origin[1], dragPoint[0], dragPoint[1]);
		}
	}
	
	/**
	 * Translates the coordinates of a 3-dimensional point to
	 * an X-coordinate in 2-dimensional space for display on this panel.
	 * <p><pre>
	 * x = (point[0]*x0vec[0] + point[1]*x1vec[0] + point[2]*x2vec[0])*z + origin[0]
	 * with z as the current zoom factor
	 * </pre></p>
	 * @param point double[] of size 3
	 * @return translated X coordinate as int
	 * @see #translatePoint_getY(double[])
	 */
	protected int translatePoint_getX(double[] point){
		double z = currentZoom;
		return (int) ((	point[0] * x0vec[0] + 
						point[1] * x1vec[0] + 
						point[2] * x2vec[0]) 
						* z + origin[0]);
	}
	
	/**
	 * Translates the coordinates of a 3-dimensional point to
	 * a Y-coordinate in 2-dimensional space for display on this panel.
	 * <p><pre>
	 * y = (point[0]*x0vec[1] + point[1]*x1vec[1] + point[2]*x2vec[1])*z + origin[1]
	 * with z as the current zoom factor
	 * </pre></p>
	 * @param point double[] of size 3
	 * @return translated Y coordinate as int
	 * @see #translatePoint_getX(double[])
	 */
	protected int translatePoint_getY(double[] point){
		double z = currentZoom;
		return (int) ((	point[0] * x0vec[1] + 
						point[1] * x1vec[1] + 
						point[2] * x2vec[1])
						* z + origin[1]);
	}
	
	/**
	 * Class implementing a MouseAdapter, that (when added as Mouse..Listeners)
	 * lets the user interact with the coordinate system.
	 * <p>
	 * This one provides following possibilities: <pre>
	 * Move the whole coordinate system (drag origin)
	 * Zoom in and out of the coordinate system
	 * Drag each axis to change its angle individually
	 * </pre>
	 * 
	 * @author David Hägele
	 *
	 */
	class MouseControl extends MouseAdapter {
		
		/** holds position of mouse at moment of press down */
		int[] atClick = new int[2];
		/** holds position of mouse at moment of release */
		int[] atRelease = new int[2];
		/** holds position of mouse at moments of dragging */
		int[] dragPoint = new int[0];
		
		/** stores the item that was selected at press down */
		int draggedItem;
		
		/** At Mouse Press: <pre>
		 * gets the selected item and stores it in draggedItem
		 * stores position of mouse in atClick
		 * initializes dragPoint with same position as atClick
		 * </pre>
		 */
		public void mousePressed(java.awt.event.MouseEvent e) {
			draggedItem = getDragableItemAt(e.getX(), e.getY());
			atClick[0] = e.getX();
			atClick[1] = e.getY();
			dragPoint = new int[] { e.getX(), e.getY() };
		};
		
		/** At Mouse Drag: <pre>
		 * when an axis was selected
		 *   rotates selected Axis to where mouse drags
		 * when origin or not an axis was selected
		 *   grabs coordinate system and moves it according to the drag
		 *   (actually moves origin by difference of klick and drag point)
		 *   
		 * repaints coordinate system
		 * </pre>
		 */
		public void mouseDragged(java.awt.event.MouseEvent e) {
			dragPoint[0] = e.getX();
			dragPoint[1] = e.getY();

			double[] vec = new double[] { 	dragPoint[0] - origin[0],
											dragPoint[1] - origin[1] };
			// making unit vector 
			double x = vec[0]; // backup
			vec[0] = vec[0] / sqrt(x * x + vec[1] * vec[1]);
			vec[1] = vec[1] / sqrt(x * x + vec[1] * vec[1]);
			switch (draggedItem) {
			case 0:
			case 1:
				origin[0] += dragPoint[0] - atClick[0];
				origin[1] += dragPoint[1] - atClick[1];
				atClick[0] = dragPoint[0];
				atClick[1] = dragPoint[1];
				break;
			case 2:
				x0vec[0] = vec[0];
				x0vec[1] = vec[1];
				break;
			case 3:
				x1vec[0] = vec[0];
				x1vec[1] = vec[1];
				break;
			case 4:
				x2vec[0] = vec[0];
				x2vec[1] = vec[1];
				break;
			case -2:
				x0vec[0] = -vec[0];
				x0vec[1] = -vec[1];
				break;
			case -3:
				x1vec[0] = -vec[0];
				x1vec[1] = -vec[1];
				break;
			case -4:
				x2vec[0] = -vec[0];
				x2vec[1] = -vec[1];
				break;
			default:
				break;
			}

			repaint();
		};
		
		/** At Mouse Release: <pre>
		 * assigns position of mouse to atRelease
		 * dragPoint is made an empty array of size 0
		 * </pre>
		 */
		public void mouseReleased(java.awt.event.MouseEvent e) {
			atRelease[0] = e.getX();
			atRelease[1] = e.getY();
			// es wird nichtmehr gedragged
			dragPoint = new int[0];
		};
		
		
		/** At Mouse Scroll: <pre>
		 * when scrolls up
		 *   zooms in once with center of panel as target
		 * when scrolls down
		 *   zooms out once with center of panel as target
		 * </pre>
		 */
		public void mouseWheelMoved(MouseWheelEvent e) {
			if(e.getPreciseWheelRotation() > 0){
				int x = origin[0] - getWidth()/2;
				int y = origin[1] - getHeight()/2;
				origin[0] -= x/2;
				origin[1] -= y/2;
				zoomOut();
				repaint();
			} else if(e.getPreciseWheelRotation() < 0){
				zoomIn();
				int x = origin[0] - getWidth()/2;
				int y = origin[1] - getHeight()/2;
				origin[0] += x;
				origin[1] += y;
				
				repaint();
			}
		}
		
		/**
		 * Returns an int representing the item that is at position x, y.
		 * @param x position (e.g. of a click)
		 * @param y position (e.g. of a click)
		 * @return <pre>
		 *   1 for origin
		 *   2 for positive side of x-axis -2 for negative side
		 *   3 for positive side of y-axis -3 for negative side
		 *   4 for positive side of z-axis -4 for negative side
		 *   0 if none of the above matches
		 * </pre>
		 */
		int getDragableItemAt(int x, int y){
			// check origin (if within box of 20px around origin)
			if(abs(origin[0] - x) < 10 &&  abs(origin[1] - y) < 10){
				return 1;
			// check Axes
			} else {
				// distance from origin to (x,y)
				double dist = sqrt((x-origin[0])*(x-origin[0]) + (y-origin[1])*(y-origin[1]));
				
				// extend axis vectors about dist, then check if that position is near to (x,y)
				if(abs(origin[0]+x0vec[0]*dist - x) < 7 && abs(origin[1]+x0vec[1]*dist - y) < 7 ){
					return 2;
				} else if(abs(origin[0]-x0vec[0]*dist - x) < 7 && abs(origin[1]-x0vec[1]*dist - y) < 7){
					return -2;
				} else if(abs(origin[0]+x1vec[0]*dist - x) < 7 && abs(origin[1]+x1vec[1]*dist - y) < 7){
					return 3;
				} else if(abs(origin[0]-x1vec[0]*dist - x) < 7 && abs(origin[1]-x1vec[1]*dist - y) < 7){
					return -3;
				} else if(abs(origin[0]+x2vec[0]*dist - x) < 7 && abs(origin[1]+x2vec[1]*dist - y) < 7){
					return 4;
				} else if(abs(origin[0]-x2vec[0]*dist - x) < 7 && abs(origin[1]-x2vec[1]*dist - y) < 7){
					return -4;
				} else {
					return 0;
				}
			}
		}
		
		
	}
	
	/** runs a demonstration of {@link CoordinateSystemDisplay} */
	public static void startDemo() {
			JFrame frame = new JFrame("3D Coordinate System - Demo");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setSize(800, 820);
			final CoordinateSystemDisplay display = new CoordinateSystemDisplay();
			frame.add(display);
			JMenuBar menuBar = new JMenuBar();
			frame.setJMenuBar(menuBar);
			
			final AtomicBoolean threadPaused = new AtomicBoolean(true);
			// thread that turns coordinate system
			Thread t = new Thread(){
				public void run() {
					while(true){
						if(threadPaused.get()){
							try {
								Thread.sleep(250);
							} catch (InterruptedException e) {
							}
						} else {
							display.rotate_Axis(0, 0.007);
//							display.rotate_Axis(1, -0.0007);
							display.rotate_Axis(2, 0.007);
							display.repaint();
							try {
								Thread.sleep(40);
							} catch (InterruptedException e) {
							}
						}
					}
				};
			};
			
			final JButton rotatebutton = new JButton();
			rotatebutton.setAction(new AbstractAction("Rotate") {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(threadPaused.get()){
						threadPaused.set(false);
						rotatebutton.setText("Stop");
					} else {
						threadPaused.set(true);
						rotatebutton.setText("Rotate");
					}
					
				}
			});
			
			final JButton showLinesbutton = new JButton();
			showLinesbutton.setAction(new AbstractAction("Show trace lines") {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					if(display.arePointTracesEnabled()){
						display.enablePointTraces(false);
						showLinesbutton.setText("Show trace lines");
					} else {
						display.enablePointTraces(true);
						showLinesbutton.setText("Hide trace lines");
					}
					display.repaint();
				}
			});
			
			final JButton disableZAxisbutton = new JButton();
			disableZAxisbutton.setAction(new AbstractAction("Disable z-Axis") {
				
				public void actionPerformed(ActionEvent arg0) {
					if(display.x2vec[0] == 0.0 && display.x2vec[1] == 0.0){
						display.x2vec[0] = -display.x0vec[1];
						display.x2vec[1] = display.x0vec[0];
						disableZAxisbutton.setText("Disable z-Axis");
					} else {
						display.x2vec[0] = 0.0;
						display.x2vec[1] = 0.0;
						disableZAxisbutton.setText("Enable z-Axis");
					}
					display.repaint();
				}
					
			});
			
			final JButton enableAAButton = new JButton();
			enableAAButton.setAction(new AbstractAction("Enable AA") {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(display.isAntialiasingEnabled()){
						enableAAButton.setText("Enable AA");
						display.enableAntiAliasing(false);
					} else {
						enableAAButton.setText("Disable AA");
						display.enableAntiAliasing(true);
					}
					display.repaint();
				}
			});
			
			final JButton showScaleButton = new JButton();
			showScaleButton.setAction(new AbstractAction("Show Scale") {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					display.enableAxisScales(!display.areAxisScalesEnabled());
					showScaleButton.setText(display.areAxisScalesEnabled() ? "Hide Scale":"Show Scale");
 					display.repaint();
				}
			});
			
			menuBar.add(new JButton(new AbstractAction("Reset") {
				@Override
				public void actionPerformed(ActionEvent e) {
					display.resetAxes();
					disableZAxisbutton.setText("Disable z-Axis");
					display.repaint();
				}
			}));
			menuBar.add(rotatebutton);
			menuBar.add(showLinesbutton);
			menuBar.add(disableZAxisbutton);
			menuBar.add(showScaleButton);
			menuBar.add(enableAAButton);
			display.add(new JLabel("you can drag axis and canvas, also zoom by scrolling ;)"));
			
			display.enableMouseControl(true);
			display.points = randomPoints(-1000,2000, 100);
			display.linepoints = new double[][]{{1,1,1},{100,1,1},{100,100,1},{1,100,1},{1,100,100},{1,1,100},{1,1,1}};
			
			frame.setVisible(true);
			t.start();
			display.resetAxes();
			display.resetOrigin();
		}


	/**
	 * builds an array of random points
	 */
	private static double[][] randomPoints(int from, int to, int numberOfPoints){
		double[][] points = new double[numberOfPoints][3];
		for(int i = 0; i < numberOfPoints; i++){
			double x = (Math.random()*(to-from))+from;
			double y = (Math.random()*(to-from))+from;
			double z = (Math.random()*(to-from))+from;
			points[i] = new double[]{x,y,z};
		}
		return points;
	}
	
	
	public static void main(String[] args) {
		startDemo();
	}
}
