import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class TextfieldEnhancer {
//	// Example
//	public static void main(String[] args) {
//		TestFrame frame = new TestFrame();
//		
//		JTextField verifiedTextfield = new JTextField();
//		Verifier dateVerifier = new Verifier() {
//			@Override
//			protected boolean verifyInput(String input) {
//				// checks if is date of format DD.MM.YY or DD.MM.YYYY
//				return input.matches("(([012]\\d)|(3[01]))\\.((0[1-9])|(1[012]))\\.(\\d{4}|\\d{2})");
//			}
//		};
//		TextfieldEnhancer.enhanceWithVerification(verifiedTextfield, dateVerifier);
//		
//		frame.getContentPane().setLayout(new BorderLayout());
//		frame.getContentPane().add(verifiedTextfield, BorderLayout.NORTH);
//		frame.setVisible(true);
//	}
	

	public static JTextField enhanceWithSuggestions(JTextField textfield, Suggester suggester){
		//TODO
		return textfield;
	}
	
	public static JTextField enhanceWithVerification(final JTextField textfield, final Verifier verifier){
		textfield.getDocument().addDocumentListener(new TextfieldListener() {
			
			@Override
			public void documentChanged(DocumentEvent e) {
				if(verifier.verifyInput(textfield.getText()) == true){
					textfield.setBackground(verifier.getColorValid());
				} else {
					textfield.setBackground(verifier.getColorInvalid());
				}
			}
		});
		return textfield;
	}
	
	
	
	public static abstract class Suggester {
		// TODO
	}
	
	
	public static abstract class Verifier {
		
		protected Color colorInvalid = Color.decode("#FF520F");
		protected Color colorValid = Color.decode("#BAFFBA");
		
		public Color getColorInvalid() {
			return colorInvalid;
		}

		public void setColorInvalid(Color colorInvalid) {
			this.colorInvalid = colorInvalid;
		}

		public Color getColorValid() {
			return colorValid;
		}

		public void setColorValid(Color colorValid) {
			this.colorValid = colorValid;
		}

		protected abstract boolean verifyInput(String input);

	}
	
	
	private static abstract class TextfieldListener implements DocumentListener {
		@Override
		public void changedUpdate(DocumentEvent e) {
			documentChanged(e);
		}
		@Override
		public void insertUpdate(DocumentEvent e) {
			documentChanged(e);
		}
		@Override
		public void removeUpdate(DocumentEvent e) {
			documentChanged(e);
		}
		
		public abstract void documentChanged(DocumentEvent e);
		
	}
	
}
