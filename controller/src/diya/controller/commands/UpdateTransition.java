package diya.controller.commands;

import java.util.ArrayList;
import java.util.Arrays;

import diya.model.automata.Automaton;
import diya.model.automata.components.Transition;
import diya.model.automata.transitionRules.TransitionRule;
import diya.view.DiyaViewInterface;

public class UpdateTransition extends Command {

	String origin;
	String destination;
	String[] transitionRules;
	String[] oldSymbols;
	
	public UpdateTransition(Automaton automaton, DiyaViewInterface view, String... parameters) {
		super(automaton, view, UndoType.UndoPossible);
		if(parameters.length == 2){
			origin = parameters[0];
			destination = parameters[1];
			transitionRules = new String[]{};
		}
		else if(parameters.length >= 3){
			origin = parameters[0];
			destination = parameters[1];
			transitionRules = this.getStringArray(parameters, 2, parameters.length);
		}else{
			throw new IllegalArgumentException("Expected parameters <origin, destination, transition>.");
		}
	}

	@Override
	protected Command execute() {
		oldSymbols = automaton.getTransition(origin, destination).getTransitionRulesAsStrings();
		automaton.updateTransition(origin, destination, transitionRules);
		this.sendMessage("Updated transition from "+origin+" to "+destination+" with "+Arrays.toString(transitionRules));
		return this;
	}

	@Override
	protected Command undo() {
		automaton.updateTransition(origin, destination, oldSymbols);
		this.sendMessage("Reset transition from "+origin+" to "+destination+" with "+Arrays.toString(oldSymbols));
		return this;
	}

}
