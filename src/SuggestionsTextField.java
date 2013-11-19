import java.awt.Component;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;


public class SuggestionsTextField extends JTextField {
	
	
	public static void main(String[] args) {
		TestFrame f = new TestFrame();
		f.getContentPane().setLayout(new PhotoCornersLayout());
		f.getContentPane().add(new SuggestionsTextField(),"topleft(0,0)bottomright(1.0,30)");
		f.setVisible(true);
	}
	
	public SuggestionsTextField() {
		final SuggestionsProvider<String> suggester = new SuggestionsProvider<String>(this) {
			
			
			@Override
			public void sendSuggestionsToReceiver(String[] suggestions) {
				JPopupMenu popup = new JPopupMenu();
				for(final String s : suggestions){
					AbstractAction a = new AbstractAction(s) {
						
						@Override
						public void actionPerformed(ActionEvent arg0) {
							((JTextField)receiver).setText(s);
						}
					};
					popup.add(a);
				}
				popup.show(receiver.getParent(), receiver.getX(), receiver.getY()+receiver.getHeight());
				receiver.requestFocus();
			}
			
			@Override
			public void makeSuggestions(Object input) {
				String s = (String) input;
				sendSuggestionsToReceiver(s.split(" "));
			}
		};
		
		this.getDocument().addDocumentListener(new DocumentListener() {
			
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				getSuggestions();
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				// TODO Auto-generated method stub
				getSuggestions();
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				getSuggestions();
			}
			
			void getSuggestions(){
				suggester.makeSuggestions(getText());
			}
		});
		
		
	}
	
	
	
}
