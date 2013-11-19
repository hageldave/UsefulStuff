import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * JTextfield that is able to show suggustions based on the text in it.
 * To provide those suggestions, the abstract method 
 * {@link #gatherSuggestions(Object, TextfieldSuggester)}
 * needs to be implemented.
 * @author David Haegele
 *
 */
public abstract class SuggestionsTextField extends JTextField {

	public static void main(String[] args) {
		TestFrame f = new TestFrame();
		f.getContentPane().setLayout(new PhotoCornersLayout());
		SuggestionsTextField textfield = new SuggestionsTextField() {

			@Override
			protected void gatherSuggestions(Object input, TextfieldSuggester suggester) {
				suggester.sendSuggestionsToReceiver(input.toString().split(" "));
			}
		};
		f.getContentPane().add(textfield, "topleft(0,0)bottomright(1.0,30)");
		f.setVisible(true);
	}

	public SuggestionsTextField() {
		final TextfieldSuggester suggester = new TextfieldSuggester(this);

		this.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				getSuggestions();
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				getSuggestions();
			}

			@Override
			public void changedUpdate(DocumentEvent arg0) {
				getSuggestions();
			}

			void getSuggestions() {
				suggester.makeSuggestions(getText());
			}
		});

	}
	
	protected void showSuggestions(String[] suggestions){
		if (suggestions.length <= 10 && suggestions.length > 0) {
			JPopupMenu popup = new JPopupMenu();
			for (final String s : suggestions) {
				AbstractAction a = new AbstractAction(s) {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						setText(s);
					}
				};
				popup.add(a);
			}
			popup.show(this.getParent(), this.getX(),
					this.getY() + this.getHeight());
			this.requestFocus();
		}
	}
	
	/**
	 * Gathers suggestions for the specified input and then calls
	 * {@link TextfieldSuggester#sendSuggestionsToReceiver(String[])}
	 * of the specified TextfieldSuggester.
	 * @param input the suggestions are based on.
	 * @param suggester to send the resulting suggestions.
	 */
	protected abstract void gatherSuggestions(Object input, TextfieldSuggester suggester);

	
	protected static class TextfieldSuggester extends
			SuggestionsProvider<String, SuggestionsTextField> {

		public TextfieldSuggester(SuggestionsTextField receiver) {
			super(receiver);
		}

		@Override
		public void makeSuggestions(Object input) {
			getReceiver().gatherSuggestions(input, this);
		}

		@Override
		public void sendSuggestionsToReceiver(String[] suggestions) {
			getReceiver().showSuggestions(suggestions);
		}
			
	}

}
