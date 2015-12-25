package diya.model.automata.events;

import java.util.ArrayList;
import diya.view.DiyaViewInterface;

public abstract class ObservableAutomaton {
	
	ArrayList<DiyaViewInterface> subscribers;
	
	public ObservableAutomaton(){
		subscribers = new ArrayList<DiyaViewInterface>();
	}
	
	public void addObserver(DiyaViewInterface view){
		
		subscribers.add(view);
	}
		
	public void removeObserver(DiyaViewInterface view){
		subscribers.remove(view);
	}
	
	public void fireEvent(AutomatonEvent event){
		for(DiyaViewInterface view : subscribers){
			view.onNotify(event);
		}
	}
}
