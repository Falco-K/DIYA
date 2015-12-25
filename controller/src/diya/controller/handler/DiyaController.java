package diya.controller.handler;

import diya.model.automata.Automaton;
import diya.view.DiyaViewInterface;

public abstract class DiyaController{
	Automaton automaton;
	
	public DiyaController(Automaton automaton){
		this.automaton = automaton;
	}
	
	public abstract void actionPerformed(DiyaViewInterface view, String text);
}
