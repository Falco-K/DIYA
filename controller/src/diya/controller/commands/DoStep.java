package diya.controller.commands;

import java.util.ArrayList;

import diya.model.automata.Automaton;
import diya.model.automata.components.State;
import diya.view.DiyaViewInterface;

public class DoStep extends Command {

	ArrayList<String> save;
	
	public DoStep(Automaton automaton, DiyaViewInterface view) {
		super(automaton, view, UndoType.NoUndoPossible);

		save = new ArrayList<String>();
	}

	@Override
	protected Command execute() {
		ArrayList<State> temp = automaton.getCurrentStates();
		for(State aState : temp){
			save.add(aState.getName());
		}

		if(automaton.doStep() == true){
			sendMessage("Step done");
			
		}else{
			//sendMessage("Couldn't do step - either no inital states set or no possible transition available.");
		}
		
		return this;
	}

	@Override
	protected Command undo() {
		automaton.setCurrentStates(save);
		sendMessage("Reset step");
		return this;
	}

}
