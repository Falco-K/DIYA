package diya.model.automata.transitionRules;

import diya.model.language.Symbol;

public interface TransitionRuleInterface {
	public Symbol getInputSymbol();
	public boolean hasEmptyInput();
	public void setInputSymbol(Symbol aSymbol);
}
