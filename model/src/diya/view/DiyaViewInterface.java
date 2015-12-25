package diya.view;

import diya.model.automata.events.AutomatonEvent;

public interface DiyaViewInterface {
	public void message(String message);
	
	public void onNotify(AutomatonEvent event);
	
	public void sendCommand(String command);
}
