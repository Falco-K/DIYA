package diya.model.automata;

import diya.model.language.Alphabet;
import java.util.ArrayList;

import diya.model.automata.components.State;
import diya.model.automata.transitionRules.*;

public class FiniteStateMachine extends Automaton{

	ArrayList<TransitionRule> possibleRules;
	
	public FiniteStateMachine(int x, int y, Alphabet alphabet) {
		super(x, y, alphabet);
		
		possibleRules = new ArrayList<TransitionRule>();
	}

	@Override
	public void afterInputRead(TransitionRule transitionRule) {
	}

	@Override
	public boolean validate(State aState) {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean getEmptyTransitionsAllowed(){
		return true;
	}
	
	public TransitionRule makeTransitionRule(String transition){
		for(TransitionRule aRule : possibleRules){
			if(aRule.toString().equals(transition)){
				return aRule;
			}
		}
		
		TransitionRule temp;
		if(transition != null && transition.isEmpty() == false){
			temp = new TransitionRule(alphabet.getSymbol(transition));
			possibleRules.add(temp);
		}
		else{
			temp = new TransitionRule(null);
			possibleRules.add(temp);
		}

		return temp;
	}
}
