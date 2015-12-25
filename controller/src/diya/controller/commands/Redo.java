package diya.controller.commands;

import diya.controller.handler.CommandList;
import diya.model.automata.Automaton;
import diya.view.DiyaViewInterface;

public class Redo extends Command{

	CommandList list;
	
	public Redo(Automaton automaton, DiyaViewInterface view, CommandList list) {
		super(automaton, view, UndoType.NoUndoPossible);

		this.list = list;
	}

	@Override
	public Command execute() {
		this.list.redoCommand();
		
		return this;
	}

	@Override
	public Command undo() {
		throw new UnsupportedOperationException();
	}
}
