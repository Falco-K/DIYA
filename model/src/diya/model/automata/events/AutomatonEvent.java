package diya.model.automata.events;

public abstract class AutomatonEvent {
	
	AutomatonEventType type;
	
	public AutomatonEvent(AutomatonEventType type){
		this.type = type;
	}
	
	public AutomatonEventType getType(){
		return type;
	}
}
