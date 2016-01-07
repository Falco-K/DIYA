package diya.model.automata.events;

import java.util.ArrayList;
import java.util.HashSet;

import diya.model.automata.components.State;
import diya.model.automata.components.Transition;


public class StepDoneEvent extends AutomatonEvent {

	ArrayList<State> states;
	ArrayList<Transition> transitions;
	int stepNumber;

	public StepDoneEvent(HashSet<State> currentStates, ArrayList<Transition> transitions, int stepNumber){
		super(AutomatonEventType.StepDone);
		
		this.states = new ArrayList<State>();
		this.states.addAll(currentStates);
		this.transitions = transitions;
		this.stepNumber = stepNumber;
	}
	
	public ArrayList<State> getStates(){
		return states;
	}
	
	public ArrayList<Transition> getTransitions(){
		return transitions;
	}
	
	public int getStepNumber(){
		return stepNumber;
	}
}
