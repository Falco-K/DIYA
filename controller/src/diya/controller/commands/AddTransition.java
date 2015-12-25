package diya.controller.commands;

import java.util.Arrays;

import diya.model.automata.Automaton;
import diya.model.automata.components.Transition;
import diya.view.DiyaViewInterface;

public class AddTransition extends Command{
	
	String origin;
	String destination;
	String[] transitionSymbols;
	
	public AddTransition(Automaton automaton, DiyaViewInterface view, String... parameters){
		super(automaton, view, UndoType.UndoPossible);
		
		if(parameters.length >= 3){
			origin = parameters[0];
			destination = parameters[1];
			transitionSymbols = this.getStringArray(parameters, 2, parameters.length);
		}else{
			throw new IllegalArgumentException("Expected parameters <origin, destination, transition>.");
		}
	}

	@Override
	public Command execute() {
		Transition transition = automaton.addTransition(origin, destination, transitionSymbols);
		
		if(transition != null){
			sendMessage("Added transition from "+origin+" to "+destination+" with " + Arrays.toString(transitionSymbols));
		}
		else{
			sendMessage("Couldn't add transition from "+origin+" to "+ destination);
		}
		
		return this;
	}

	@Override
	public Command undo() {
		Transition transition = automaton.removeTransition(origin, destination);
		
		if(transition != null){
			sendMessage("Removed transition from "+origin+" to "+destination);
		}else{
			sendMessage("Couldn't remove the transition.");
		};
		
		return this;
	}
}
