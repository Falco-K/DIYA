package diya.model.automata.events;

import java.util.ArrayList;

import diya.model.automata.components.State;

public class StepDoneEvent extends AutomatonEvent {

	ArrayList<State> states;
	
	public StepDoneEvent(ArrayList<State> currentStates) {
		super(AutomatonEventType.StepDone);
		
		states = currentStates;
	}
	
	public ArrayList<State> getStates(){
		return states;
	}
}
