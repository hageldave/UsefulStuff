
/*
 * ProgrammersLayout.java
 *
 * Copyright 2013 David Haegele
 *
 * This class is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This class is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.HashMap;
import java.util.Map;

/**
 * This Layout allows laying out containers so their children's dimensions and
 * locations scale relative to the containers size. E.g. layout a JPanel that
 * contains a JButton so that the JButton always is half as wide as the JPanel
 * and always located horizontally centered in the lower half of the JPanel.
 * ProgrammersLayout also allows complete or partial absolute values for size and
 * location.
 * <p>
 * Notice that this Manager wont take care of possibly overlapping components. 
 * Design your layout well so this will not happen.
 * <p>
 * To assign layout constraints to a Component of a Container using the
 * ProgrammersLayout, you need to use the {@link Container#add(Component, Object)}
 * Method when adding a Component where you pass your layout constraints in a
 * String. <br>
 * E.g.:
 * <code>container.add(new JButton("B1"), "dim(1/4, 1/2) xLoc(1/2, center) yLoc(2/3, top)")</code>
 * </p>
 * <p>
 * <b>How To: Constraints Expressions</b>
 * </p>
 * <p>
 * A constraint String consists of 3 parts, <i>dim(...) xLoc(...) yLoc(...)</i>.
 * The order of those parts is arbitrary.
 * <p>
 * <ul>
 * <li>
 * dim(W, H) defines constraints for the components dimension <br>
 * <ul>
 * <li>
 * <b>W</b> determines the width and can be a fraction, float or integer (e.g.
 * 1/3, 0.5 or 100). <br>
 * A fraction or float value defines the width relative to to the parents width
 * (e.g. 1/3 -> one third of the parents width) <br>
 * An integer value defines an absolute width that is not affected by the
 * parents width.</li><br>
 * <li>
 * <b>H</b> determines the height and works in the same way as W.</li>
 * </ul>
 * <br>
 * <li>
 * xLoc(X, A) defines constraints for the components horizontal location <br>
 * <ul>
 * <li>
 * <b>X</b> determines a horizontal location and can be fraction, float or
 * integer (e.g. 1/3, 0.5 or 100). <br>
 * A fraction or float value defines the location relative to the parents width
 * (e.g. 0.5 -> half of parents width, means horizontal center) <br>
 * An integer value defines an absolute horizontal location that is not affected
 * by the parents width, which makes the component stay in place (horizontally).
 * </li><br>
 * <li>
 * <b>A</b> determines how the component is aligned to X. <br>
 * A can be one one of <i>left, center, right</i>. <br>
 * - left means the component is left aligned <br>
 * - center means X is in the middle of the component <br>
 * - right means the component is right aligned</li>
 * </ul>
 * <br>
 * <li>
 * yLoc(Y, B) defines constraints for the components vertical location <br>
 * <ul>
 * <li>
 * <b>Y</b> determines a vertical location and can be fraction, float or integer
 * (e.g. 1/3, 0.5 or 100). <br>
 * A fraction or float value defines the location relative to the parents height
 * (e.g. 0.5 -> half of parents height, means vertical center) <br>
 * An integer value defines an absolute vertical location that is not affected
 * by the parents height, which makes the component stay in place (vertically).</li>
 * <br>
 * <li>
 * <b>B</b> determines how the component is aligned to Y. <br>
 * B can be one of <i>top, center, bottom</i>. <br>
 * - top means the component is top aligned <br>
 * - center means Y is in the middle of the component <br>
 * - bottom means the component is bottom aligned</li><br>
 * </ul>
 * </ul>
 * </p>
 * <p>
 * <i><b>Examples:</b></i><br>
 * "dim(100, 20) xloc(0, left) yloc(0, top)" <br>
 * this places the components top left corner at point (0|0) statically. The
 * size of the component will be widht=100 height=20 at all times. <br>
 * <br>
 * "dim(100, 1/4) xloc(0, left) yloc(0, top)" <br>
 * will define the components width to be 100 at all times and its height to be
 * 0.25 times the parents height. When the parent resizes the components height
 * will resize relative to it. <br>
 * <br>
 * "dim(100, 0.25) xloc(1.0, right) yloc(0, top)" <br>
 * this xLoc definition makes the component stick to the right side of its
 * parent <br>
 * <br>
 * "dim(100, 0.25) xloc(1/2, center) yloc(0.5, center)" <br>
 * this xLoc and yLoc definition places the component in the center of its
 * parent perfectly. <br>
 * <br>
 * "xLoc(1/2,CENTER)dim(100,0.25)Yloc(0.5,center)" <br>
 * exactly the same as before, works as well.
 * 
 * 
 * @author David Hägele
 * 
 */
public class ProgrammersLayout implements LayoutManager2 {
	
	protected Map<Component, Constraint> constraintsMap = new HashMap<>();
	

	@Override
	@Deprecated
	public void addLayoutComponent(String name, Component comp) {
		// Do nothing
	}

	@Override
	public void layoutContainer(Container parent) {
		synchronized (parent.getTreeLock()) {
			for(Component c: parent.getComponents()){
				layoutComponent(parent, c);
			}
		}
	}
	
	/** 
	 * lays out the specified component
	 * @param parent containing the component
	 * @param child component to be laid out (sized and positioned)
	 */
	private void layoutComponent(Container parent, Component child){
		float width = parent.getWidth();
		float height = parent.getHeight();
		Constraint p = constraintsMap.get(child);
		if(p==null){
			child.doLayout();
			return;
		}
		
		float f1 = p.width;
		float f2 = p.height;
		if(p.isRelativeWidth){
			f1 *= width;
		}
		if(p.isRelativeHeight){
			f2 *= height;
		}
		child.setSize((int)f1, (int)f2);
		
		f1 = p.x;
		f2 = p.y;
		if(p.isRelativeX){
			f1 *= width;
		}
		if(p.isRelativeY){
			f2 *= height;
		}
		
		setPosition(child, f1, f2, p.xAlign, p.yAlign);
	}
	
	/** 
	 * sets the position of the specified component according to
	 * the passed arguments.
	 * @param c component to be positioned
	 * @param x absolute X position
	 * @param y absolute Y position
	 * @param xAlign components horizontal alignment
	 * @param yAlign components vertical alignment
	 */
	private void setPosition(Component c, float x, float y, 
			ProgrammersLayout.Constraint.XAlignment xAlign, 
			ProgrammersLayout.Constraint.YAlignment yAlign){
		float w = c.getWidth();
		float h = c.getHeight();
		float xPos;
		float yPos;
		
		switch (xAlign) {
		case center:
			xPos = x - w/2;
			break;
		case right:
			xPos = x - w;
			break;
		default:
			xPos = x;
			break;
		}
		switch (yAlign) {
		case center:
			yPos = y - h/2;
			break;
		case bottom:
			yPos = y - h;
			break;
		default:
			yPos = y;
			break;
		}
		
		c.setLocation((int)xPos, (int)yPos);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		float parentsWidth = 1;
		float parentsHeight = 1;
		synchronized (parent.getTreeLock()) {
			// FIXME: ugly, badly structured, not very readable
			for(Component c: parent.getComponents()){
				Dimension minDim = c.getMinimumSize();
				float candidateParentW = 1;
				float candidateParentH = 1;
				Constraint p = constraintsMap.get(c);
				if(p!=null){
					float cW = minDim.width; // components minimum width
					float cH = minDim.height; // components minimum height
					
					// BEGIN WIDTH PART
					if (!p.isRelativeWidth) {
						// comp has a static width
						cW = p.width; // minWidth does not apply anymore
						if (p.isRelativeX) {
							// comp has relative location
							switch (p.xAlign) {
							case right:
								candidateParentW = cW / p.x;
								break;
							case left:
								candidateParentW = cW / (1 - p.x);
								break;
							default:
								float w1 = (cW / 2) / p.x;
								float w2 = (cW / 2) / (1 - p.x);
								candidateParentW = (w1 > w2 ? w1 : w2);
								break;
							}
						}
					} else {
						// comp has a relative width
						candidateParentW = cW / p.width;
					}
					if (!p.isRelativeX) {
						// absolute locations may require greater parents width
						switch (p.xAlign) {
						case right:
							candidateParentW = 
									(candidateParentW > p.x ? candidateParentW : p.x);
							break;
						case left:
							candidateParentW = 
									(candidateParentW > p.x + cW ? candidateParentW : p.x + cW);
							break;
						default:
							candidateParentW = 
									(candidateParentW > p.x + (cW / 2) ? 
											candidateParentW : p.x + (cW / 2));
							break;
						}
					}
					// END WIDTH PART

					// BEGIN HEIGTH PART
					if (!p.isRelativeHeight) {
						cH = p.height;
						if (p.isRelativeY) {
							switch (p.yAlign) {
							case bottom:
								candidateParentH = cH / p.y;
								break;
							case top:
								candidateParentH = cH / (1 - p.y);
								break;
							default:
								float h1 = (cH / 2) / p.y;
								float h2 = (cH / 2) / (1 - p.y);
								candidateParentW = (h1 > h2 ? h1 : h2);
								break;
							}
						}
					} else {
						candidateParentH = cH / p.height;
					}
					if (!p.isRelativeHeight) {
						switch (p.yAlign) {
						case bottom:
							candidateParentH = (candidateParentH > p.y ? 
									candidateParentH : p.y);
							break;
						case top:
							candidateParentH = (candidateParentH > p.y + cH ? 
									candidateParentH : p.y + cH);
							break;
						default:
							candidateParentH = (candidateParentH > p.y + (cH / 2) ? 
									candidateParentH : p.y + (cH / 2));
							break;
						}
					}
					// END HEIGTH PART

				}
				parentsWidth = (parentsWidth > candidateParentW ? 
						parentsWidth : candidateParentW);
				parentsHeight = (parentsHeight > candidateParentH ? 
						parentsHeight : candidateParentH);
			}
		
		}
		return new Dimension(
				(int)(parentsWidth>(int)parentsWidth? parentsWidth+1:parentsWidth), 
				(int)(parentsHeight>(int)parentsHeight? parentsHeight+1:parentsHeight));
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		float parentsWidth = 1;
		float parentsHeight = 1;
		synchronized (parent.getTreeLock()) {
			// FIXME: ugly, badly structured, not very readable
			for(Component c: parent.getComponents()){
				Dimension prefDim = c.getPreferredSize();
				float candidateParentW = 1;
				float candidateParentH = 1;
				Constraint p = constraintsMap.get(c);
				if(p!=null){
					float cW = prefDim.width; // components preferred width
					float cH = prefDim.height; // components preferred height
					
					// BEGIN WIDTH PART
					if (!p.isRelativeWidth) {
						// comp has a static width
						cW = p.width; // prefWidth does not apply anymore
						if (p.isRelativeX) {
							// comp has relative location
							switch (p.xAlign) {
							case right:
								candidateParentW = cW / p.x;
								break;
							case left:
								candidateParentW = cW / (1 - p.x);
								break;
							default:
								float w1 = (cW / 2) / p.x;
								float w2 = (cW / 2) / (1 - p.x);
								candidateParentW = (w1 > w2 ? w1 : w2);
								break;
							}
						}
					} else {
						// comp has a relative width
						candidateParentW = cW / p.width;
					}
					if (!p.isRelativeX) {
						// absolute locations may require greater parents width
						switch (p.xAlign) {
						case right:
							candidateParentW = 
									(candidateParentW > p.x ? candidateParentW : p.x);
							break;
						case left:
							candidateParentW = 
									(candidateParentW > p.x + cW ? candidateParentW : p.x + cW);
							break;
						default:
							candidateParentW = 
									(candidateParentW > p.x + (cW / 2) ? candidateParentW : 
									p.x + (cW / 2));
							break;
						}
					}
					// END WIDTH PART

					// BEGIN HEIGTH PART
					if (!p.isRelativeHeight) {
						cH = p.height;
						if (p.isRelativeY) {
							switch (p.yAlign) {
							case bottom:
								candidateParentH = cH / p.y;
								break;
							case top:
								candidateParentH = cH / (1 - p.y);
								break;
							default:
								float h1 = (cH / 2) / p.y;
								float h2 = (cH / 2) / (1 - p.y);
								candidateParentW = (h1 > h2 ? h1 : h2);
								break;
							}
						}
					} else {
						candidateParentH = cH / p.height;
					}
					if (!p.isRelativeHeight) {
						switch (p.yAlign) {
						case bottom:
							candidateParentH = (candidateParentH > p.y ? 
									candidateParentH : p.y);
							break;
						case top:
							candidateParentH = (candidateParentH > p.y + cH ? 
									candidateParentH : p.y + cH);
							break;
						default:
							candidateParentH = (candidateParentH > p.y + (cH / 2) ? 
											candidateParentH : p.y + (cH / 2));
							break;
						}
					}
					// END HEIGTH PART

				}
				parentsWidth = (parentsWidth > candidateParentW ? 
						parentsWidth : candidateParentW);
				parentsHeight = (parentsHeight > candidateParentH ? 
						parentsHeight : candidateParentH);
			}
		
		}
		return new Dimension(
				(int)(parentsWidth>(int)parentsWidth? parentsWidth+1:parentsWidth), 
				(int)(parentsHeight>(int)parentsHeight? parentsHeight+1:parentsHeight));	
	}

	@Override
	public void removeLayoutComponent(Component comp) {
		constraintsMap.remove(comp);
	}

	/**
	 * Adds a layout component with constraints passed
	 * as String.
	 * <p>
	 * <b>How To: Constraints Expressions</b>
	 * </p>
	 * <p>
	 * A constraint String consists of 3 parts, <i>dim(...) xLoc(...)
	 * yLoc(...)</i>. The order of those parts is arbitrary.
	 * <p>
	 * <ul>
	 * <li>
	 * dim(W, H) defines constraints for the components dimension <br>
	 * <ul>
	 * <li>
	 * <b>W</b> determines the width and can be a fraction, float or integer
	 * (e.g. 1/3, 0.5 or 100). <br>
	 * A fraction or float value defines the width relative to to the parents
	 * width (e.g. 1/3 -> one third of the parents width) <br>
	 * An integer value defines an absolute width that is not affected by the
	 * parents width.</li><br>
	 * <li>
	 * <b>H</b> determines the height and works in the same way as w.</li>
	 * </ul>
	 * <br>
	 * <li>
	 * xLoc(X, A) defines constraints for the components horizontal location <br>
	 * <ul>
	 * <li>
	 * <b>X</b> determines a horizontal location and can be fraction, float or
	 * integer (e.g. 1/3, 0.5 or 100). <br>
	 * A fraction or float value defines the location relative to the parents
	 * width (e.g. 0.5 -> half of parents width, means horizontal center) <br>
	 * An integer value defines an absolute horizontal location that is not
	 * affected by the parents width, which makes the component stay in place
	 * (horizontally).</li><br>
	 * <li>
	 * <b>A</b> determines how the component is aligned to X. <br>
	 * A can be one one of <i>left, center, right</i>. <br>
	 * - left means the component is left aligned <br>
	 * - center means X is in the middle of the component <br>
	 * - right means the component is right aligned</li>
	 * </ul>
	 * <br>
	 * <li>
	 * yLoc(Y, B) defines constraints for the components vertical location <br>
	 * <ul>
	 * <li>
	 * <b>Y</b> determines a vertical location and can be fraction, float or
	 * integer (e.g. 1/3, 0.5 or 100). <br>
	 * A fraction or float value defines the location relative to the parents
	 * height (e.g. 0.5 -> half of parents height, means vertical center) <br>
	 * An integer value defines an absolute vertical location that is not
	 * affected by the parents height, which makes the component stay in place
	 * (vertically).</li><br>
	 * <li>
	 * <b>B</b> determines how the component is aligned to Y. <br>
	 * B can be one of <i>top, center, bottom</i>. <br>
	 * - top means the component is top aligned <br>
	 * - center means Y is in the middle of the component <br>
	 * - bottom means the component is bottom aligned</li><br>
	 * </ul>
	 * </ul>
	 * </p>
	 */
	public void addLayoutComponent(Component comp, Object constraints) {
		Constraint p = parseConstraints((String) constraints);
		constraintsMap.put(comp, p);
	}

	@Override
	@Deprecated
	public float getLayoutAlignmentX(Container target) {
		return 0;
	}

	@Override
	@Deprecated
	public float getLayoutAlignmentY(Container target) {
		return 0;
	}

	@Override
	@Deprecated
	public void invalidateLayout(Container target) {
	}

	@Override
	public Dimension maximumLayoutSize(Container parent) {
		float parentsWidth = 1000000;
		float parentsHeight = 1000000;
		
		synchronized (parent.getTreeLock()) {
			for(Component c: parent.getComponents()){
				Constraint p = constraintsMap.get(c);
				if(p==null){continue;}
				
				/* only constraints with absolute location and 
				 * relative dimension can limit the max size. 
				 * Also components need to grow in opposite 
				 * direction as parent expands, to exceed the 
				 * parents boundaries */
				if(!p.isRelativeX){
					if(p.isRelativeWidth){
						switch (p.xAlign) {
						case right:
							parentsWidth = parentsWidth < p.x / p.width ? 
									parentsWidth : p.x / p.width;
							break;
						case center:
							parentsWidth = parentsWidth < p.x / (p.width/2) ? 
									parentsWidth : p.x / (p.width/2);
							break;
						default:
							break;
						}
					}
				}
				
				if(!p.isRelativeY){
					if(p.isRelativeHeight){
						switch (p.yAlign) {
						case bottom:
							parentsHeight = parentsHeight < p.y / p.height ? 
									parentsHeight : p.y / p.height;
							break;
						case center:
							parentsHeight = parentsHeight < p.y / (p.height/2) ? 
									parentsHeight : p.y / (p.height/2);
							break;
						default:
							break;
						}
					}
				}
			}
		}
		return new Dimension((int)parentsWidth, (int)parentsHeight);
	}
	
	private Constraint parseConstraints(String constraints){
		String dimString = constraints.toLowerCase().replace("dim(", "#");
		String xLocString = constraints.toLowerCase().replace("xloc(", "#");
		String yLocString = constraints.toLowerCase().replace("yloc(", "#");
		
		int i = dimString.indexOf('#');
		dimString = dimString.substring(i+1, dimString.indexOf(')', i));
		i = xLocString.indexOf('#');
		xLocString = xLocString.substring(i+1, xLocString.indexOf(')', i));
		i = yLocString.indexOf('#');
		yLocString = yLocString.substring(i+1, yLocString.indexOf(')', i));
		
		Constraint p = new Constraint();
		String[] dim = dimString.split(",");
		
		// get width
		p.isRelativeWidth = dim[0].contains("/") || dim[0].contains(".");
		if(dim[0].contains("/")){
			float f1 = Float.parseFloat(dim[0].trim().split("/")[0]);
			float f2 = Float.parseFloat(dim[0].trim().split("/")[1]);
			p.width = f1/f2;
		} else {
			p.width = Float.parseFloat(dim[0].trim());
		}
		
		// get height
		p.isRelativeHeight = dim[1].contains("/") || dim[1].contains(".");
		if(dim[1].contains("/")){
			float f1 = Float.parseFloat(dim[1].trim().split("/")[0]);
			float f2 = Float.parseFloat(dim[1].trim().split("/")[1]);
			p.height = f1/f2;
		} else {
			p.height = Float.parseFloat(dim[1].trim());
		}
		
		// get X pos
		String[] xLoc = xLocString.split(",");
		p.isRelativeX = xLoc[0].contains("/") || xLoc[0].contains(".");
		if(xLoc[0].contains("/")){
			float f1 = Float.parseFloat(xLoc[0].trim().split("/")[0]);
			float f2 = Float.parseFloat(xLoc[0].trim().split("/")[1]);
			p.x = f1/f2;
		} else {
			p.x = Float.parseFloat(xLoc[0].trim());
		}
		p.xAlign = ProgrammersLayout.Constraint.XAlignment.valueOf(xLoc[1].trim());
		
		// get y pos
		String[] yLoc = yLocString.split(",");
		p.isRelativeY = yLoc[0].contains("/") || yLoc[0].contains(".");
		if (yLoc[0].contains("/")) {
			float f1 = Float.parseFloat(yLoc[0].trim().split("/")[0]);
			float f2 = Float.parseFloat(yLoc[0].trim().split("/")[1]);
			p.y = f1 / f2;
		} else {
			p.y = Float.parseFloat(yLoc[0].trim());
		}
		p.yAlign = ProgrammersLayout.Constraint.YAlignment
				.valueOf(yLoc[1].trim());
		
		return p;
	}
	
	
	
	protected static class Constraint {
		float width;
		boolean isRelativeWidth;
		float height;
		boolean isRelativeHeight;
		
		float x;
		boolean isRelativeX;
		XAlignment xAlign;
		float y;
		boolean isRelativeY;
		YAlignment yAlign;
		
		@Override
		public String toString() {
			String W = (isRelativeWidth?  String.valueOf(width):String.valueOf((int)width));
			String H = (isRelativeHeight?  String.valueOf(height):String.valueOf((int)height));
			String X = (isRelativeX?  String.valueOf(x):String.valueOf((int)x));
			String Y = (isRelativeY?  String.valueOf(y):String.valueOf((int)y));
			
			return "dim("+W+", "+H+") xloc("+X+", "+xAlign+") yloc("+Y+", "+yAlign+")";  
		}
		
		protected static enum XAlignment {
			center,left,right
		}
		protected static enum YAlignment {
			center,top,bottom
		}
		
	}

}
