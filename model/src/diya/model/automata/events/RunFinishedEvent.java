package diya.model.automata.events;

public class RunFinishedEvent extends AutomatonEvent {

	boolean hasAccepted;
	
	public RunFinishedEvent(boolean hasAccepted) {
		super(AutomatonEventType.RunFinished);
		
		this.hasAccepted = hasAccepted;
	}
	
	public boolean hasAccepted(){
		return this.hasAccepted;
	}
}
