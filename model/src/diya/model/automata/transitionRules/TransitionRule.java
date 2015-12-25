package diya.model.automata.transitionRules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import diya.model.language.Symbol;

public class TransitionRule {
	Symbol inputSymbol;
	
	public TransitionRule(Symbol symbol){
		inputSymbol = symbol;
	}
	
	public Symbol getSymbol(){
		return inputSymbol;
	}

	public void setSymbol(Symbol aSymbol){
		inputSymbol = aSymbol;
	}

	@Override
	public String toString(){
		return String.valueOf(inputSymbol.toString());
	}
}
