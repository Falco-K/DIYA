package diya.controller.commands;

import java.util.Arrays;

import diya.model.automata.Automaton;
import diya.model.language.AlphabetType;
import diya.view.DiyaViewInterface;

public class SetInputAlphabet extends Command{

	String[] alphabet;
	String[] oldAlphabet;
	
	public SetInputAlphabet(Automaton automaton, DiyaViewInterface view, String... parameters) {
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
		oldAlphabet = automaton.getAlphabets().get(AlphabetType.INPUT).getAsStrings();
		automaton.setInputAlphabet(alphabet);
		view.sendCommand("Alphabet set to "+Arrays.toString(alphabet));
		return this;
	}

	@Override
	protected Command undo() {

		automaton.setInputAlphabet(oldAlphabet);
		return this;
	}

}
