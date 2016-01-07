package com.diya.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Group;
import diya.model.automata.components.InputTape;
import diya.model.automata.components.State;
import diya.model.automata.components.Transition;
import diya.view.DiyaViewInterface;

public class Graph extends Group{
	
	final static ShaderProgram fontShader;
	
	static{
		fontShader = new ShaderProgram(Gdx.files.internal("font.vert"), Gdx.files.internal("font.frag"));
		if (!fontShader.isCompiled()) {
		    Gdx.app.error("fontShader", "compilation failed:\n" + fontShader.getLog());
		}
		
	}
	
	DiyaViewInterface view;
	HashMap<String, Node> nodes;
	InputTapeObject inputTape;
	ShapeRenderer shapeRenderer;
	int nodeSize;

	public Graph(int width, int height, DiyaViewInterface view, InputTape startTape, ShapeRenderer shapeRenderer){
		nodes = new HashMap<String, Node>();
		nodeSize = 32;
		this.view = view;
		this.setBounds(0, 0, width, height);
		inputTape = new InputTapeObject(startTape);
		this.addActor(inputTape);
		this.shapeRenderer = shapeRenderer;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha){

	}
	
	public void drawGraph(Batch batch){
		Collection<Node> allNodes = nodes.values();
		
		shapeRenderer.begin(ShapeType.Line);
		for(Node aNode : allNodes){
			aNode.drawEdges(shapeRenderer);
		}
		
		inputTape.drawDebug(shapeRenderer);
		shapeRenderer.end();


		batch.setProjectionMatrix(this.getStage().getCamera().combined);
		batch.begin();
		for(Node aNode : allNodes){
			aNode.drawCircle(batch, 1);
		}
		
		inputTape.drawButtons(batch, 1);
		inputTape.draw(batch, 1);
		batch.end();
		
		Gdx.gl.glEnable(GL20.GL_BLEND);
		
		shapeRenderer.begin(ShapeType.Filled);
		for(Node aNode : allNodes){
			aNode.drawAnimation(shapeRenderer);
		}
		shapeRenderer.end();
		
		Gdx.gl.glDisable(GL20.GL_BLEND);
		
		batch.begin();
		batch.setShader(fontShader);
		for(Node aNode : allNodes){
			aNode.drawLabels(batch, 1);
		}
		
		inputTape.drawLabels(batch, 1);
		batch.setShader(null);
		batch.end();
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

	public void animateTransition(ArrayList<State> states, ArrayList<Transition> transitions, int stepCount) {

		Set<Entry<String, Node>> allNodes = nodes.entrySet();
		for(Entry<String, Node> aNodeName : allNodes){
			Node aNode = aNodeName.getValue();
			aNode.setHighlightingColor(new Color(1f,0f,0f,0.3f));
			aNode.setHighlighting(false);
		}
		
		if(transitions == null){
			return;
		}
		
		for(Transition aTransition : transitions){
			Node aNode = nodes.get(aTransition.getOrigin().getName());
			aNode.getEdge(aTransition).animateTransition();
		}
		
		if(stepCount == 1){
			for(State aState : states){
				nodes.get(aState.getName()).setHighlighting(true);
			}
		}
	}
}
