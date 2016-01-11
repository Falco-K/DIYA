package diya.model.automata.components;

import java.util.ArrayList;
import java.util.Arrays;

import diya.model.automata.transitionRules.SimpleTransitionRule;
import diya.model.automata.transitionRules.TransitionRuleInterface;
import diya.model.language.Symbol;

public class Transition extends Component{
	State origin;
	State destination;
	ArrayList<TransitionRuleInterface> transitionRules;
	
	public Transition(State origin, State destination){
		super(origin.getX(), origin.getY());
		this.origin = origin;
		this.destination = destination;
		this.transitionRules = new ArrayList<TransitionRuleInterface>();
	}
	
	public void addTransitionRule(TransitionRuleInterface transitionRule){
		transitionRules.add(transitionRule);
	}
	
	public void removeTransitionRule(SimpleTransitionRule aRule){
		transitionRules.remove(aRule);
	}
	
	public void clearTransitionRules(){
		transitionRules.clear();
	}
	
	public ArrayList<TransitionRuleInterface> getTransitionRules(){
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
	
	public TransitionRuleInterface getTransitionRule(Symbol aSymbol){
		for(TransitionRuleInterface aRule : transitionRules){
			if(aRule.getInputSymbol() == aSymbol){
				return aRule;
			}
		}
		
		return null;
	}
	
	public boolean accepts(Symbol input){
		for(TransitionRuleInterface aRule : transitionRules){
			if(aRule.hasEmptyInput()){
				continue;
			}
			
			if(aRule.getInputSymbol().equals(input)){
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isEmpty(){
		return transitionRules.isEmpty();
	}
	
	public boolean hasEmptyWordTransition(){
		for(TransitionRuleInterface aRule : transitionRules){
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
