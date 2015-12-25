package diya.controller.commands;

import diya.controller.handler.CommandList;
import diya.model.automata.Automaton;
import diya.view.DiyaViewInterface;

public class Undo extends Command {

	CommandList list;
	
	public Undo(Automaton automaton, DiyaViewInterface view, CommandList list) {
		super(automaton, view, UndoType.NoUndoPossible);
		
		this.list = list;
	}

	@Override
	public Command execute() {
		list.undoCommand();
		
		return this;
	}

	@Override
	public Command undo() {
		throw new UnsupportedOperationException();
	}
}
