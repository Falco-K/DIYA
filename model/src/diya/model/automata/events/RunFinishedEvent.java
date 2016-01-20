package diya.model.automata.events;

import java.util.HashSet;

import diya.model.automata.components.State;

public class RunFinishedEvent extends AutomatonEvent {

	boolean hasAccepted;
	HashSet<State> currentStates;
	
	public RunFinishedEvent(boolean hasAccepted, HashSet<State> currentStates) {
		super(AutomatonEventType.RunFinished);
		
		this.hasAccepted = hasAccepted;
		this.currentStates = currentStates;
	}
	
	public boolean hasAccepted(){
		return this.hasAccepted;
	}
	
	public HashSet<State> getStates(){
		return currentStates;
	}
}
