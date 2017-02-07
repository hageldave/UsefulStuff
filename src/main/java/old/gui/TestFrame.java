package old.gui;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;


public class TestFrame extends JFrame {

	public TestFrame() {
		super();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setSize(1200, 350);
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
				switchpanel.addComponentListener(new ComponentAdapter() {
					
					@Override
					public void componentResized(ComponentEvent e) {
						System.out.println(""+switchpanel.getWidth() + "/"+switchpanel.getHeight());
					}
				});

				AbstractAction actionT = new AbstractAction("fromtop") {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						JPanel panel = new JPanel(new PhotoCornersLayout());
						JTabbedPane tabbedpane = new JTabbedPane();
						JScrollPane p1 = new JScrollPane();
						JPanel p2 = new JPanel();
						JList<String> list = new JList<>(
								"Liebe Leserinnen und Leser: 500 Millionen Menschen nutzen Wikipedia im Monat. Sie ist die Nr. 5 der am häufigsten besuchten Webseiten der Welt und hat Kosten wie jede andere Top-Seite: Server, Strom, Programme, Personal. Nur knapp 250 Angestellte arbeiten in der gemeinnützigen Organisation hinter Wikipedia. Wikipedia ist anders. Sie ist ein besonderer Ort, wie eine Bibliothek oder ein großer Park: Hier gehen wir alle hin. Hier lernen wir. Hier denken wir nach. Um Wikipedias Unabhängigkeit zu schützen, gibt es keine Werbung. Wir finanzieren uns durch Spenden von durchschnittlich 20 €. Heute bitten wir Sie um einen Beitrag. Wenn jeder nur einen kleinen Beitrag leisten würde, wäre unsere Spendenkampagne in einer Stunde vorüber. Finden Sie Wikipedia nützlich? Dann nehmen Sie sich 1 Minute Zeit, damit Wikipedia ein weiteres Jahr werbefrei und für uns alle da sein kann. Bitte helfen Sie mit. Vielen Dank!".split("er"));
						p1.setViewportView(list);
						tabbedpane.addTab("tab1", p1);
						tabbedpane.addTab("tab2", p2);
						panel.add(tabbedpane,"topleft(0.2,0)bottomright(1.0,1.0)");
						switchpanel.switchPanel(panel, switchpanel.TOP);
					}
					
				};
				AbstractAction actionL = new AbstractAction("fromleft") {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						JPanel panel = new JPanel();
						panel.add(new JButton("" + Math.random()));
						switchpanel.switchPanel(panel, switchpanel.LEFT, 200);
					}
					
				};
				AbstractAction actionR = new AbstractAction("fromright") {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						JPanel panel = new JPanel();
						panel.add(new JCheckBox("" + Math.random()));
						switchpanel.switchPanel(panel, switchpanel.RIGHT);
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
						switchpanel.switchPanelFast(panel, switchpanel.BOTTOM);
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
