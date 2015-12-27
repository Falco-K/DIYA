package diya.model.language;

import java.util.HashMap;

public class Alphabet {
	HashMap<String, Symbol> symbols;
	
	public Alphabet(){
		symbols = new HashMap<String, Symbol>();
	}
	
	public void addSymbol(Symbol symbol){
		symbols.put(String.valueOf(symbol.symbolChar), symbol);
	}
	
	public void addSymbol(String symbol){
		Symbol newSymbol = new Symbol(symbol);
		symbols.put(symbol, newSymbol);
	}
	
	public void removeSymbol(Symbol symbol){
		symbols.remove(symbol.symbolChar);
	}
	
	public Symbol getSymbol(String symbol){
		return symbols.get(symbol);
	}
	
	public String[] getAsStrings(){
		String[] symbolStrings = new String[symbols.size()];
		return symbols.keySet().toArray(symbolStrings);
	}
	
	public Word createWord(String[] input){
		
		Symbol[] temp = new Symbol[input.length];
		for(int i = 0; i < input.length; i++){
			temp[i] = symbols.get(input[i]);
		}
		
		return new Word(temp);
	}
}
