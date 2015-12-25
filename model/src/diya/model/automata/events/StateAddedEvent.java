package diya.model.automata.events;

import diya.model.automata.components.State;

public class StateAddedEvent extends AutomatonEvent {

	State state;
	
	public StateAddedEvent(State state) {
		super(AutomatonEventType.StateAdded);
		
		this.state = state;
	}
	
	public State getState(){
		return state;
	}
}
