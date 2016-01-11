package diya.model.automata.events;

import diya.model.automata.components.State;

public class InvalidAutomatonEvent extends AutomatonEvent {

	State perpetrator;
	
	public InvalidAutomatonEvent(State perpetrator) {
		super(AutomatonEventType.InvalidAutomaton);
		
		this.perpetrator = perpetrator;
	}
	
	public State getPerpetrator(){
		return perpetrator;
	}
}
