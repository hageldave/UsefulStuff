

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.PopupMenu;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;


public class PanelSwitchingPanel extends JPanel {
	public static final Side LEFT = Side.left;
	public static final Side RIGTH = Side.right;
	public static final Side TOP = Side.top;
	public static final Side BOTTOM = Side.bottom;
	

	private JPanel currentPanel;
	private volatile Boolean isSwitching = false;
	
	
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
	
	
	protected Component _add(Component comp){
		return super.add(comp);
	}
	
	
	public boolean isSwitching(){
		return this.isSwitching;
	}
	
	
	public JPanel switchPanel(JPanel panel, Side from, int speedInMs) {
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
			SwitchThread switcher = new SwitchThread(this, currentPanel, panel, speedInMs);
			switcher.start();
		}
		JPanel oldPanel = currentPanel;
		currentPanel = panel;
		return oldPanel;
	}
	
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
		final int refreshCycleTime = 56; // = 18fps
		
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
	public void setLayout(LayoutManager mgr) {
		// do nothing
		throw new UnsupportedOperationException("Cannot change Layout of a " + this.getClass().getCanonicalName() + ".");
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
