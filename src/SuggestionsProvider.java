import java.awt.Component;

/**
 * This abstract class is a framework for a class that makes input based suggestions
 * and provides them to some {@link Component}. 
 * <p>
 * A typical providing cycle starts with an event that calls 
 * {@link #makeSuggestions(Object)} with the input to process as argument.
 * The method then creates the suggestions based on the input and calls 
 * {@link #sendSuggestionsToReceiver(Object[])} which will provide the 
 * created suggestions to the receiver.
 * 
 * @author David Haegele
 *
 * @param <S> class that the suggestions are instances of
 * @param <C> class of the receiver {@link Component}
 */
public abstract class SuggestionsProvider<S, C extends Component> {
	/** Component that receives the suggestions */
	protected C receiver;
	
	/**
	 * Constructor that takes the receiving component as argument
	 * @param receiver {@link Component} that will receive the suggestions
	 */
	public SuggestionsProvider(C receiver) {
		this.receiver = receiver;
	}
	
	/**
	 * Returns the receiver of this SuggestionsProvider
	 * @return receiver component
	 */
	public C getReceiver(){
		return receiver;
	}
	
	/**
	 * Creates suggestions based on the input. <br>
	 * This method should also make sure that the created suggestions will be
	 * passed to {@link #sendSuggestionsToReceiver(Object[])}.
	 * @param input on which the suggestions are based.
	 */
	public abstract void makeSuggestions(Object input);
	
	/**
	 * Sends the specified suggestions to the receiver component. <br>
	 * (or makes the receiver display the suggestions)
	 * @param suggestions
	 */
	public abstract void sendSuggestionsToReceiver(S[] suggestions);

}
