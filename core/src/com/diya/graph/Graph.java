package com.diya.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import diya.model.automata.components.InputTape;
import diya.model.automata.components.State;
import diya.model.automata.components.Transition;
import diya.view.DiyaViewInterface;

public class Graph extends Group{
	DiyaViewInterface view;
	HashMap<String, Node> nodes;
	InputTapeObject inputTape;
	int nodeSize;

	public Graph(int width, int height, DiyaViewInterface view, InputTape startTape){
		nodes = new HashMap<String, Node>();
		nodeSize = 32;
		this.view = view;
		this.setBounds(0, 0, width, height);
		inputTape = new InputTapeObject(startTape);
		this.addActor(inputTape);
	}
	
	public void addNode(State state){
		Node temp = new Node(state, nodeSize);
		if(nodes.get(temp.getName()) == null){
			nodes.put(temp.getName(), temp);
			this.addActor(temp);
		}
	}
	
	public void removeNode(State state){
		Node temp = nodes.remove(state.getName());
		//removeEdgesToNode(state);
		this.removeActor(temp);
	}
	
	public void addEdge(Transition transition){
		Edge edge = new Edge(transition, nodes.get(transition.getOrigin().getName()), nodes.get(transition.getDestination().getName()));
		nodes.get(transition.getOrigin().getName()).addEdge(edge);
		this.addActor(edge);
	}
	
	public void updateEdge(Transition transition){
		nodes.get(transition.getOrigin().getName()).getEdge(transition).updateLabel();
	}
	
	public void updateNode(State state){
		nodes.get(state.getName()).update();
	}
	
	public void removeEdge(Transition transition){
		Edge temp = nodes.get(transition.getOrigin().getName()).getEdge(transition);
		if(temp != null){
			this.removeActor(temp);
			nodes.get(transition.getOrigin().getName()).removeEdge(temp);
		}
	}
	
	public void removeEdgesToNode(State state){
		Iterator<Entry<String, Node>> iterator = nodes.entrySet().iterator();
		
		while(iterator.hasNext()){
			Entry<String, Node> pair = iterator.next();

			pair.getValue().removeEdgeToNode(state);
		}
	}
	
	public void sendCommand(String message){
		view.sendCommand(message);
	}
	
	public void accepted(boolean accepted){
		inputTape.finished(accepted);
		
		Set<Entry<String, Node>> allNodes = nodes.entrySet();
		for(Entry<String, Node> aNode : allNodes){
			if(aNode.getValue().isFinal()){
				aNode.getValue().setHighlightingColor(new Color(0f,1f,0f,0.3f));
			}
			else{
				aNode.getValue().setHighlightingColor(new Color(1f,0f,0f,0.3f));
			}
		}
	}
	
	public void automatonReseted(){
		inputTape.resetCellHighlightning();
	}
	/*
	@Override
	public void drawDebug(ShapeRenderer shapeRenderer){
		Iterator<Entry<String, Node>> iterator = nodes.entrySet().iterator();
		
		while(iterator.hasNext()){
			Entry<String, Node> pair = iterator.next();

			pair.getValue().drawDebug(shapeRenderer);
		}
	}*/

	public void setHighlightedStates(ArrayList<State> states) {

		Set<Entry<String, Node>> allNodes = nodes.entrySet();
		for(Entry<String, Node> aNode : allNodes){
			aNode.getValue().setHighlightingColor(new Color(1f,0f,0f,0.3f));
			aNode.getValue().setHighlighting(false);
		}
		
		for(State aState : states){
			nodes.get(aState.getName()).setHighlighting(true);
		}
	}
}
