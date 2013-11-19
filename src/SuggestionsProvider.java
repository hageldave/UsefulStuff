import java.awt.Component;


public abstract class SuggestionsProvider<E> {
	
	Component receiver;
	
	public SuggestionsProvider(Component receiver) {
		setReceiver(receiver);
	}
	
	public void setReceiver(Component receiver){
		this.receiver = receiver;
	}
	
	public abstract void makeSuggestions(Object input);
	
	public abstract void sendSuggestionsToReceiver(E[] suggestions);

}
