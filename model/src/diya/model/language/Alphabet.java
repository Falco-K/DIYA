package diya.model.language;

import java.util.HashMap;

public class Alphabet {
	HashMap<String, Symbol> symbols;
	
	public Alphabet(){
		symbols = new HashMap<String, Symbol>();
	}
	
	public void addSymbol(Symbol symbol){
		symbols.put(symbol.toString(), symbol);
	}
	
	public void addSymbol(String symbol){
		Symbol newSymbol = new Symbol(symbol);
		this.addSymbol(newSymbol);
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
}
