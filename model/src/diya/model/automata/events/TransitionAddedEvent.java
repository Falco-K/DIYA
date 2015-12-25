package diya.model.automata.events;

import diya.model.automata.components.Transition;

public class TransitionAddedEvent extends AutomatonEvent{

	Transition transition;
	
	public TransitionAddedEvent(Transition transition) {
		super(AutomatonEventType.TransitionAdded);

		this.transition = transition;
	}
	
	public Transition getTransition(){
		return transition;
	}

}
