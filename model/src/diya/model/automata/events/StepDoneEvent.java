package diya.model.automata.events;

import java.util.ArrayList;

import diya.model.automata.components.State;
import diya.model.automata.components.Transition;


public class StepDoneEvent extends AutomatonEvent {

	ArrayList<State> states;
	ArrayList<Transition> transitions;
	
	public StepDoneEvent(ArrayList<State> currentStates) {
		this(currentStates, null);
	}
	
	public StepDoneEvent(ArrayList<State> currentStates, ArrayList<Transition> transitions){
		super(AutomatonEventType.StepDone);
		
		this.states = currentStates;
		this.transitions = transitions;
	}
	
	public ArrayList<State> getStates(){
		return states;
	}
	
	public ArrayList<Transition> getTransitions(){
		return transitions;
	}
}
