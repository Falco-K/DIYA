package diya.model.automata.events;

import diya.model.automata.components.Transition;

public class TransitionUpdatedEvent extends AutomatonEvent {

	Transition transition;
	
	public TransitionUpdatedEvent(Transition transition) {
		super(AutomatonEventType.TransitionUpdated);
		
		this.transition = transition;
	}
	
	public Transition getTransition(){
		return transition;
	}
}
