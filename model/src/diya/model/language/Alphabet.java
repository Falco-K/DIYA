package diya.model.language;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

public class Alphabet{
	
	public static Alphabet combine(Alphabet... alphabets){
		Alphabet temp = new Alphabet();
		
		for(Alphabet alphabet : alphabets){
			for(Symbol aSymbol : alphabet.getSymbols()){
				temp.addSymbol(aSymbol);
			}
		}
		
		return temp;
	}
	
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
	
	public Collection<Symbol> getSymbols(){
		return symbols.values();
	}
	
	public String[] getAsStrings(){
		return getAsStrings(false);
	}
		
	public String[] getAsStrings(boolean sort){
		String[] symbolStrings = new String[symbols.size()];
		String[] temp = symbols.keySet().toArray(symbolStrings);
		if(sort){
			Arrays.sort(temp);
		}
		
		return temp;
	}
	
	public Alphabet clone(){
		Alphabet tempAlphabet = new Alphabet();
		Collection<Symbol> tempSymbols = symbols.values();
		
		for(Symbol aSymbol : tempSymbols){
			tempAlphabet.addSymbol(aSymbol);
		}
		
		return tempAlphabet;
	}
}
