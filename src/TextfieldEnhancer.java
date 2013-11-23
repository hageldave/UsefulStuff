import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;

import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
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
//		JTextField suggestionsTextfield = new JTextField();
//		Suggester wordSuggester = new Suggester() {
//			
//			@Override
//			public String[] gatherSuggestions(final String input) {
//				// suggests words contained in the input
//				String[] suggestions = input.split(" ");
//				return suggestions;
//			}
//		};
//		wordSuggester.setBackgroundTask(true);
//		TextfieldEnhancer.enhanceWithSuggestions(suggestionsTextfield, wordSuggester);
//		
//		frame.getContentPane().setLayout(new BorderLayout());
//		frame.getContentPane().add(verifiedTextfield, BorderLayout.NORTH);
//		frame.getContentPane().add(suggestionsTextfield, BorderLayout.SOUTH);
//		frame.setVisible(true);
//	}
	
	/**
	 * Enables the specified JTextfield to show suggestions based on its input.
	 * <br>
	 * The suggestions are provided by the specified {@link Suggester} which
	 * generates them in its {@link Suggester#gatherSuggestions(String)}method.
	 * <br>
	 * The suggestions will be displayed via the Suggesters
	 * {@link Suggester#displaySuggestions(String[])}method, which by default
	 * displays a popup list below the textfield.
	 * @param textfield to be enhanced with suggestions
	 * @param suggester which provides the suggestions
	 * @return the specified JTextField.
	 */
	public static JTextField enhanceWithSuggestions(final JTextField textfield, final Suggester suggester){
		suggester.textfield = textfield;
		// add a documentlistener that triggers the suggestion making process.
		textfield.getDocument().addDocumentListener(new TextfieldListener(suggester.getID()) {
			
			@Override
			public void documentChanged(DocumentEvent e) {
				// when suggester is a backgroundtask -> execute in a SwingWorker
				if(suggester.isBackgroundTask()){
					SwingWorker<String[], Void> backgroundtask = new SwingWorker<String[], Void>(){
						@Override
						protected String[] doInBackground() throws Exception {
							return suggester.gatherSuggestions(textfield.getText());
						}
						@Override
						protected void done() {
							try {
								suggester.displaySuggestions(textfield, get());
							} catch (InterruptedException | ExecutionException e) {
								e.printStackTrace();
							}
						};
					};
					backgroundtask.execute();
				} else {
					suggester.displaySuggestions(textfield, suggester.gatherSuggestions(textfield.getText()));
				}
			}
		});
		return textfield;
	}
	
	/**
	 * Enables the specified JTextField to verify its input directly.
	 * <br>
	 * The verification of the input is done by the specified {@link Verifier}.
	 * <b
	 * @param textfield
	 * @param verifier
	 * @return
	 */
	public static JTextField enhanceWithVerification(final JTextField textfield, final Verifier verifier){
		textfield.getDocument().addDocumentListener(new TextfieldListener(verifier.getID()) {
			
			@Override
			public void documentChanged(DocumentEvent e) {
				if (verifier.isBackgroundTask()) {
					SwingWorker<Boolean, Void> backgroundtask = new SwingWorker<Boolean, Void>() {

						@Override
						protected Boolean doInBackground() throws Exception {
							return verifier.verifyInput(textfield.getText());
						}
						@Override
						protected void done() {
							try {
								boolean isvalid = get();
								verifier.showVerification(textfield, isvalid);
							} catch (InterruptedException | ExecutionException e) {
								e.printStackTrace();
							}
						}
					};
					backgroundtask.execute();
				} else {
					verifier.showVerification(textfield, verifier.verifyInput(textfield.getText()));
				}
			}
		});
		return textfield;
	}
	
	
	public static void removeSuggester(JTextField textfield, Suggester suggester){
		textfield.getDocument().removeDocumentListener(new TextfieldListener(suggester.getID()) {
			@Override
			public void documentChanged(DocumentEvent e) {}
		});
	}
	
	
	public static void removeVerifier(JTextField textfield, Verifier verifier){
		textfield.getDocument().removeDocumentListener(new TextfieldListener(verifier.getID()) {
			@Override
			public void documentChanged(DocumentEvent e) {}
		});
	}
	
	
	protected static abstract class Enhancement {
		private static int CURRENT_ID = 0;
		private String id;
		
		
		public Enhancement() {
			id=getIdPrefix()+getNewID();
		}
		
		public String getID() {
			return id;
		}
		
		protected abstract String getIdPrefix();
		
		private static synchronized int getNewID(){
			CURRENT_ID++;
			return CURRENT_ID;
		}
	}
	
	
	public static abstract class Suggester extends Enhancement{
		protected JTextField textfield = null; // TODO: textfield unabhängigkeit...
		
		/** minimum number of suggestions to trigger popup list */
		protected int minimumSuggestions = 1;
		/** maximum number of suggestions to trigger popup list */
		protected int maximumSuggestions = 10;
		
		protected boolean isBackgroundTask = false;	
		
		
		@Override
		protected String getIdPrefix() {
			return "suggester";
		}
		
		public boolean isBackgroundTask() {
			return isBackgroundTask;
		}

		public void setBackgroundTask(boolean isBackgroundTask) {
			this.isBackgroundTask = isBackgroundTask;
		}

		public void displaySuggestions(final JTextField textfield, String[] suggestions){
			if(textfield != null){
				if (suggestions.length <= maximumSuggestions
						&& suggestions.length >= minimumSuggestions) {
					JPopupMenu popup = new JPopupMenu();
					for (final String text : suggestions) {
						AbstractAction action = new AbstractAction(text) {

							@Override
							public void actionPerformed(ActionEvent arg0) {
								textfield.setText(text);
							}
						};
						popup.add(action);
					}
					popup.show(textfield.getParent(), textfield.getX(),
							textfield.getY() + textfield.getHeight());
					// regain focus (popup.show() took the focus)
					textfield.requestFocus();
				}
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
		 * {@link #displaySuggestions(String[])}.
		 * @param input the suggestions are based on.
		 */
		public abstract String[] gatherSuggestions(String input);
		
	}
	
	
	public static abstract class Verifier extends Enhancement{
		protected Color colorInvalid = Color.decode("#FF520F");
		protected Color colorValid = Color.decode("#BAFFBA");
		
		protected boolean isBackgroundTask = false;	
		
		
		@Override
		protected String getIdPrefix() {
			return "verifier";
		}
		
		public boolean isBackgroundTask() {
			return isBackgroundTask;
		}

		public void setBackgroundTask(boolean isBackgroundTask) {
			this.isBackgroundTask = isBackgroundTask;
		}
		
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
		
		public void showVerification(JTextField textfield, boolean isValid){
			if(isValid){
				textfield.setBackground(getColorValid());
			} else {
				textfield.setBackground(getColorInvalid());
			}
		}

		protected abstract boolean verifyInput(String input);

	}
	
	
	private static abstract class TextfieldListener implements DocumentListener {
		private String ID;
		
		public TextfieldListener(String id) {
			ID = id;
		}
		
		public String getID() {
			return ID;
		}
		
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
		
		@Override
		public boolean equals(Object obj) {
			if(obj != null && obj instanceof TextfieldListener){
				return ((TextfieldListener)obj).ID.equals(ID);
			} else {
				return false;
			}
		}
		
	}
	
}
