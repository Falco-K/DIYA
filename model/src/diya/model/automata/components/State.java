package diya.model.automata.components;

import java.util.ArrayList;
import java.util.Iterator;

import diya.model.language.Symbol;

public class State extends Component implements Iterable<Transition>{
	String name;
	boolean isInitial;
	boolean isFinal;
	
	ArrayList<Transition> outgoingTransitions;
	
	public State(String statename, float x, float y){
		this(statename, false, false, x, y);
	}
	
	public State(String statename, boolean isInitial, boolean isFinal, float x, float y){
		super(x, y);
		name = statename;
		setInitial(isInitial);
		setFinal(isFinal);
		
		outgoingTransitions = new ArrayList<Transition>();
	}
	
	public Transition addTransitionToState(Transition transition){
		if(transition != null && outgoingTransitions.contains(transition) == false){
			outgoingTransitions.add(transition);
			return transition;
		}
		
		return null;
	}
	
	public boolean removeTransitionToSameDestination(Transition transition){ 
		for(Transition aTransition : outgoingTransitions){
			if(aTransition.destination.name.matches(transition.destination.name)){
				outgoingTransitions.remove(aTransition);
				return true;
			}
		}

		return false;
	}
	
	public boolean removeTransition(Transition transition){
		return outgoingTransitions.remove(transition);
	}
	
	public Transition removeTransition(String destination){
		Transition temp = getTransition(destination);
		outgoingTransitions.remove(temp);
		
		return temp;
	}
	
	public Transition getTransition(String destination){
		for(Transition aTransition : outgoingTransitions){
			if(aTransition.destination.name.matches(destination)){
				return aTransition;
			}
		}
		
		return null;
	}
	
	public Transition getTransition(State destination){
		for(Transition aTransition : outgoingTransitions){
			if(aTransition.destination == destination){
				return aTransition;
			}
		}
		
		return null;
	}
	
	public ArrayList<Transition> getNextEdges(Symbol symbol, boolean getEmptyTransitions){
		ArrayList<Transition> transitions = new ArrayList<Transition>();
		
		if(outgoingTransitions.isEmpty()){
			return transitions;
		}
		
		for(Transition aTransition : outgoingTransitions){
			if(aTransition.accepts(symbol) || (getEmptyTransitions && aTransition.isEmpty())){
				transitions.add(aTransition);
			}
		}
		
		return transitions;
	}
	
	public ArrayList<Transition> getOutgoingTransitions(){
		return outgoingTransitions;
	}
	
	public Iterator<Transition> iterator(){
		return outgoingTransitions.iterator();
	}
	
	public void setFinal(boolean isAccepting){
		isFinal = isAccepting;
	}
	
	public void setInitial(boolean isStart){
		isInitial = isStart;
	}
	
	public boolean isFinal(){
		return isFinal;
	}
	
	public boolean isInitial(){
		return isInitial;
	}
	
	public String getName(){
		return name;
	}
}
