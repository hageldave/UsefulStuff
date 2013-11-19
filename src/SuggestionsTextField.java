import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * JTextfield that is able to show suggustions based on the text input.
 * To provide those suggestions, the abstract method 
 * {@link #gatherSuggestions(Object, TextfieldSuggester)}
 * needs to be implemented. The suggestions are shown in a popup list
 * under the Textfield.
 * 
 * @author David Haegele
 * @version 1.1
 *
 */
@SuppressWarnings("serial")
public abstract class SuggestionsTextField extends JTextField {

//	// Exapmle
//	public static void main(String[] args) {
//		TestFrame f = new TestFrame();
//		f.getContentPane().setLayout(new PhotoCornersLayout());
//		SuggestionsTextField textfield = new SuggestionsTextField() {
//
//			@Override
//			protected void gatherSuggestions(String input, TextfieldSuggester suggester) {
//				// suggests words contained in the input
//				suggester.sendSuggestionsToReceiver(input.split(" "));
//			}
//		};
//		f.getContentPane().add(textfield, "topleft(0,0)bottomright(1.0,30)");
//		f.setVisible(true);
//	}
	
	/** minimum number of suggestions to trigger popup list */
	private int minimumSuggestions = 1;
	/** maximum number of suggestions to trigger popup list */
	private int maximumSuggestions = 10;

	/** Constructs a {@link SuggestionsTextField} */
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
	
	/**
	 * Displays the suggestions in a popup list under this
	 * {@link SuggestionsTextField}.
	 * @param suggestions that will be shown.
	 */
	protected void showSuggestions(String[] suggestions) {
		if (suggestions.length <= maximumSuggestions
				&& suggestions.length >= minimumSuggestions) {
			JPopupMenu popup = new JPopupMenu();
			for (final String text : suggestions) {
				AbstractAction action = new AbstractAction(text) {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						setText(text);
					}
				};
				popup.add(action);
			}
			popup.show(this.getParent(), this.getX(),
					this.getY() + this.getHeight());
			// regain focus (popup.show() took the focus)
			this.requestFocus();
		}
	}
	
	/**
	 * Returns the minimum number of available Suggestions to trigger
	 * displaying of them.
	 * @return minimum number of suggestions to trigger popup list.
	 */
	public int getMinimumSuggestions() {
		return minimumSuggestions;
	}

	/**
	 * Sets the minimum number of suggestions that need to be available
	 * to trigger the popup list for display. Default is 1.
	 * @param minimum number of suggestions to trigger popup list.
	 */
	public void setMinimumSuggestions(int minimum) {
		this.minimumSuggestions = minimum;
	}

	/**
	 * Returns the maximum number of allowed Suggestions to trigger
	 * displaying of them.
	 * @return maximum number of suggestions to trigger popup list.
	 */
	public int getMaximumSuggestions() {
		return maximumSuggestions;
	}

	/**
	 * Sets the maximum number of suggestions that are allowed
	 * to trigger the popup list for display. Default is 10.
	 * @param maximum number of suggestions to trigger popup list.
	 */
	public void setMaximumSuggestions(int maximum) {
		this.maximumSuggestions = maximum;
	}

	/**
	 * Gathers suggestions for the specified input and then calls
	 * {@link TextfieldSuggester#sendSuggestionsToReceiver(String[])}
	 * of the specified TextfieldSuggester.
	 * @param input the suggestions are based on.
	 * @param suggester to send the resulting suggestions.
	 */
	protected abstract void gatherSuggestions(String input, TextfieldSuggester suggester);

	/** 
	 * {@link SuggestionsProvider} implementation used by this 
	 * {@link SuggestionsTextField} 
	 */
	protected static class TextfieldSuggester extends
			SuggestionsProvider<String, SuggestionsTextField> {

		public TextfieldSuggester(SuggestionsTextField receiver) {
			super(receiver);
		}

		@Override
		public void makeSuggestions(Object input) {
			getReceiver().gatherSuggestions((String)input, this);
		}

		@Override
		public void sendSuggestionsToReceiver(String[] suggestions) {
			getReceiver().showSuggestions(suggestions);
		}		
	}

}
