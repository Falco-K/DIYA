package diya.model.automata.transitionRules;

import diya.model.automata.Automaton;
import diya.model.language.Symbol;

public class SimpleTransitionRule implements TransitionRuleInterface{
	Symbol inputSymbol;
	
	public SimpleTransitionRule(Symbol symbol){
		inputSymbol = symbol;
	}
	
	@Override
	public Symbol getInputSymbol(){
		return inputSymbol;
	}
	
	@Override
	public boolean hasEmptyInput(){
		return inputSymbol == null;
	}

	@Override
	public void setInputSymbol(Symbol aSymbol){
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
