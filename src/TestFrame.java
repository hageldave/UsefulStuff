import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class TestFrame extends JFrame {

	public TestFrame() {
		super();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(500, 350);
	}
	
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				JFrame frame = new TestFrame();
				
				JMenuBar menubar = new JMenuBar();
				frame.setJMenuBar(menubar);
				
				final JPanel panel1 = new JPanel();
				panel1.add(new JLabel("PANEL 1"));
				
				final PanelSwitchingPanel switchpanel = new PanelSwitchingPanel(panel1);
				frame.getContentPane().add(switchpanel);

				AbstractAction actionT = new AbstractAction("fromtop") {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						JPanel panel = new JPanel(new PhotoCornersLayout());
						JTabbedPane tabbedpane = new JTabbedPane();
						JPanel p1 = new JPanel();
						JPanel p2 = new JPanel();
						tabbedpane.addTab("tab1", p1);
						tabbedpane.addTab("tab2", p2);
						panel.add(tabbedpane,"topleft(0.5,0)bottomright(1.0,1.0)");
						switchpanel.switchPanel(panel, switchpanel.TOP, 350);
					}
					
				};
				AbstractAction actionL = new AbstractAction("fromleft") {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						JPanel panel = new JPanel();
						panel.add(new JButton("" + Math.random()));
						switchpanel.switchPanel(panel, switchpanel.LEFT, 150);
					}
					
				};
				AbstractAction actionR = new AbstractAction("fromright") {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						JPanel panel = new JPanel();
						panel.add(new JCheckBox("" + Math.random()));
						switchpanel.switchPanel(panel, switchpanel.RIGTH, 350);
					}
					
				};
				AbstractAction actionB = new AbstractAction("frombottom") {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						JPanel panel = new JPanel();
						panel.add(new JTextArea("Zahlen werden allgemein in einem Stellenwertsystem\n "
								+ "(Positionssystem, polyadischen Zahlensystem, b-adischen Zahlensystem)\n "
								+ "als Folge von Ziffern dargestellt. Die Wertigkeit einer Ziffer hängt\n "
								+ "von der Stelle ab, an der die Ziffer steht. Man unterscheidet zwischen \n"
								+ "ganzen Zahlen (Zahlen ohne Komma)  und gebrochenen Zahlen \n(Zahlen mit einem Komma)."));
						switchpanel.switchPanel(panel, switchpanel.BOTTOM, 350);
					}
					
				};
				
				JMenu menu = new JMenu("Switch it!");

				menu.add(new JMenuItem(actionL));
				menu.add(new JMenuItem(actionR));
				menu.add(new JMenuItem(actionT));
				menu.add(new JMenuItem(actionB));
				
				menubar.add(menu);
				frame.setVisible(true);
				
			}
		});
		
	}
}
