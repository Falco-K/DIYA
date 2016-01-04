package com.diya.graph;

import java.util.EnumSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.diya.ConstructionMenuInterface;
import com.diya.ConstructionMenuOption;
import com.diya.ConstructionStage;

import diya.model.automata.components.Transition;

public class Edge extends GraphElement implements ConstructionMenuInterface{	
	Transition transition;
	
	Node origin;
	Node destination;
	Group edgeLabels;
	Label edgeLabel;
	Connection curve;
	
	Vector2 edgeVector;
	Vector2 calculatingVector;
	
	public Edge(Transition transition, Node origin, Node destination){
		this.transition = transition;

		this.origin = origin;
		this.destination = destination;
		edgeVector = new Vector2();
		calculatingVector = new Vector2();

		
		edgeLabels = new Group();
		edgeLabels.setTouchable(Touchable.disabled);
		edgeLabel = new Label("", labelStyle);
		edgeLabel.setFillParent(true);
		edgeLabels.addActor(edgeLabel);
		edgeLabel.setAlignment(Align.center);
		curve = new Connection(origin, destination, edgeVector);
		curve.setOrigin(Align.center);
		this.addActor(edgeLabels);
		edgeLabels.setOrigin(Align.center);
		
		this.setDebug(true);
		
		if(origin == destination){
			this.addListener(new ClickListener(){
				Vector2 temp = new Vector2();
				float offsetX;
				
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
					if(button == Input.Buttons.LEFT){
						offsetX = x;

						return true;
					}
					
					return false;
				}
				
				@Override
				public void touchDragged(InputEvent event, float x, float y, int pointer){
					Actor tempActor = (Edge)event.getListenerActor();

					float rotation = temp.set(x-offsetX, y-tempActor.getOriginY()).angle()-90;
					
					if(tempActor.getRotation()+rotation >= 360){
						rotation-=360;
					}
					else if(tempActor.getRotation()+rotation < 0){
						rotation+=360;
					}
					tempActor.rotateBy(rotation);
				}
			});
		}
		
		updateLabel();
	}
	
	public Node getOriginNode(){
		return origin;
	}
	
	public Node getDestinationNode(){
		return destination;
	}

	public String getOriginName(){
		return origin.getName();
	}
	
	public String getDestinationName(){
		return destination.getName();
	}
	
	public void updateLabel(){
		String[] rules = transition.getTransitionRulesAsStrings();
		String text = "";
		for(String aRule : rules){
			if(text.equals("") == false){
				text += ", ";
			}
			text +=aRule;
		}
		
		edgeLabel.setText(text);
	}
	
	public void animateTransition(){
		curve.setAnimation(true);
	}
	
	@Override
	public void drawDebug(ShapeRenderer shapeRenderer){
		curve.drawDebug(shapeRenderer);
		/*
		this.drawDebugBounds(shapeRenderer);
		this.applyTransform(shapeRenderer, this.computeTransform());
		this.drawDebugChildren(shapeRenderer);
		this.resetTransform(shapeRenderer);*/
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha){
		this.applyTransform(batch, this.computeTransform());
		batch.setShader(fontShader);
		this.drawChildren(batch, parentAlpha);
		batch.setShader(null);
		this.resetTransform(batch);
	}
	
	@Override
	public void act(float delta){

		if(origin == destination){
			updateLoop();
		}
		else if(destination.hasEdgeTo(origin) == false){
			updateLine();
		}
		else{
			updateCurve();
		}
	}
	
	private void updateLoop(){
		boolean changed = false;
		
		if(curve.getConnectionType() == ConnectionType.Loop){
			changed = true;
		}
		
		curve.setConnectionType(ConnectionType.Loop);
		
		if(changed == false){
			return;
		}
		
		setOrigin(origin.getInnerRadius(), -origin.getInnerRadius()/2);
		setSize(origin.getInnerRadius()*2f, origin.getInnerRadius()*3f);
		setPosition(origin.getX(), origin.getY()+origin.getInnerRadius()*1.5f);
		
		edgeLabels.setBounds(0, origin.getInnerRadius()*1f, this.getWidth(), origin.getInnerRadius()*2f);
		edgeVector.set(0, 1);
		edgeVector.setLength(origin.getInnerRadius()*2.5f);
		edgeVector.setAngle(this.getRotation()+90);
		edgeLabels.setOrigin(Align.center);
		
		rotateLabels();
		curve.calculateConnection();
	}
	
	private void updateLine(){
		boolean changed = false;
		
		if(curve.getConnectionType() != ConnectionType.Line){
			changed = true;
		}
		
		curve.setConnectionType(ConnectionType.Line);
		
		float updatedX = destination.getX()-origin.getX();
		float updatedY = destination.getY()-origin.getY();
		
		//Don't update positions if positions did not change.
		if(updatedX == edgeVector.x && updatedY == edgeVector.y && changed == false){
			return;
		}
		
		edgeVector.set(updatedX, updatedY);

		calculatingVector.set(edgeVector);
		calculatingVector.setLength(origin.getInnerRadius());
		float x = origin.getMidX()+calculatingVector.x;
		float y = origin.getMidY()+calculatingVector.y;

		setBounds(x, y, edgeVector.len()-origin.getInnerRadius()-destination.getInnerRadius(), 32);
		setRotation(edgeVector.angle());
		edgeLabels.setOrigin(Align.center);
		edgeLabels.setBounds(0, 0, this.getWidth(), this.getHeight());
		
		rotateLabels();
		curve.calculateConnection();
	}
	
	private void updateCurve(){
		boolean changed = false;
		
		if(curve.getConnectionType() == ConnectionType.Curve){
			changed = true;
		}
		
		curve.setConnectionType(ConnectionType.Curve);
		
		float updatedX = destination.getX()-origin.getX();
		float updatedY = destination.getY()-origin.getY();
		
		//Don't update positions if positions did not change.
		if(updatedX == edgeVector.x && updatedY == edgeVector.y && changed == false){
			return;
		}
		
		edgeVector.set(updatedX, updatedY);
		
		calculatingVector.set(edgeVector);
		calculatingVector.setLength(origin.getInnerRadius());
		float x = origin.getMidX()+calculatingVector.x;
		float y = origin.getMidY()+calculatingVector.y;

		setBounds(x, y, edgeVector.len()-origin.getInnerRadius()-destination.getInnerRadius(), 48);
		setRotation(edgeVector.angle());
		edgeLabels.setOrigin(Align.center);
		edgeLabels.setBounds(0, 10, this.getWidth(), this.getHeight());
			
		rotateLabels();
		curve.calculateConnection();
	}
	
	private void rotateLabels(){
		if(edgeVector.angle() > 90 && edgeVector.angle() < 270){
			edgeLabels.setRotation(180);
		}
		else
		{
			edgeLabels.setRotation(0);
		}
	}
	
	@Override
	public EnumSet<ConstructionMenuOption> getMenuOptions() {
		return EnumSet.of(ConstructionMenuOption.Close, ConstructionMenuOption.SetSymbol);
	}

	@Override
	public void setSelectedOption(ConstructionMenuOption option, CharSequence text) {
		String newRule = "";
		boolean inRules = false;
		String[] currentRules = transition.getTransitionRulesAsStrings();
		for(String aRule : currentRules){
			if(aRule.equals(text.toString())){
				inRules = true;
			}
			else{
				newRule+=aRule + " ";
			}
		}
		
		if(inRules == false){
			newRule += text.toString();
		}
	
		((ConstructionStage)this.getStage()).sendCommand("updatetransition "+origin.getName()+" "+destination.getName()+" "+newRule);
	}

	@Override
	public void setActive() {
		this.setTouchable(Touchable.enabled);
	}

	@Override
	public void setInactive() {
		this.setTouchable(Touchable.disabled);
	}

	@Override
	public float preferredMenuPositionX() {
		Vector2.X.x = edgeLabels.getOriginX();
		Vector2.X.y = edgeLabels.getOriginY();
		return edgeLabels.localToStageCoordinates(Vector2.X).x;
	}

	@Override
	public float preferredMenuPositionY() {
		Vector2.X.x = edgeLabels.getOriginX();
		Vector2.X.y = edgeLabels.getOriginY();
		return edgeLabels.localToStageCoordinates(Vector2.X).y;
	}
	
	@Override
	public String getSelectedOptions(){
		String options = "";
		for(String aRule : transition.getTransitionRulesAsStrings()){
			options+=ConstructionMenuOption.SetSymbol.toString()+aRule+",";
		}
		return options;
	}
}
