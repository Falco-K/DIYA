package diya.model.automata.transitionRules;

import diya.model.automata.Automaton;
import diya.model.language.Symbol;

public class TransitionRule {
	Symbol inputSymbol;
	
	public TransitionRule(Symbol symbol){
		inputSymbol = symbol;
	}
	
	public Symbol getSymbol(){
		return inputSymbol;
	}
	
	public boolean hasEmptyInput(){
		return inputSymbol == null;
	}

	public void setSymbol(Symbol aSymbol){
		inputSymbol = aSymbol;
	}

	@Override
	public String toString(){
		if(inputSymbol == null){
			return Automaton.EMPTY_WORD;
		}
		
		return inputSymbol.toString();
	}
}
