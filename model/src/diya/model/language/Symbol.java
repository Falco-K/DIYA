package diya.model.language;

public class Symbol {
	String symbolChar;
	
	public Symbol(String symbol){
		this.symbolChar = symbol;
	}
	
	@Override
	public String toString(){
		return symbolChar;
	}
}
