package diya.model.automata;

import diya.model.language.Alphabet;
import diya.model.language.Symbol;

import java.util.ArrayList;
import java.util.HashSet;

import diya.model.automata.components.InputTape;
import diya.model.automata.components.State;
import diya.model.automata.components.Transition;
import diya.model.automata.events.RunFinishedEvent;
import diya.model.automata.transitionRules.*;

public class FiniteStateMachine extends Automaton{

	ArrayList<SimpleTransitionRule> possibleRules;
	
	public FiniteStateMachine(int x, int y, Alphabet alphabet) {
		super(x, y, alphabet, new InputTape(x, y));
		
		possibleRules = new ArrayList<SimpleTransitionRule>();
	}

	@Override
	public boolean executeTransition(HashSet<State> currentStatesOut, ArrayList<Transition> transitionsOut) {
		
		Symbol nextSymbol = ((InputTape)getMainInputTape()).readSymbolMoveTape();

		if(nextSymbol == null){
			fireEvent(new RunFinishedEvent(hasAccepted()));
			return false;
		}
		
		//TODO: Validate Current States for next Transitions -> Check for bad nondeterminism!

		HashSet<State> oldStates = new HashSet<State>();
		oldStates.addAll(currentStatesOut);
		currentStatesOut.clear();

		for(State aState : oldStates){
			for(Transition aTransition : aState.getNextEdges(nextSymbol)){
				transitionsOut.add(aTransition);
				currentStatesOut.add(aTransition.getDestination());
			}
		}
		
		ArrayList<Transition> emptyWordTransitions = getEmptyWordTransitionChain();
		for(Transition aTransition : emptyWordTransitions){
			currentStatesOut.add(aTransition.getDestination());
			transitionsOut.add(aTransition);
		}

		return true;
	}

	@Override
	public boolean validate(State aState) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean getEmptyTransitionsAllowed(){
		return true;
	}
	
	public SimpleTransitionRule makeTransitionRule(String transition){
		for(SimpleTransitionRule aRule : possibleRules){
			if(aRule.toString().equals(transition)){
				return aRule;
			}
		}
		
		SimpleTransitionRule temp;
		if(transition != null && transition.isEmpty() == false){
			temp = new SimpleTransitionRule(inputAlphabet.getSymbol(transition));
			possibleRules.add(temp);
		}
		else{
			temp = new SimpleTransitionRule(null);
			possibleRules.add(temp);
		}

		return temp;
	}
}
