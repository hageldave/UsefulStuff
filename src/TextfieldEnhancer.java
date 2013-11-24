import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.concurrent.ExecutionException;

import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Factory Class that provides methods for enhancing existing (=initialized)
 * {@link JTextField}s.
 * <p>
 * This can come in handy when you want to establish usability features like
 * 'live inline validation' or 'autocomplete'. <br>
 * For 'live inline validation' you can use 
 * {@link #enhanceWithVerification(JTextField, Verifier)} and get a textfield
 * that will validate its input on the fly and in case of failure, for example,
 * paints the textfield red. <br>
 * For 'autocomplete' you can use 
 * {@link #enhanceWithSuggestions(JTextField, Suggester)} and get a textfield
 * that will display suggestions as you type based on the current input.
 * 
 * @author David Haegele
 * @version 1.0
 *
 */
public class TextfieldEnhancer {
	// Example
	public static void main(String[] args) {
		TestFrame frame = new TestFrame();
		
		JTextField verifiedTextfield = new JTextField();
		Verifier dateVerifier = new Verifier() {
			@Override
			public boolean verifyInput(String input) {
				// checks if is date of format DD.MM.YY or DD.MM.YYYY
				return input.matches("(([012]\\d)|(3[01]))\\.((0[1-9])|(1[012]))\\.(\\d{4}|\\d{2})");
			}
		};
		TextfieldEnhancer.enhanceWithVerification(verifiedTextfield, dateVerifier);
		
		JTextField suggestionsTextfield = new JTextField();
		Suggester wordSuggester = new Suggester() {
			
			@Override
			public String[] gatherSuggestions(final String input) {
				// suggests words contained in the input
				String[] suggestions = input.split(" ");
				return suggestions;
			}
		};
		wordSuggester.setBackgroundTask(true);
		TextfieldEnhancer.enhanceWithSuggestions(suggestionsTextfield, wordSuggester);
		
		frame.getContentPane().setLayout(new java.awt.BorderLayout());
		frame.getContentPane().add(verifiedTextfield, java.awt.BorderLayout.NORTH);
		frame.getContentPane().add(suggestionsTextfield, java.awt.BorderLayout.SOUTH);
		frame.setVisible(true);
	}
	
	/**
	 * Enables the specified JTextfield to show suggestions based on its input.
	 * <br>
	 * The suggestions are provided by the specified {@link Suggester} which
	 * generates them in its {@link Suggester#gatherSuggestions(String)}method.
	 * <br>
	 * The suggestions will be displayed via the Suggesters
	 * {@link Suggester#displaySuggestions(JTextField, String[])}method, which by default
	 * displays a popup list below the textfield.
	 * @param textfield to be enhanced with suggestions
	 * @param suggester which provides the suggestions
	 * @return the specified JTextField
	 */
	public static JTextField enhanceWithSuggestions(final JTextField textfield, final Suggester suggester){
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
	 * <br>
	 * The verification result will be displayed by the Verifiers
	 * {@link Verifier#displayVerification(JTextField, boolean)}method, which by
	 * default changes the background color of the textfield to either 
	 * red or green.
	 * @param textfield to be enhanced with verification
	 * @param verifier which verifies the input
	 * @return the specified JTextField
	 */
	public static JTextField enhanceWithVerification(final JTextField textfield, final Verifier verifier){
		// add a documentlistener that triggers the verification process.
		textfield.getDocument().addDocumentListener(new TextfieldListener(verifier.getID()) {
			
			@Override
			public void documentChanged(DocumentEvent e) {
				// when verifier is a backgroundtask -> execute in a SwingWorker
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
								verifier.displayVerification(textfield, isvalid);
							} catch (InterruptedException | ExecutionException e) {
								e.printStackTrace();
							}
						}
					};
					backgroundtask.execute();
				} else {
					verifier.displayVerification(textfield, verifier.verifyInput(textfield.getText()));
				}
			}
		});
		return textfield;
	}
	
	
	/**
	 * Removes the suggestion enhancement provided by the specified suggester.
	 * @param textfield to remove the suggester from
	 * @param suggester to be removed
	 */
	public static void removeSuggester(JTextField textfield, Suggester suggester){
		/* create a dummy textfieldlistener with the suggesters ID to remove
		 * the actual textfieldlistener
		 */
		textfield.getDocument().removeDocumentListener(new TextfieldListener(suggester.getID()) {
			@Override
			public void documentChanged(DocumentEvent e) {}
		});
	}
	
	/**
	 * Removes the verification enhancement provided by the specified verifier.
	 * @param textfield to remove the verifier from
	 * @param verifier to be removed
	 */
	public static void removeVerifier(JTextField textfield, Verifier verifier){
		/* create a dummy textfieldlistener with the verifiers ID to remove
		 * the actual textfieldlistener
		 */
		textfield.getDocument().removeDocumentListener(new TextfieldListener(verifier.getID()) {
			@Override
			public void documentChanged(DocumentEvent e) {}
		});
	}
	
	/**
	 * Base Class for JTextField enhancements.
	 * Provides the getID() method.
	 * @author David Haegele
	 */
	protected static abstract class Enhancement {
		/** gets incremented everytime an object of this type is initialized */
		private static Integer CURRENT_ID = 0;
		/** ID of this Enhancement object. Is unique. */
		private final String id;
		
		/** Constructor. Automatically generates {@link #id} */
		public Enhancement() {
			id=getIdPrefix()+getNewID();
		}
		
		/**
		 * Returns this enhancements {@link #id}
		 * @return ID of this enhancement
		 */
		public String getID() {
			return id;
		}
		
		/**
		 * Returns the ID prefix for the implementing subclass.
		 * (should not be a number, otherwise uniqueness of id ceases)
		 * @return prefix for 
		 */
		protected abstract String getIdPrefix();
		
		/**
		 * atomically increments CURRENT_ID and returns it.
		 * @return CURRENT_ID
		 */
		private static int getNewID(){
			synchronized (CURRENT_ID) {
				CURRENT_ID++;
				return CURRENT_ID;
			}
		}
	}
	
	//TODO javadoc
	public static abstract class Suggester extends Enhancement {
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
		 * Gathers suggestions for the specified input.
		 * @param input the suggestions are based on
		 * @return gathered suggestions in a String[]
		 */
		public abstract String[] gatherSuggestions(String input);
		
	}
	
	//TODO javadoc
	public static abstract class Verifier extends Enhancement {
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
		
		public void displayVerification(JTextField textfield, boolean isValid){
			if(isValid){
				textfield.setBackground(getColorValid());
			} else {
				textfield.setBackground(getColorInvalid());
			}
		}

		public abstract boolean verifyInput(String input);

	}
	
	//TODO javadoc
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
