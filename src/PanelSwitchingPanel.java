

import java.awt.LayoutManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JPanel;


public class PanelSwitchingPanel extends JPanel {
	public static final Side LEFT = Side.left;
	public static final Side RIGTH = Side.right;
	public static final Side TOP = Side.top;
	public static final Side BOTTOM = Side.bottom;
	

	private JPanel currentPanel;
	
	
	public PanelSwitchingPanel(JPanel displaypanel) {
		this.currentPanel = displaypanel;
		super.setLayout(null);
		this.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				currentPanel.setSize(getSize());
				currentPanel.setLocation(0, 0);
				currentPanel.doLayout();
			}
		});
		this.add(currentPanel);
	}
	
	@Override
	public void setLayout(LayoutManager mgr) {
		// do nothing
	}
	
	
	public JPanel switchPanel(JPanel panel, Side from, int speedInMs) {
		this.add(panel);
		synchronized (this.getTreeLock()) {

			panel.setSize(getSize());
			panel.doLayout();
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
	
	
	private static enum Side {
		left,
		right,
		top,
		bottom
	}
	
	
	private static class SwitchThread extends Thread {
		JPanel parent;
		JPanel panel1;
		JPanel panel2;
		int time;
		
		public SwitchThread(JPanel parent, JPanel panel1, JPanel panel2, int time) {
			this.parent = parent;
			this.panel1 = panel1;
			this.panel2 = panel2;
			this.time = time;
		}
		
		@Override
		public void run() {
			int steps = time/50; // time / 50ms (50ms = 20fps)
			
			float x1 = panel1.getX();
			float y1 = panel1.getY();
			float x2 = panel2.getX();
			float y2 = panel2.getY();
			
			float stepX = (x1 - x2)/steps;
			float stepY = (y1 - y2)/steps;
			for(int i = 0; i < steps; i++){ 
				x1 += stepX;
				x2 += stepX;
				y1 += stepY;
				y2 += stepY;
				
				panel1.setLocation((int)x1, (int)y1);
				panel2.setLocation((int)x2, (int)y2);
				
//				System.out.println("repaint " + x1 + " " + y1 + " | " + x2 + " " + y2);
				parent.repaint();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			parent.remove(panel1);
			panel2.setLocation(0, 0);
			panel2.doLayout();
			parent.repaint();
		}
	}
	
	
	

}
