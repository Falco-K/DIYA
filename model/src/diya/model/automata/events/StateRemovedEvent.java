package diya.model.automata.events;

import diya.model.automata.components.State;

public class StateRemovedEvent extends AutomatonEvent{

	State state;
	
	public StateRemovedEvent(State state) {
		super(AutomatonEventType.StateRemoved);
		
		this.state = state;
	}
	
	public State getState(){
		return state;
	}
}
