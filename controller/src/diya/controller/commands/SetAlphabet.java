package diya.controller.commands;

import java.util.Arrays;

import diya.model.automata.Automaton;
import diya.view.DiyaViewInterface;

public class SetAlphabet extends Command{

	String[] alphabet;
	String[] oldAlphabet;
	
	public SetAlphabet(Automaton automaton, DiyaViewInterface view, String... parameters) {
		super(automaton, view, UndoType.UndoPossible);

		if(parameters.length >= 1){
			alphabet = parameters;
		}
		else{
			throw new IllegalArgumentException();
		}
	}

	@Override
	protected Command execute() {
		oldAlphabet = automaton.getAlphabet();
		automaton.setAlphabet(alphabet);
		view.sendCommand("Alphabet set to "+Arrays.toString(alphabet));
		return this;
	}

	@Override
	protected Command undo() {

		automaton.setAlphabet(oldAlphabet);
		return this;
	}

}
