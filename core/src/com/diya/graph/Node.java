package com.diya.graph;

import java.util.ArrayList;
import java.util.EnumSet;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.diya.ConstructionMenuOption;
import com.diya.ConstructionStage;
import com.diya.ConstructionMenuInterface;

import diya.model.automata.components.State;
import diya.model.automata.components.Transition;

public class Node extends GraphElement implements ConstructionMenuInterface{
	
	static float finalCircleRadius = 5;
	
	State state;
	Circle circle;
	ArrayList<Edge> outgoingEdges;
	StartEdge startEdge;
	boolean highlight;
	float animation;
	Color highlightingColor;
	
	public Node(State state, int radius){
		
		this.state = state;
		this.circle = new Circle();	
		this.setName(state.getName());

		this.setSize(radius*2f, radius*2f);
		this.setPosition(state.getX()-radius, state.getY()-radius);
		this.setTransform(false);
		
		Label nodeLabel = new Label(this.state.getName(), labelStyle);
		nodeLabel.setPosition(-nodeLabel.getWidth()/2.0f+circle.radius, -nodeLabel.getHeight()/2.0f+circle.radius+4);
		nodeLabel.setTouchable(Touchable.disabled);
		this.addActor(nodeLabel);
		startEdge = new StartEdge(this);
		startEdge.setTouchable(Touchable.enabled);
		this.addActor(startEdge);
		this.highlightingColor = Color.RED;

		this.outgoingEdges = new ArrayList<Edge>();
		this.setDebug(true);
		
		this.addListener(new DragListener(){
			
			float offsetX;
			float offsetY;
			boolean dragged = false;
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if(button == Input.Buttons.LEFT && event.getTarget() instanceof Node){
					offsetX = x;
					offsetY = y;
					return true;
				}
				
				return false;
			}
			
			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer){
				moveBy(x-offsetX, y-offsetY);
				dragged = true;
			}
			
			@Override
		    public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				if(dragged){
					dragged = false;
					Node temp = (Node)event.getListenerActor();
					((ConstructionStage)event.getStage()).sendCommand("updatestate "+temp.getName()+" "+temp.getMidX()+" "+temp.getMidY());
				}
			}
		});
	}
	
	public void setHighlightingColor(Color color){
		this.highlightingColor = color;
	}

	public void addEdge(Edge edge){
		this.outgoingEdges.add(edge);
	}
	
	public void removeEdge(Edge edge){
		this.outgoingEdges.remove(edge);
	}
	
	public void removeEdge(final Transition transition){
		this.removeEdgeToNode(transition.getDestination());
	}
	
	public void setHighlighting(boolean highlight){
		this.highlight = highlight;
	}
	
	public void removeEdgeToNode(final State state){
		Edge removedEdge = null;
		for(Edge aEdge : outgoingEdges){
			if(aEdge.transition.getDestination().getName().equals(state.getName())){
				removedEdge = aEdge;
				break;
			}
		}
		
		if(removedEdge != null){
			this.outgoingEdges.remove(removedEdge);
		}
	}
	
	public Edge getEdge(final Transition transition){

		for(Edge aEdge : outgoingEdges){
			if(aEdge.transition == transition){
				return aEdge;
			}
		}
		
		return null;
	}
	
	public boolean hasEdgeTo(Node node){
		for(Edge aEdge : outgoingEdges){
			if(aEdge.getDestinationNode() == node){
				return true;
			}
		}
		
		return false;
	}

	public float getInnerRadius(){
		return circle.radius;
	}
	
	public float getOuterRadius(){
		return this.isFinal() ? circle.radius+finalCircleRadius : circle.radius;
	}
	
	public float getMidX(){
		return getX()+getInnerRadius();
	}
	
	public boolean isFinal(){
		return this.state.isFinal();
	}
	
	public float getMidY(){
		return getY()+getInnerRadius();
	}

	public void update(){
		this.setPosition(state.getX()-this.getInnerRadius(), state.getY()-this.getInnerRadius());
	}
	
	@Override
	protected void positionChanged(){
		circle.setPosition(this.getX()+circle.radius, this.getY()+circle.radius);
	}
	
	@Override
	protected void sizeChanged(){
		circle.setRadius(this.getHeight()/2.0f);
	}
	
	@Override
	public void act(float delta){
		if(this.state.isInitial()){
			startEdge.updateEdgePosition();
		}

		super.act(delta);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha){
		batch.setShader(fontShader);
		this.toFront();
		this.drawChildren(batch, parentAlpha);
		batch.setShader(null);
	}

	@Override
	public void drawDebug(ShapeRenderer shapeRenderer){
		//this.drawDebugBounds(shapeRenderer);
		shapeRenderer.setColor(Color.GREEN);
		for(Edge anEdge: outgoingEdges){
			anEdge.toBack();
			anEdge.drawDebug(shapeRenderer);
		}
		
		if(this.state.isInitial()){
			startEdge.drawDebug(shapeRenderer);
		}
		
		shapeRenderer.setColor(Color.BLACK);
		shapeRenderer.circle(this.circle.x, this.circle.y, this.circle.radius);
		
		shapeRenderer.set(ShapeType.Filled);
		shapeRenderer.setColor(highlightingColor);
		if(highlight == true){
			shapeRenderer.circle(this.circle.x, this.circle.y, Interpolation.linear.apply(0, this.circle.radius, animation));
			
			if(animation < 1){
				animation+=0.05;
			}
		}
		else if(animation > 0){
			animation-= 0.05;
			shapeRenderer.circle(this.circle.x, this.circle.y, Interpolation.linear.apply(0, this.circle.radius, animation));
		}
		
		shapeRenderer.set(ShapeType.Line);
		if(this.state.isFinal()){
			shapeRenderer.circle(this.circle.x, this.circle.y, this.circle.radius+finalCircleRadius);
		}
	}

	@Override
	public EnumSet<ConstructionMenuOption> getMenuOptions(){
		return EnumSet.of(ConstructionMenuOption.Close, ConstructionMenuOption.SetStart, ConstructionMenuOption.SetFinal);
	}
	
	@Override
	public void setActive() {
	}

	@Override
	public void setInactive() {
	}

	@Override
	public void setSelectedOption(ConstructionMenuOption entry, CharSequence text) {
		if(entry.equals(ConstructionMenuOption.SetStart)){
			((ConstructionStage)this.getStage()).sendCommand("updatestate "+this.getName()+" "+(!this.state.isInitial())+" "+this.state.isFinal()+" "+this.getMidX()+" "+this.getMidY());
		}
		else if(entry.equals(ConstructionMenuOption.SetFinal)){
			((ConstructionStage)this.getStage()).sendCommand("updatestate "+this.getName()+" "+this.state.isInitial()+" "+(!this.state.isFinal())+" "+this.getMidX()+" "+this.getMidY());
		}
	}

	@Override
	public float preferredMenuPositionX() {
		return getMidX();
	}
	
	@Override
	public float preferredMenuPositionY(){
		return getMidY();
	}
	
	@Override
	public String getSelectedOptions(){
		String options = "";
		if(this.state.isInitial()){
			options+=ConstructionMenuOption.SetStart.toString();
		}
		if(this.state.isFinal()){
			if(options != ""){
				options+=",";
			}
			
			options+=ConstructionMenuOption.SetFinal.toString();
		}
		return options;
	}
	
	class StartEdge extends Actor{
		
		Node node;
		Vector2 edgeVector;
		Vector2 startVector;
		private Vector2 arrowCenter;
		private Vector2 arrowStart;
		private Vector2 arrowLeftSide;
		private Vector2 arrowRightSide;
		
		public StartEdge(final Node node){
			this.node = node;
			this.edgeVector = new Vector2(node.getInnerRadius()*2, 0);
			this.startVector = new Vector2();
			this.arrowCenter = new Vector2();
			this.arrowStart = new Vector2();
			this.arrowLeftSide = new Vector2();
			this.arrowRightSide = new Vector2();
			
			this.setSize(node.getInnerRadius()*2f, node.getInnerRadius());

			this.addListener(new ClickListener(){
				Vector2 temp = new Vector2();
				float offsetX;
				float offsetY;
				
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
					if(button == Input.Buttons.LEFT){
						offsetX = x;
						offsetY = y;
						return true;
					}
					
					return false;
				}
				
				@Override
				public void touchDragged(InputEvent event, float x, float y, int pointer){
					Actor tempActor = (StartEdge)event.getListenerActor();
					
					float rotation = temp.set(x+offsetX, y-offsetY).angle();
					if(tempActor.getRotation()+rotation >= 360){
						rotation-=360;
					}
					else if(tempActor.getRotation()+rotation < 0){
						rotation+=360;
					}
					tempActor.rotateBy(rotation);
				}
			});
			
			updateEdgePosition();
			this.setOrigin(node.getInnerRadius(), node.getInnerRadius());
			this.setRotation(180);
		}
		
		public void updateEdgePosition(){
			edgeVector.setAngle(this.getRotation());
			startVector.set(edgeVector);
			startVector.rotate(90);
			startVector.setLength(this.getHeight()/2);
			setPosition(edgeVector.x+startVector.x, edgeVector.y+startVector.y);
			calculateArrowHead(0);
		}
		
		@Override
		public void drawDebug(ShapeRenderer shapeRenderer){
			shapeRenderer.setColor(Color.DARK_GRAY);
			shapeRenderer.line(node.getMidX(), node.getMidY(), node.getMidX()+edgeVector.x, node.getMidY()+edgeVector.y);
			drawArrowHead(shapeRenderer);
		}
		
		private void calculateArrowHead(float rotation){	
			arrowCenter.set(edgeVector);
			arrowCenter.setLength(node.isFinal() ? node.getInnerRadius()+5 : node.getInnerRadius());
			arrowStart.set(node.getMidX()+arrowCenter.x, node.getMidY()+arrowCenter.y);
			
			arrowLeftSide.set(arrowCenter);
			arrowLeftSide.setLength(16);
			arrowLeftSide.setAngle(arrowLeftSide.angle()+30);
			arrowLeftSide.rotate(rotation);
			
			arrowRightSide.set(arrowCenter);
			arrowRightSide.setLength(16);
			arrowRightSide.setAngle(arrowRightSide.angle()-30);
			arrowRightSide.rotate(rotation);
			
			arrowCenter.setLength(16/3);	
			arrowCenter.rotate(rotation);
		}
		
		private void drawArrowHead(ShapeRenderer shapeRenderer){
			shapeRenderer.triangle(arrowStart.x, arrowStart.y, arrowStart.x+arrowCenter.x, arrowStart.y+arrowCenter.y, 
					arrowStart.x+arrowLeftSide.x, arrowStart.y+arrowLeftSide.y);
			
			shapeRenderer.triangle(arrowStart.x, arrowStart.y, arrowStart.x+arrowCenter.x, arrowStart.y+arrowCenter.y, 
					arrowStart.x+arrowRightSide.x, arrowStart.y+arrowRightSide.y);
		}
	}
}
