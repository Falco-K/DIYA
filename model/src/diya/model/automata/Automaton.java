package diya.model.automata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;

import diya.model.automata.components.*;
import diya.model.automata.events.*;
import diya.model.language.*;
import diya.model.automata.transitionRules.*;

public abstract class Automaton extends ObservableAutomaton implements Iterable<Entry<String, State>>{
	
	public final static String EMPTY_WORD = "\u03B5";
	
	HashSet<State> currentStates;
	HashMap<String, State> states;
	HashMap<TapeType, Tape> tapes;
	HashMap<AlphabetType, Alphabet> alphabets;
	boolean running;
	int stepCount;
	
	public Automaton(int x, int y, Alphabet alphabet, Tape tapeWithInput){
		states = new HashMap<String, State>();
		currentStates = new  HashSet<State>();
		tapes = new HashMap<TapeType, Tape>();
		alphabets = new HashMap<AlphabetType, Alphabet>();
		
		tapes.put(TapeType.MAIN_TAPE, tapeWithInput);
		alphabets.put(AlphabetType.INPUT, alphabet);
		
		running = false;
		stepCount = 0;
	}
	
	public void setInput(Word word){
		reset();
		getMainInputTape().setTape(word);
		fireEvent(new TapeUpdatedEvent(getMainInputTape()));
	}
	
	public void setInput(String[] input){
		setInput(new Word(input, alphabets.get(AlphabetType.INPUT)));
	}
	
	public void setInputAlphabet(String[] symbolStrings){
		Alphabet newAlphabet = new Alphabet();
		
		for(int i = 0; i < symbolStrings.length; i++){
			if(alphabets.get(AlphabetType.INPUT).getSymbol(symbolStrings[i]) == null){
				newAlphabet.addSymbol(new Symbol(symbolStrings[i]));
			}
			else{
				newAlphabet.addSymbol(alphabets.get(AlphabetType.INPUT).getSymbol(symbolStrings[i]));
			}
		}
		
		alphabets.put(AlphabetType.INPUT, newAlphabet);
	}
	
	public HashMap<AlphabetType, Alphabet> getAlphabets(){
		return alphabets;
	}
	
	public HashMap<AlphabetType, Alphabet> getAlphabets(AlphabetType... types){	
		HashMap<AlphabetType, Alphabet> temp = new HashMap<AlphabetType, Alphabet>(alphabets.size());
		
		for(AlphabetType type : types){
			temp.put(type, alphabets.get(type));
		}
		
		return temp;
	}
	
	public Alphabet getAlphabet(AlphabetType type){
		return alphabets.get(type);
	}
	
	public Tape getMainInputTape(){
		return tapes.get(TapeType.MAIN_TAPE);
	}
	
	public String getMainInputTapeContent(){
		return tapes.get(TapeType.MAIN_TAPE).toString();
	}
	
	public void reset(){
		running = false;
		stepCount = 0;
		tapes.get(TapeType.MAIN_TAPE).resetTape();
		setCurrentStates(null);
	}
	
	public boolean doStep(){
		
		if(currentStates.isEmpty() && running == false){
			setInitialStates();
			running = !running;
			
			ArrayList<Transition> emptyWordTransitions = getEmptyWordTransitionChain();
			for(Transition aTransition : emptyWordTransitions){
				currentStates.add(aTransition.getDestination());
			}
			
			stepCount++;
			fireEvent(new StepDoneEvent(currentStates, getEmptyWordTransitionChain(), stepCount));
		}
		else{
			
			ArrayList<Transition> tempTransitions = new ArrayList<Transition>();
			if(executeTransition(currentStates, tempTransitions) == false){
				return false;
			}

			stepCount++;
			fireEvent(new StepDoneEvent(currentStates, tempTransitions, stepCount));
		}

		fireEvent(new TapeUpdatedEvent(getMainInputTape()));
		return true;
	}
	
	public boolean run(Word aWord){
		setInput(aWord);
		while(doStep()){};
		
		return hasAccepted();
	}
	
	public boolean hasAccepted(){
		boolean accepted = false;
		
		if(getMainInputTape().readCurrentSymbol() == null){
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
			
			if(transition == null || transition.length < 1){
				newEdge.addTransitionRule(this.makeTransitionRule(null));
			}
			else{
				for(String aRule : transition){
					TransitionRuleInterface newRule = this.makeTransitionRule(aRule);
					
					if(newRule.getInputSymbol() == null){
						return null;
					}
					
					newEdge.addTransitionRule(newRule);
				}
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
		
		if(transitionRule == null || transitionRule.length < 1){
			temp.addTransitionRule(this.makeTransitionRule(null));
		}
		else{
			for(String aSymbol : transitionRule){
				temp.addTransitionRule(this.makeTransitionRule(aSymbol));
			}
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

		fireEvent(new StepDoneEvent(currentStates, null, stepCount));
	}
	
	public Iterator<Entry<String, State>> iterator(){
		return states.entrySet().iterator();
	}
	
	public ArrayList<Transition> getEmptyWordTransitionChain(){
		
		ArrayList<Transition> temp = new ArrayList<Transition>();
		for(State aState : currentStates){
			for(Transition aTransition : aState.getEmptyTransitionsChain(null)){
				temp.add(aTransition);
			}
		}
		
		return temp;
	}
	
	public int getCurrentStepCount(){
		return stepCount;
	}
	
	public abstract boolean executeTransition(HashSet<State> currentStatesOut, ArrayList<Transition> transitionsOut);
	
	//public abstract void afterInputRead(TransitionRule transitionRule);
	
	public abstract TransitionRuleInterface makeTransitionRule(String transition);
	
	public abstract boolean validate(State aState);
}
