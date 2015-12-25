package diya.model.automata.events;

import diya.model.automata.components.State;

public class StateUpdatedEvent extends AutomatonEvent {

	State state;
	
	public StateUpdatedEvent(State state) {
		super(AutomatonEventType.StateUpdated);
		this.state = state;
	}
	
	public State getState(){
		return state;
	}
}
