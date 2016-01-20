package diya.model.automata;

import java.util.ArrayList;
import java.util.HashSet;

import diya.model.automata.components.State;
import diya.model.automata.components.Tape;
import diya.model.automata.components.TapeType;
import diya.model.automata.components.Transition;
import diya.model.automata.events.InvalidAutomatonEvent;
import diya.model.automata.events.RunFinishedEvent;
import diya.model.automata.transitionRules.SimpleTransitionRule;
import diya.model.automata.transitionRules.TuringTransitionRule;
import diya.model.language.Alphabet;
import diya.model.language.AlphabetType;
import diya.model.language.Symbol;

public class TuringMachine extends Automaton {

	Symbol blank;

	public TuringMachine(int x, int y, Alphabet inputAlphabet, Alphabet tapeAlphabet, String blank){
		super(x, y, inputAlphabet, new Tape(x, y));

		this.blank = new Symbol(blank);
		tapes.get(TapeType.MAIN_TAPE).setBlank(this.blank);
		
		alphabets.put(AlphabetType.INPUT, inputAlphabet);
		
		Alphabet moveSymbols = new Alphabet();
		moveSymbols.addSymbol("R");
		moveSymbols.addSymbol("L");
		alphabets.put(AlphabetType.MOVEMENT, moveSymbols);
	
		Alphabet tapeSymbols = tapeAlphabet;
		tapeSymbols.addSymbol(this.blank);
		
		alphabets.put(AlphabetType.TAPE, tapeSymbols);
	}

	@Override
	public boolean executeTransition(HashSet<State> currentStatesOut, ArrayList<Transition> transitionsOut) {

		for(State aState : currentStatesOut){
			if(validate(aState) == false || currentStatesOut.size() > 1){
				fireEvent(new InvalidAutomatonEvent(aState));
				return false;
			}
			
			if(aState.isFinal()){
				fireEvent(new RunFinishedEvent(hasAccepted(), currentStatesOut));
				return false;
			}
		}
		
		Symbol currentSymbol = tapes.get(TapeType.MAIN_TAPE).readCurrentSymbol();
		
		for(State aState : currentStatesOut){
			ArrayList<Transition> possibleTransitions = aState.getNextEdges(currentSymbol);
			
			if(possibleTransitions.isEmpty()){
				fireEvent(new RunFinishedEvent(hasAccepted(), currentStatesOut));
				return false;
			}
			
			Transition transition = possibleTransitions.get(0);			
			TuringTransitionRule transitionRule = (TuringTransitionRule) transition.getTransitionRule(currentSymbol);
			
			Symbol executionSymbol = transitionRule.getExecutionSymbol();
			
			Alphabet movement = alphabets.get(AlphabetType.MOVEMENT);
			Tape tape = tapes.get(TapeType.MAIN_TAPE);
			
			if(executionSymbol.equals(movement.getSymbol("R"))){
				tape.moveHeadRight();
			}
			else if(executionSymbol.equals(movement.getSymbol("L"))){
				tape.moveHeadLeft();
			}
			else{
				tape.writeSymbol(executionSymbol);
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
		
		if(transition == null || transition.isEmpty()){
			transition = this.blank + "/" + this.blank;
		}
		
		String[] symbols = transition.split("/");
		
		Symbol inputSymbol = alphabets.get(AlphabetType.TAPE).getSymbol(symbols[0]);
		
		Symbol executionSymbol = alphabets.get(AlphabetType.TAPE).getSymbol(symbols[1]);
		
		if(executionSymbol == null){
			executionSymbol = alphabets.get(AlphabetType.MOVEMENT).getSymbol(symbols[1]);
		}
		
		return new TuringTransitionRule(inputSymbol, executionSymbol);
	}

	@Override
	public boolean validate(State aState) {
		return true;
	}
}
