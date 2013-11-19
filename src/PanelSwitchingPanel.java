

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.PopupMenu;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;

/**
 * This JPanel is a container for another JPanel. It can be used to create some
 * eyecandy because it can switch the displayed JPanel with another one showing
 * an animation that slides the currently displayed Panel out to one side while
 * sliding in the new Panel to display from the opposite side. For this to
 * happen use the 
 * <code>{@link PanelSwitchingPanel#switchPanel(JPanel, Side, int)}</code>
 * method.
 * <p>
 * The displayed JPanel will be displayed within the bounds of this 
 * PanelSwitchingPanel at the same size. So if you want to change the size or
 * location  of the displayed JPanel, use the PanelSwitchingPanels methods for
 * that, so display wont get messed up.
 * 
 * @author David Haegele
 * @version 1.1
 */
@SuppressWarnings("serial")
public class PanelSwitchingPanel extends JPanel {
	public static final Side LEFT = Side.left;
	public static final Side RIGHT = Side.right;
	public static final Side TOP = Side.top;
	public static final Side BOTTOM = Side.bottom;
	

	/** currently displayed Panel */
	private JPanel currentPanel;
	/** tells if a panel switching operation is in progress */
	private volatile Boolean isSwitching = false;
	
	/**
	 * Constructs a new PanelSwitchingPanel. The passed JPanel
	 * will be displayed by this panel.
	 * @param displaypanel initial JPanel to be displayed.
	 */
	public PanelSwitchingPanel(JPanel displaypanel) {
		this.currentPanel = displaypanel;
		super.setLayout(null);
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				currentPanel.setSize(getSize());
				currentPanel.setLocation(0, 0);
				validate();
			}
		});
		this._add(currentPanel);
	}
	
	/** 
	 * protected add method that works like {@link JPanel#add(Component)}.<br>
	 * This is a workarround to still offer the add method for in class usage
	 * or usage of subclasses.
	 * @param comp to be added.
	 * @return the component argument
	 */
	protected Component _add(Component comp){
		return super.add(comp);
	}
	
	/**
	 * tells if this Panel is currently in a switchPanel operation from a
	 * previous call of <code>{@link #switchPanel(JPanel, Side, int)}.</code>
	 * @return true if there is a switch operation in progress
	 */
	public boolean isSwitching(){
		return this.isSwitching;
	}
	
	/**
	 * switches the currently displayed JPanel with the specified one.
	 * This {@link PanelSwitchingPanel} will perform that showing a sliding
	 * animation where the old panel slides out and the new one in. The new
	 * panel will slide in from the specified side (use one of
	 * {@link PanelSwitchingPanel#TOP},{@link #LEFT},{@link #RIGTH},{@link #BOTTOM})
	 * The shown animation will last the specified time (values between 150
	 * and 500 ms look nice).
	 * 
	 * @param panel to replace the current panel
	 * @param from side from where the new panel will slide in
	 * @param animationTime time in ms for the slide animation to last
	 * @return the panel that got replaced
	 */
	public JPanel switchPanel(JPanel panel, Side from, int animationTime) {
		if (panel == null) {
			throw new NullPointerException(
					"Cannot switch Panel to null. Wanna try yourswitcher.remove(yourswitcher.getCurrentPanel()) instead?");
		}
		
		// if another switchPanel operation is in progress, wait until it has finished
		synchronized (isSwitching) {
		while(this.isSwitching == true){
			Thread.yield();
		}
		this.isSwitching = true;
		}

		this._add(panel);
		synchronized (this.getTreeLock()) {
			panel.setSize(getSize());
			panel.validate();
			switch (from) {
			case left:
				panel.setLocation(-getWidth(), 0);
				break;
			case right:
				panel.setLocation(getWidth(), 0);
				break;
			case top:
				panel.setLocation(0, -getHeight());
				break;
			default:
				panel.setLocation(0, getHeight());
				break;
			}
			SwitchThread switcher = new SwitchThread(this, currentPanel, panel, animationTime);
			switcher.start();
		}
		JPanel oldPanel = currentPanel;
		currentPanel = panel;
		return oldPanel;
	}
	
	
	public JPanel switchPanel(JPanel panel, Side from) {
		return switchPanel(panel, from, getGoodAnimationTime(from));
	}
	
	public JPanel switchPanelFast(JPanel panel, Side from) {
		return switchPanel(panel, from, getFastAnimationTime(from));
	}
	
	private int getFastAnimationTime(Side side){
		if(side == Side.left || side == Side.right){
			if(getWidth()>2700){
				return 400;
			} else {
				double x = 115 * (Math.exp(0.000436*getWidth()));
				return (int) Math.round(x);
			}
		} else {
			if(getHeight()>2700){
				return 400;
			} else {
				double x = 115 * (Math.exp(0.000436*getHeight()));
				return (int) Math.round(x);
			}
		}
	}
	
	private int getGoodAnimationTime(Side side){
		if(side == Side.left || side == Side.right){
			if(getWidth()>2700){
				return 400;
			} else {
				double x =70+ 120 * (Math.exp(0.000436*getWidth()));
				return (int) Math.round(x);
			}
		} else {
			if(getHeight()>2700){
				return 400;
			} else {
				double x =70+ 120 * (Math.exp(0.000436*getHeight()));
				return (int) Math.round(x);
			}
		}
	}
	
	/**
	 * replaces the current panel with the specified one. No animation to be
	 * shown when using this method, just straight replacing.
	 * @param panel to replace the current one
	 * @return the panel that got replaced
	 */
	public JPanel replacePanel(JPanel panel) {
		if (panel == null) {
			throw new NullPointerException(
					"Cannot replace current JPanel with null. Wanna try yourswitcher.remove(yourswitcher.getCurrentPanel()) instead?");
		} else {
			JPanel oldPanel = this.currentPanel;
			currentPanel = panel;
			this.remove(oldPanel);
			this._add(panel);
			panel.setLocation(0, 0);
			panel.setSize(this.getWidth(), this.getHeight());
			this.validate();
			return oldPanel;
		}
	}
	
	/**
	 * returns the JPanel that is currently displayed by this {@link PanelSwitchingPanel}
	 * @return JPanel that is currently displayed.
	 */
	public JPanel getCurrentPanel(){
		return this.currentPanel;
	}
	
	
	private static enum Side {
		left,
		right,
		top,
		bottom
	}
	
	
	private static class SwitchThread extends Thread {
		PanelSwitchingPanel parent;
		JPanel panel1;
		JPanel panel2;
		int timeToComplete;
		final int refreshCycleTime = 33; // = 30fps
		
		public SwitchThread(PanelSwitchingPanel parent, JPanel panel1, JPanel panel2, int timeToComplete) {
			this.parent = parent;
			this.panel1 = panel1;
			this.panel2 = panel2;
			this.timeToComplete = timeToComplete;
		}
		
		@Override
		public void run() {
			int steps = timeToComplete/refreshCycleTime;
			
			float x1 = panel1.getX();
			float y1 = panel1.getY();
			float x2 = panel2.getX();
			float y2 = panel2.getY();
			
			float stepX = (x1 - x2)/steps;
			float stepY = (y1 - y2)/steps;
			
			long timer;
			for(int i = 0; i < steps; i++){
				timer = System.currentTimeMillis();
						
				x1 += stepX;
				x2 += stepX;
				y1 += stepY;
				y2 += stepY;
				
				panel1.setLocation((int)x1, (int)y1);
				panel2.setLocation((int)x2, (int)y2);
				
				parent.repaint();
				// calc remaining sleep time to keep 18fps and store into timer
				timer = refreshCycleTime-(System.currentTimeMillis()-timer);
//				System.out.println(timer);
				if(timer > -1){
					try {
						Thread.sleep(timer);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			parent.remove(panel1);
			parent.isSwitching = false;
			panel2.setLocation(0, 0);
			parent.validate();
			parent.repaint();
		}
	}


	/** does nothing */
	@Override
	public final void setLayout(LayoutManager mgr) {
		// do nothing
		if(!Thread.currentThread().getStackTrace()[2].getMethodName().contains("<init>"))
			System.err.println("Cannot change Layout of a " + this.getClass().getCanonicalName() + ".");
	}

	/** does nothing */
	@Override
	public Component add(Component comp) {
		throw new UnsupportedOperationException("Cannot add Components to a " + this.getClass().getCanonicalName() + ".\nUse switchPanel() or replacePanel() intstead.");
	}

	/** does nothing */
	@Override
	public Component add(Component comp, int index) {
		throw new UnsupportedOperationException("Cannot add Components to a " + this.getClass().getCanonicalName() + ".\nUse switchPanel() or replacePanel() intstead.");
	}

	/** does nothing */
	@Override
	public void add(Component comp, Object constraints) {
		throw new UnsupportedOperationException("Cannot add Components to a " + this.getClass().getCanonicalName() + ".\nUse switchPanel() or replacePanel() intstead.");
	}

	/** does nothing */
	@Override
	public void add(Component comp, Object constraints, int index) {
		throw new UnsupportedOperationException("Cannot add Components to a " + this.getClass().getCanonicalName() + ".\nUse switchPanel() or replacePanel() intstead.");
	}

	/** does nothing */
	@Override
	public Component add(String name, Component comp) {
		throw new UnsupportedOperationException("Cannot add Components to a " + this.getClass().getCanonicalName() + ".\nUse switchPanel() or replacePanel() intstead.");
	}

	/** does nothing */
	@Override
	public void add(PopupMenu popup) {
		throw new UnsupportedOperationException("Cannot use PopupMenus with a " + this.getClass().getCanonicalName() + ".\nAdd the PopupMenu to the contained JPanel instead.");
	}
	
	
	

}
