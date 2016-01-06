package diya.model.automata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import diya.model.automata.components.*;
import diya.model.automata.events.*;
import diya.model.language.*;
import diya.model.automata.transitionRules.*;

public abstract class Automaton extends ObservableAutomaton implements Iterable<Entry<String, State>>{
	ArrayList<State> currentStates;
	HashMap<String, State> states;
	InputTape inputTape;
	Alphabet alphabet;
	boolean running;
	
	public Automaton(int x, int y, Alphabet alphabet){
		states = new HashMap<String, State>();
		currentStates = new ArrayList<State>();
		inputTape = new InputTape(x, y);
		this.alphabet = alphabet;
		running = false;
	}
	
	public void setInput(Word word){
		reset();
		inputTape.setTape(word);
	}
	
	public void setInput(String[] input){
		setInput(alphabet.createWord(input));
	}
	
	public void setAlphabet(String[] symbolStrings){
		Alphabet newAlphabet = new Alphabet();
		
		for(int i = 0; i < symbolStrings.length; i++){
			if(alphabet.getSymbol(symbolStrings[i]) == null){
				newAlphabet.addSymbol(new Symbol(symbolStrings[i]));
			}
			else{
				newAlphabet.addSymbol(alphabet.getSymbol(symbolStrings[i]));
			}
		}
		
		alphabet = newAlphabet;
	}
	
	public String[] getAlphabet(){
		return alphabet.getAsStrings();
	}
	
	public InputTape getInputTape(){
		return inputTape;
	}
	
	public void reset(){
		inputTape.resetTape();
		setCurrentStates(null);
		running = false;
	}
	
	public boolean doTransition(){
		
		if(currentStates.isEmpty() && running == false){
			setInitialStates();
			running = !running;
			
			fireEvent(new StepDoneEvent(currentStates));
		}
		else{
			Symbol nextSymbol = inputTape.readSymbolMoveTape();

			if(nextSymbol == null){
				fireEvent(new RunFinishedEvent(hasAccepted()));
				return false;
			}
			
			ArrayList<State> tempStates = new ArrayList<State>();
			ArrayList<Transition> tempTransitions = new ArrayList<Transition>();
			
			for(State aState : currentStates){
				for(Transition aTransition : aState.getNextEdges(nextSymbol, getEmptyTransitionsAllowed())){
					tempTransitions.add(aTransition);
					tempStates.add(aTransition.getDestination());
					afterInputRead(aTransition.getTransitionRule(nextSymbol));
				}
			}
			
			currentStates = tempStates;
			
			fireEvent(new StepDoneEvent(currentStates, tempTransitions));
		}

		return true;
	}
	
	public boolean hasAccepted(){
		boolean accepted = false;
		
		if(inputTape.readCurrentSymbol() == null){
			for(State aState : currentStates){
				if(aState.isFinal()){
					accepted = true;
					break;
				}
			}
		}
		
		return accepted;	
	}
	
	public ArrayList<State> getCurrentStates(){
		return new ArrayList<State>(currentStates);
	}
	
	public State addState(String name, boolean initial, boolean accepting, float x, float y){
		return addState(new State(name, initial, accepting, x, y));
	}
	
	public State addState(State state){
		String name = state.getName();
		if(states.get(name) == null){
			states.put(name, state);
			fireEvent(new StateAddedEvent(states.get(name)));
			return states.get(name);
		}
		
		return null;
	}
	
	public State removeState(String name){
		State temp = states.remove(name);
		if(temp != null){
			fireEvent(new StateRemovedEvent(temp));
		}
		
		return temp;
	}
	
	public State updateState(String name, boolean initial, boolean accepting, float x, float y){
		State temp = states.get(name);
		if(temp != null){
			temp.setInitial(initial);
			temp.setFinal(accepting);
			temp.setPos(x, y);
			fireEvent(new StateUpdatedEvent(temp));
		}
		return temp;
	}
	
	public boolean isStateAccepting(String name){
		State temp = states.get(name);
		if(temp == null || (temp.isFinal() == false)){
			return false;
		}
		else{
			return true;
		}
	}
	
	public boolean isStateStart(String name){
		State temp = states.get(name);
		if(temp == null || (temp.isInitial() == false)){
			return false;
		}
		else{
			return true;
		}
	}
	
	public float getStatePositionX(String name){
		State temp = states.get(name);
		if(temp != null){
			return temp.getX();
		}
		else{
			return -1;
		}
	}
	
	public float getStatePositionY(String name){
		State temp = states.get(name);
		if(temp != null){
			return temp.getY();
		}
		else{
			return -1;
		}
	}
	
	public Transition getTransition(String origin, String destination){
		return states.get(origin).getTransition(destination);
	}
	
	public Transition addTransition(String origin, String destination, String[] transition){
		
		State firstState = states.get(origin);
		if(firstState != null && firstState.getTransition(destination) == null)
		{
			
			State secondState = states.get(destination);
			Transition newEdge = new Transition(firstState, secondState);
			for(String aRule : transition){
				if(alphabet.getSymbol(aRule) == null){
					return null;
				}
				
				newEdge.addTransitionRule(this.makeTransitionRule(aRule));
			}
			
			if(secondState != null && addTransition(newEdge) != null){
				return newEdge;
			}
		}
		
		return null;
	}
	
	public Transition addTransition(Transition transition){
		Transition temp = states.get(transition.getOrigin().getName()).addTransitionToState(transition);
		
		if(temp != null){
			fireEvent(new TransitionAddedEvent(temp));
		}
		
		return temp;
	}
	
	public Transition updateTransition(String origin, String destination, String[] transitionRule){
		Transition temp = getTransition(origin, destination);
		temp.clearTransitionRules();
		
		for(String aSymbol : transitionRule){
			temp.addTransitionRule(this.makeTransitionRule(aSymbol));
		}
		
		fireEvent(new TransitionUpdatedEvent(temp));
		return temp;
	}
	
	public Transition removeTransition(String origin, String destination){
		State temp = states.get(origin);
		if(temp != null) //Check if origin was destination and might already be removed
		{
			Transition transition = temp.removeTransition(destination);
			if(transition != null){
				fireEvent(new TransitionRemovedEvent(transition));
			}
			
			return transition;
		}
		
		return null;
	}
	
	public ArrayList<Transition> getTransitionsWithDestination(String destination){
		Iterator<Entry<String, State>> iterator = states.entrySet().iterator();
		ArrayList<Transition> removedEdges = new ArrayList<Transition>();
		
		while(iterator.hasNext()){
			Entry<String, State> pair = iterator.next();
			Transition temp = pair.getValue().getTransition(destination);
			
			if(temp != null){
				removedEdges.add(temp);
			}
		}
		
		return removedEdges;
	}
	
	public ArrayList<Transition> getTransitionsWithOrigin(String origin, boolean getCircularReferences){
		ArrayList<Transition> removedTransitions = new ArrayList<Transition>();
		
		for(Transition aTransition : states.get(origin).getOutgoingTransitions()){
			
			if(getCircularReferences == false && aTransition.getOrigin().equals(aTransition.getDestination())){
				continue;
			}
			
			removedTransitions.add(aTransition);
		}
		
		return removedTransitions;
	}
	
	public ArrayList<Transition> removeTransitionsWithDestination(String destination){
		Iterator<Entry<String, State>> iterator = states.entrySet().iterator();
		ArrayList<Transition> removedEdges = new ArrayList<Transition>();
		
		while(iterator.hasNext()){
			Entry<String, State> pair = iterator.next();
			Transition temp = pair.getValue().getTransition(destination);
			
			if(temp != null){
				removedEdges.add(temp);
				this.removeTransition(temp.getOrigin().getName(), temp.getDestination().getName());
				//pair.getValue().removeTransition(temp);
			}
		}
		
		return removedEdges;
	}
	
	public ArrayList<Transition> removeTransitionWithOrigin(String origin){
		ArrayList<Transition> removedTransitions = new ArrayList<Transition>();
		
		for(Transition aTransition : states.get(origin).getOutgoingTransitions()){
			removedTransitions.add(aTransition);
		}
		
		for(Transition aTransition : removedTransitions){
			this.removeTransition(aTransition.getOrigin().getName(), aTransition.getDestination().getName());
		}
		
		return removedTransitions;
	}
	
	public void setInitialStates(){
		currentStates.clear();
		Iterator<Entry<String, State>> iterator = states.entrySet().iterator();
		
		while(iterator.hasNext()){
			Entry<String, State> pair = iterator.next();
			
			State state = pair.getValue();
			if(state.isInitial()){
				this.currentStates.add(state);
			}
		}
	}
	
	public void setCurrentStates(ArrayList<String> stateNames){
		currentStates.clear();
		
		if(stateNames != null){
			for(String aState : stateNames){
				State temp = states.get(aState);
				
				if(temp != null){
					currentStates.add(temp);
				}
			}
		}

		fireEvent(new StepDoneEvent(currentStates));
	}
	
	public Iterator<Entry<String, State>> iterator(){
		return states.entrySet().iterator();
	}
	
	public abstract void afterInputRead(TransitionRule transitionRule);
	
	public abstract TransitionRule makeTransitionRule(String transition);
	
	public abstract boolean validate(State aState);
	
	public abstract boolean getEmptyTransitionsAllowed();
}
