package diya.controller.commands;

import diya.model.automata.Automaton;
import diya.view.DiyaViewInterface;

public class ResetAutomaton extends Command {

	public ResetAutomaton(Automaton automaton, DiyaViewInterface view){
		super(automaton, view, UndoType.NoUndoPossible);
		
	}

	@Override
	public Command execute() {
		automaton.reset();
		
		return this;
	}

	@Override
	public Command undo() {
		throw new UnsupportedOperationException();
	}

}
