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
	
	public Word(String word, Alphabet alphabet){
		this(word.split(" "), alphabet);
	}
	
	public Word(String[] symbols, Alphabet alphabet){
		this.symbols = new ArrayList<Symbol>();
		
		for(String x : symbols){
			if(x.trim().isEmpty() == false){
				this.symbols.add(alphabet.getSymbol(x));
			}
		}
	}
	
	public ArrayList<Symbol> getSymbols(){
		return symbols;
	}
}
