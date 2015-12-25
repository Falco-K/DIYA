package diya.model.language;

import java.util.ArrayList;

public class Word{
	ArrayList<Symbol> symbols;
	
	public Word(Symbol... symbols){
		this.symbols = new ArrayList<Symbol>();
		
		for(Symbol aSymbol : symbols){
			this.symbols.add(aSymbol);
		}
	}
	
	public Word(String word){
		this.symbols = new ArrayList<Symbol>();
		
		for(String x : word.split(" ")){
			this.symbols.add(new Symbol(x));
		}
	}
	
	public ArrayList<Symbol> getSymbols(){
		return symbols;
	}
}
