package diya.controller.handler;

import diya.controller.commands.*;
import diya.model.automata.Automaton;
import diya.view.DiyaViewInterface;

public class DiyaCommandProcessor extends DiyaController{

	CommandList commands;
	
	public DiyaCommandProcessor(Automaton automaton){
		super(automaton);
		
		commands = new CommandList();
	}

	public void actionPerformed(DiyaViewInterface view, String text){


		String[] fullCommand = text.split(" ");
		
		if(fullCommand.length < 1){
			return;
		}

		String command = fullCommand[0];
		String[] parameters = new String[fullCommand.length-1];
		for(int i = 1; i < fullCommand.length; i++){
			parameters[i-1] = fullCommand[i];
		}
		
		interpretCommand(command, parameters, view);
	}
	
	private void interpretCommand(String command, String[] parameters, DiyaViewInterface view){
		try
		{
			if(command.equals("addstate")){
				commands.addAndExecuteCommand(new AddState(automaton, view, parameters)); 
			}
			else if(command.equals("addtransition")){
				commands.addAndExecuteCommand(new AddTransition(automaton, view, parameters)); 
			}
			else if(command.equals("removetransition")){
				commands.addAndExecuteCommand(new RemoveTransition(automaton, view, parameters)); 
			}
			else if(command.equals("removestate")){
				commands.addAndExecuteCommand(new RemoveState(automaton, view, parameters)); 
			}
			else if(command.equals("updatestate")){
				commands.addAndExecuteCommand(new UpdateState(automaton, view, parameters));
			}
			else if(command.equals("reset")){
				commands.addAndExecuteCommand(new ResetAutomaton(automaton, view)); 
			}
			else if(command.equals("updatetransition")){
				commands.addAndExecuteCommand(new UpdateTransition(automaton, view, parameters));
			}
			else if(command.equals("setinput")){
				commands.addAndExecuteCommand(new SetInput(automaton, view, parameters));
			}
			else if(command.equals("dostep")){
				commands.addAndExecuteCommand(new DoStep(automaton, view));
			}
			else if(command.equals("undo")){
				commands.addAndExecuteCommand(new Undo(automaton, view, commands)); 
			}
			else if(command.equals("redo")){
				commands.addAndExecuteCommand(new Redo(automaton, view, commands)); 
			}
			else{
				view.message("Unknown command '"+command+"'.");
				return;
			}
		}
		catch(IllegalArgumentException e)
		{
			view.message(e.getMessage() == null ? "Error" : e.toString());
		}
	}
}
