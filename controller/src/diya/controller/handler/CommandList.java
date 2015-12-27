package diya.controller.handler;

import java.util.Stack;

import diya.controller.commands.Command;
import diya.controller.commands.UndoType;

public class CommandList {

	final int LIST_SIZE = 10;
	
	Stack<Command> undoList;
	Stack<Command> redoList;
	
	public CommandList(){
		undoList = new Stack<Command>();
		redoList = new Stack<Command>();
	}
	
	public void addAndExecuteCommand(Command command){
		if(command.getUndoType() == UndoType.UndoPossible){
			if(undoList.size() >= LIST_SIZE){
				undoList.remove(0);
			}
			
			undoList.push(command);
			emptyRedoList();
		}
		
		command.executeAll();
	}
	
	public void undoCommand(){
		if(undoList.isEmpty()){
			return;
		}
		
		redoList.push(undoList.pop().undoAll());	
	}
	
	public void redoCommand(){
		if(redoList.isEmpty()){
			return;
		}
		
		undoList.push(redoList.pop().executeAll());
	}
	
	private void emptyRedoList(){
		redoList.clear();
	}
}
