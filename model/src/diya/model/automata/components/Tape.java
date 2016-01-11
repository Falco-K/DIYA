package diya.model.automata.components;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

import diya.model.automata.events.TapeUpdatedEvent;
import diya.model.language.Symbol;
import diya.model.language.Word;

public class Tape extends Component implements Iterable<Symbol>{
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
	
	public int getCurrentHeadPosition(){
		return predecessors.size();
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

	@Override
	public Iterator<Symbol> iterator() {
		return new TapeIterator();
	}
	
	private final class TapeIterator implements Iterator<Symbol>{
		
		private int cursor;
		private final int boundary;
		private final int end;
		
		public TapeIterator(){
			this.cursor = 0;
			this.boundary = predecessors.size();
			this.end = boundary+successors.size();
		}

		@Override
		public boolean hasNext() {
			return this.cursor < end;
		}

		@Override
		public Symbol next() {
			if(!this.hasNext()){
				throw new NoSuchElementException();
			}
			
			Symbol symbol;
			
			if(cursor < boundary){
				symbol = predecessors.get(cursor);
			}
			else{
				symbol = successors.get((successors.size()-1)-(cursor-boundary));
			}
			
			cursor++;
			return symbol;
		}	
		
		@Override
		public void remove(){
			throw new UnsupportedOperationException();
		}
	}
}
