package diya.model.automata.transitionRules;

import diya.model.language.Symbol;

public class TuringTransitionRule implements TransitionRuleInterface{

	Symbol inputSymbol;
	Symbol executionSymbol;
	
	public TuringTransitionRule(Symbol inputSymbol, Symbol executionSymbol) {
		this.inputSymbol = inputSymbol;
		this.executionSymbol = executionSymbol;
	}

	@Override
	public Symbol getInputSymbol() {
		return inputSymbol;
	}
	
	public Symbol getExecutionSymbol(){
		return executionSymbol;
	}

	@Override
	public boolean hasEmptyInput() {
		return inputSymbol == null;
	}

	@Override
	public void setInputSymbol(Symbol aSymbol) {
		inputSymbol = aSymbol;
	}

	@Override
	public String toString(){
 		return inputSymbol.toString()+"/"+executionSymbol.toString();
	}
}
