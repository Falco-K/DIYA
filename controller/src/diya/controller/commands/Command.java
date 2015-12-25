package diya.controller.commands;

import diya.model.automata.Automaton;
import diya.view.DiyaViewInterface;

public abstract class Command{	
	Automaton automaton;
	DiyaViewInterface view;
	
	final UndoType undoType;
	Command nextCommand;
	
	public Command(Automaton automaton, DiyaViewInterface view, UndoType undoType){
		this.automaton = automaton;
		this.view = view;
		this.undoType = undoType;
		this.nextCommand = null;
	}
	
	public void linkCommand(Command command){
		if(nextCommand == null){
			nextCommand = command;
		}
	}

	public Command executeAll(){
		Command runner = this.nextCommand;
		while(runner != null){
			runner.execute();
			runner = runner.nextCommand;
		}
		
		this.execute();
		
		return this;
	}

	public Command undoAll(){	
		if(undoType == UndoType.UndoPossible)
		{
			this.undo();
		}
		
		Command runner = this.nextCommand;
		while(runner != null){
			if(runner.undoType == UndoType.UndoPossible){
				runner.undo();
			}
			
			runner = runner.nextCommand;
		}
		
		return this;
	}
	
	public void sendMessage(String message){
		view.message(message);
	}
	
	public UndoType getUndoType(){
		return undoType;
	}
	
	protected String[] getStringArray(String[] array, int begin, int end){
		int length = end-begin;
		if(length <= 0){
			return null;
		}
		
		String[] temp = new String[length];
		for(int i = begin; i < end; i++){
			temp[i-begin] = array[i];
		}
		
		return temp;
	}
	
	protected abstract Command execute();
	protected abstract Command undo();
}
