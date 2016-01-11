package diya.model.automata.events;

import diya.model.automata.components.Tape;

public class TapeUpdatedEvent extends AutomatonEvent{

	Tape tape;
	
	public TapeUpdatedEvent(Tape tape) {
		super(AutomatonEventType.TapeUpdated);

		this.tape = tape;
	}

	public Tape getTape(){
		return tape;
	}
}
