package diya.model.automata;

import java.util.ArrayList;
import java.util.HashSet;

import diya.model.automata.components.State;
import diya.model.automata.components.Tape;
import diya.model.automata.components.Transition;
import diya.model.automata.events.InvalidAutomatonEvent;
import diya.model.automata.events.RunFinishedEvent;
import diya.model.automata.transitionRules.SimpleTransitionRule;
import diya.model.automata.transitionRules.TuringTransitionRule;
import diya.model.language.Alphabet;
import diya.model.language.Symbol;

public class TuringMachine extends Automaton {

	Alphabet moveSymbols;
	Tape mainTape;
	
	public TuringMachine(int x, int y, Alphabet alphabet) {
		super(x, y, alphabet, new Tape(x, y));

		mainTape = tapes.get(Integer.valueOf(0));
		moveSymbols = new Alphabet();
		moveSymbols.addSymbol("R");
		moveSymbols.addSymbol("L");
	}

	@Override
	public boolean executeTransition(HashSet<State> currentStatesOut, ArrayList<Transition> transitionsOut) {

		for(State aState : currentStatesOut){
			if(validate(aState) == false){
				fireEvent(new InvalidAutomatonEvent(aState));
				return false;
			}
			
			if(aState.isFinal()){
				fireEvent(new RunFinishedEvent(hasAccepted()));
				return false;
			}
		}
		
		Symbol currentSymbol = mainTape.readCurrentSymbol();
		
		for(State aState : currentStatesOut){
			ArrayList<Transition> possibleTransitions = aState.getNextEdges(currentSymbol);
			
			if(possibleTransitions.isEmpty()){
				fireEvent(new RunFinishedEvent(hasAccepted()));
				return false;
			}
			
			Transition transition = possibleTransitions.get(0);			
			TuringTransitionRule transitionRule = (TuringTransitionRule) transition.getTransitionRule(currentSymbol);
			
			Symbol executionSymbol = transitionRule.getExecutionSymbol();
			if(executionSymbol.equals(moveSymbols.getSymbol("R"))){
				mainTape.moveHeadRight();
			}
			else if(executionSymbol.equals(moveSymbols.getSymbol("L"))){
				mainTape.moveHeadLeft();
			}
			else{
				mainTape.writeSymbol(executionSymbol);
			}
			
			currentStatesOut.clear();
			currentStatesOut.add(possibleTransitions.get(0).getDestination());
			transitionsOut.add(possibleTransitions.get(0));
		}
		
		return true;
	}
	
	@Override
	public boolean hasAccepted(){
		boolean accepted = false;
		
		for(State aState : currentStates){
			if(aState.isFinal()){
				accepted = true;
				break;
			}
		}
		
		return accepted;
	}

	@Override
	public TuringTransitionRule makeTransitionRule(String transition) {
		String[] symbols = transition.split("/");
		
		Symbol inputSymbol = inputAlphabet.getSymbol(symbols[0]);
		
		Symbol executionSymbol = inputAlphabet.getSymbol(symbols[1]);
		
		if(executionSymbol == null){
			executionSymbol = moveSymbols.getSymbol(symbols[1]);
		}
		
		return new TuringTransitionRule(inputSymbol, executionSymbol);
	}

	@Override
	public boolean validate(State aState) {
		return true;
	}

	@Override
	public boolean getEmptyTransitionsAllowed() {
		return false;
	}

}
