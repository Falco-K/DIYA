package diya.model.automata.components;

import java.util.ArrayList;
import java.util.Stack;

import diya.model.language.Symbol;
import diya.model.language.Word;

public class Tape extends Component{
	Stack<Symbol> successors;
	Stack<Symbol> predecessors;
	Symbol blank;
	
	public Tape(int x, int y){
		super(x, y);
		
		successors = new Stack<Symbol>();
		predecessors = new Stack<Symbol>();
		blank = new Symbol("#");
	}
	
	public void setTape(Word word){
		predecessors.clear();
		successors.clear();
		ArrayList<Symbol> symbols = word.getSymbols();
		//Words must be added backwards onto the stack - current symbol is the top most.
		for(int i = symbols.size()-1; i >= 0; i--){
			successors.push(symbols.get(i));
		}
	}
	
	public Symbol readCurrentSymbol(){
		if(successors.isEmpty()){
			return null;
		}
		
		Symbol readSymbol = successors.peek();
		
		if(readSymbol != blank && readSymbol != null)
		{
			return readSymbol;
		}
		else
		{
			return null;
		}
	}
	
	public void moveHeadRight(){
		if(successors.isEmpty()){
			successors.push(blank);
		}
		predecessors.push(successors.pop());
	}
	
	public void moveHeadLeft(){
		if(predecessors.isEmpty()){
			predecessors.push(blank);
		}
		successors.push(predecessors.pop());
	}
	
	public void writeSymbol(Symbol symbol){
		successors.pop();
		successors.push(symbol);
	}
	
	public void resetTape(){
		while(predecessors.isEmpty() == false){
			moveHeadLeft();
		}
	}
}
