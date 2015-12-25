package diya.controller.commands;

import java.util.ArrayList;

import diya.model.automata.Automaton;
import diya.model.automata.components.State;
import diya.model.automata.components.Transition;
import diya.view.DiyaViewInterface;

public class RemoveState extends Command{
	
	String name;
	State state;
	
	ArrayList<Transition> removedEdges;
	
	public RemoveState(Automaton automaton, DiyaViewInterface view, String... parameters){
		super(automaton, view, UndoType.UndoPossible);
		
		if(parameters.length == 1){
			name = parameters[0];
		}
		else{
			throw new IllegalArgumentException();
		}
		
		if(removedEdges == null){
			removedEdges = new ArrayList<Transition>();
			removedEdges.addAll(automaton.getTransitionsWithDestination(name));
			//Get all outgoing destinations, but not those with a circular reference (-> a loop), else transition is removed twice.
			removedEdges.addAll(automaton.getTransitionsWithOrigin(name, false));
			
			Command runner = this;
			for(Transition aEdge : removedEdges){
				runner.linkCommand(new RemoveTransition(automaton, view, aEdge));
				runner = runner.nextCommand;
			}
		}
	}

	@Override
	public Command execute() {			
		state = automaton.removeState(name);
		
		if(state != null){
			view.message("Removed State "+name);
		}
		else{
			view.message("Couldn't remove state "+name);
		}
		
		return this;
	}

	@Override
	public Command undo() {
		State temp = automaton.addState(state);
		
		if(temp != null){
			view.message("Added State"+name+" again");
		}
		else{
			view.message("Couldn't add state "+name+" again");
		}
		
		return this;
	}
}
