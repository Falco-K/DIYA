package diya.controller.commands;

import diya.model.automata.Automaton;
import diya.model.automata.components.State;
import diya.view.DiyaViewInterface;

public class AddState extends Command{
	String name;
	boolean initial;
	boolean accepting;
	float x;
	float y;

	public AddState(Automaton automaton, DiyaViewInterface view, String... parameters){
		super(automaton, view, UndoType.UndoPossible);
		
		if(parameters.length == 3){
			this.name = parameters[0];
			this.x = Float.parseFloat(parameters[1]);
			this.y = Float.parseFloat(parameters[2]);
		}
		else if(parameters.length == 5){
			this.name = parameters[0];
			this.initial = Boolean.parseBoolean(parameters[1]);
			this.accepting = Boolean.parseBoolean(parameters[2]);
			this.x = Float.parseFloat(parameters[3]);
			this.y = Float.parseFloat(parameters[4]);
		}
		else{
			throw new IllegalArgumentException();
		}
	}

	@Override
	public Command execute() {
		State state = automaton.addState(name, initial, accepting, x, y);
		
		if(state != null){
			sendMessage("Added "+state.getName());
		}
		else{
			sendMessage("Couldn't add state "+name);
		}
		
		return this;
	}

	@Override
	public Command undo() {
		State state = automaton.removeState(name);
		
		if(state != null){
			sendMessage("Removed "+name);
		}
		else{
			sendMessage("Couldn't remove "+name);
		}
		return this;
	}
}
