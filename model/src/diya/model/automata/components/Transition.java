package diya.model.automata.components;

import java.util.ArrayList;
import java.util.Arrays;

import diya.model.automata.transitionRules.TransitionRule;
import diya.model.language.Symbol;

public class Transition extends Component{
	State origin;
	State destination;
	ArrayList<TransitionRule> transitionRules;
	
	public Transition(State origin, State destination){
		super(origin.getX(), origin.getY());
		this.origin = origin;
		this.destination = destination;
		this.transitionRules = new ArrayList<TransitionRule>();
	}
	
	public void addTransitionRule(TransitionRule aRule){
		transitionRules.add(aRule);
	}
	
	public void removeTransitionRule(TransitionRule aRule){
		transitionRules.remove(aRule);
	}
	
	public void clearTransitionRules(){
		transitionRules.clear();
	}
	
	public ArrayList<TransitionRule> getTransitionRules(){
		return transitionRules;
	}
	
	public String[] getTransitionRulesAsStrings(){
		String[] rules = new String[transitionRules.size()];
		for(int i = 0; i < rules.length; i++){
			rules[i] = transitionRules.get(i).toString();
		}
		Arrays.sort(rules);
		
		return rules;
	}
	
	public TransitionRule getTransitionRule(Symbol aSymbol){
		for(TransitionRule aRule : transitionRules){
			if(aRule.getSymbol() == aSymbol){
				return aRule;
			}
		}
		
		return null;
	}
	
	public boolean accepts(Symbol input){
		for(TransitionRule aRule : transitionRules){
			if(aRule.hasEmptyInput()){
				continue;
			}
			
			if(aRule.getSymbol().equals(input)){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isEmpty(){
		return transitionRules.isEmpty();
	}
	
	public boolean hasEmptyWordTransition(){
		for(TransitionRule aRule : transitionRules){
			if(aRule.hasEmptyInput()){
				return true;
			}
		}
		
		return false;
	}
	
	public State getDestination(){
		return this.destination;
	}
	
	public State getOrigin(){
		return this.origin;
	}
}
