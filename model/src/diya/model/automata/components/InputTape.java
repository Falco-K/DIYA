package diya.model.automata.components;

import diya.model.language.Symbol;

public class InputTape extends Tape{

	public InputTape(int x, int y){
		super(x, y);
	}
	
	public Symbol readSymbolMoveTape(){
		Symbol symbol = readCurrentSymbol();
		moveHeadRight();
		
		return symbol;
	}
	
	@Override
	public void moveHeadRight(){
		if(successors.isEmpty() || successors.peek() == null){
			return;
		}
		
		predecessors.push(successors.pop());
	}
}
