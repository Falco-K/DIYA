package diya.controller.commands;

import java.util.Arrays;

import diya.model.automata.Automaton;
import diya.view.DiyaViewInterface;

public class SetInput extends Command {

	String[] input;
	public SetInput(Automaton automaton, DiyaViewInterface view, String... parameters) {
		super(automaton, view, UndoType.NoUndoPossible);

		if(parameters.length == 0){
			input = new String[0];
		}
		else if(parameters.length >= 1){
			input = parameters;
		}
		else{
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected Command execute() {
		automaton.setInput(input);
		view.message("Set input to "+Arrays.toString(input));
		return this;
	}

	@Override
	protected Command undo() {
		// TODO Auto-generated method stub
		return null;
	}
}
