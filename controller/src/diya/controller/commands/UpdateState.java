package diya.controller.commands;

import diya.model.automata.Automaton;
import diya.model.automata.components.State;
import diya.view.DiyaViewInterface;

public class UpdateState extends Command {
	String name;
	boolean initial;
	boolean accepting;
	float x;
	float y;
	boolean oldInitial;
	boolean oldAccepting;
	float oldX;
	float oldY;

	public UpdateState(Automaton automaton, DiyaViewInterface view, String... parameters){
		super(automaton, view, UndoType.UndoPossible);
		
		if(parameters.length == 3){
			this.name = parameters[0];
			this.x = Float.parseFloat(parameters[1]);
			this.y = Float.parseFloat(parameters[2]);
			this.accepting = automaton.isStateAccepting(name);
			this.initial = automaton.isStateStart(name);
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
		oldInitial = automaton.isStateStart(name);
		oldAccepting = automaton.isStateAccepting(name);
		oldX = automaton.getStatePositionX(name);
		oldY = automaton.getStatePositionY(name);
		State state = automaton.updateState(name, initial, accepting, x, y);
		
		if(state != null){
			sendMessage("Updated "+state.getName());
		}
		else{
			sendMessage("Couldn't update state "+name);
		}
		
		return this;
	}

	@Override
	public Command undo() {
		State state = automaton.updateState(name, oldInitial, oldAccepting, oldX, oldY);
		
		if(state != null){
			sendMessage("Reset "+name);
		}
		else{
			sendMessage("Couldn't reset "+name);
		}
		return this;
	}
}
