package diya.controller.commands;

import diya.model.automata.Automaton;
import diya.model.automata.components.Transition;
import diya.view.DiyaViewInterface;

public class RemoveTransition extends Command{

	Transition transition;
	String origin;
	String destination;
	
	public RemoveTransition(Automaton automaton, DiyaViewInterface view, String... parameters){
		super(automaton, view, UndoType.UndoPossible);
		
		if(parameters.length == 2){
			origin = parameters[0];
			destination = parameters[1];
		}
		else{
			throw new IllegalArgumentException();
		}
	}
	
	public RemoveTransition(Automaton automaton, DiyaViewInterface view, Transition transition){
		super(automaton, view, UndoType.UndoPossible);
		
		this.transition = transition;
		this.origin = transition.getOrigin().getName();
		this.destination = transition.getDestination().getName();
	}

	@Override
	public Command execute() {
		transition = automaton.removeTransition(origin, destination);	
		if(transition != null){
			view.message("Removed transition from "+origin+" to "+destination);
		}else{
			view.message("Transition from "+origin+" to "+destination+" could not be removed.");
		}
		return this;
	}

	@Override
	public Command undo() {
		transition = automaton.addTransition(transition);
		return this;
	}
}
