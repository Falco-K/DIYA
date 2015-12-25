package diya.model.automata.events;

import diya.model.automata.components.Transition;

public class TransitionRemovedEvent extends AutomatonEvent{

	Transition transition;
	
	public TransitionRemovedEvent(Transition transition) {
		super(AutomatonEventType.TransitionRemoved);
		
		this.transition = transition;
	}
	
	public Transition getTransition(){
		return transition;
	}
}
