package com.diya.graph;

import java.util.EnumSet;

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
	
	static final int ARROW_HEAD_SIZE = 16;
	static final int ARROW_HEAD_ANGLE = 30;
	static final int ARROW_HEAD_OFFSET = 32;
	
	Transition transition;
	Node origin;
	Node destination;
	Group edgeLabels;
	Label edgeLabel;
	Connection curve;
	
	Vector2 edgeVector;
	Vector2 boxBegin;
	
	public Edge(Transition transition, Node origin, Node destination){
		this.transition = transition;

		this.origin = origin;
		this.destination = destination;
		edgeVector = new Vector2();
		boxBegin = new Vector2();

		
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
				
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
					if(button == Input.Buttons.LEFT){
						return true;
					}
					
					return false;
				}
				
				@Override
				public void touchDragged(InputEvent event, float x, float y, int pointer){
					Actor tempActor = (Edge)event.getListenerActor();
					float rotation = temp.set(x-tempActor.getOriginX(), y-tempActor.getOriginY()).angle()-90;
					
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
		boolean changed = false;
		
		if(origin == destination){
			if(curve.getConnectionType() == ConnectionType.Loop){
				changed = true;
			}
			curve.setConnectionType(ConnectionType.Loop);
			
			if(changed == false){
				return;
			}
			
			this.setOrigin(origin.getRadius(), -origin.getRadius()/2);
			this.setSize(origin.getRadius()*2, origin.getRadius()*2f);
			this.setPosition(origin.getX(), origin.getY()+origin.getRadius()*1.5f);
			this.edgeLabels.setBounds(0, this.getHeight()/2, this.getWidth(), this.getHeight());
			this.edgeVector.set(0, 1);
			this.edgeVector.setLength(origin.getRadius()*2.5f);
			this.edgeVector.setAngle(this.getRotation()+90);
			this.edgeLabels.setOrigin(Align.center);
			if((this.getRotation()) > 90 && (this.getRotation()) < 270){
				this.edgeLabels.setRotation(180);
			}
			else{
				this.edgeLabels.setRotation(0);
			}
			
			this.curve.calculateCurve();
		}
		else if(destination.hasEdgeTo(origin) == false){
			if(curve.getConnectionType() == ConnectionType.Line){
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

			boxBegin.set(edgeVector);
			boxBegin.setLength(origin.getRadius());
			float x = origin.getX()+origin.getRadius()+boxBegin.x;
			float y = origin.getY()+origin.getRadius()+boxBegin.y;
			boxBegin.setLength(origin.getRadius()/2);
			boxBegin.setAngle(edgeVector.angle()-90);

			this.setBounds(x, y, edgeVector.len()-origin.getRadius()-destination.getRadius(), 32);
			this.setRotation(edgeVector.angle());
			
			if(edgeVector.angle() > 90 && edgeVector.angle() < 270){
				this.edgeLabels.setOrigin(Align.center);
				this.edgeLabels.setBounds(0, 0, this.getWidth(), this.getHeight());
				edgeLabels.setRotation(180);
			}
			else
			{
				this.edgeLabels.setOrigin(Align.center);
				this.edgeLabels.setBounds(0, 0, this.getWidth(), this.getHeight());
				edgeLabels.setRotation(0);
			}
			
			this.curve.calculateCurve();
		}
		else{
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
			
			this.curve.calculateCurve();
			
			boxBegin.set(edgeVector);
			boxBegin.setLength(origin.getRadius());
			float x = origin.getX()+origin.getRadius()+boxBegin.x;
			float y = origin.getY()+origin.getRadius()+boxBegin.y;
			boxBegin.setLength(origin.getRadius()/2);
			boxBegin.setAngle(edgeVector.angle()-90);
			
			this.setBounds(x, y, edgeVector.len()-origin.getRadius()-destination.getRadius(), 48);
			this.setRotation(edgeVector.angle());
				
			if(edgeVector.angle() > 90 && edgeVector.angle() < 270){
				this.edgeLabels.setBounds(0, 0, this.getWidth(), this.getHeight());
				this.edgeLabels.setOrigin(Align.center);
				edgeLabels.setRotation(180);
			}
			else
			{
				this.edgeLabels.setBounds(0, 10, this.getWidth(), this.getHeight());
				this.edgeLabels.setOrigin(Align.center);
				edgeLabels.setRotation(0);
			}
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
	
	class Connection extends Actor{		
		Bezier<Vector2> curve;
		Vector2 lineStart;
		Vector2 lineEnd;
		Vector2 firstPoint;
		Vector2 secondPoint;
		Vector2 thirdPoint;
		Vector2 fourthPoint;
		Vector2 calculatingVector;
		Vector2 edgeVector;
		
		Vector2 boxBegin;
		
		Vector2 arrowLeftSide;
		Vector2 arrowRightSide;
		Vector2 arrowCenter;
		Vector2 arrowStart;
		
		Node origin;
		Node destination;
		
		ConnectionType type;
		
		public Connection(Node origin, Node destination, Vector2 edgeVector){
			this.curve = new Bezier<Vector2>();
			
			this.lineStart = new Vector2();
			this.lineEnd = new Vector2();
			this.firstPoint = new Vector2();
			this.secondPoint = new Vector2();
			this.thirdPoint = new Vector2();
			this.fourthPoint = new Vector2();
			this.calculatingVector = new Vector2();
			this.edgeVector = edgeVector;
			
			arrowLeftSide = new Vector2();
			arrowRightSide = new Vector2();
			arrowCenter = new Vector2();
			arrowStart = new Vector2();
			boxBegin = new Vector2();
			
			this.origin = origin;
			this.destination = destination;
		}
		
		public void setConnectionType(ConnectionType type){
			this.type = type;
		}
		
		public ConnectionType getConnectionType(){
			return this.type;
		}
		
		public void calculateCurve(){
			
			if(type == ConnectionType.Loop){
				this.firstPoint.set(origin.getMidX(), origin.getMidY());
				
				float midX = origin.getMidX()+edgeVector.x;
				float midY = origin.getMidY()+edgeVector.y;
				
				calculatingVector.set(edgeVector);
				calculatingVector.setLength(calculatingVector.len());
				calculatingVector.setAngle(calculatingVector.angle()+90);
				this.secondPoint.set(midX+calculatingVector.x, midY+calculatingVector.y);
				calculatingVector.setAngle(calculatingVector.angle()+180);
				this.thirdPoint.set(midX+calculatingVector.x, midY+calculatingVector.y);
				
				this.fourthPoint.set(origin.getMidX(), origin.getMidY());
				
				curve.set(firstPoint, secondPoint, thirdPoint, fourthPoint);

				curve.valueAt(arrowCenter, 0.88f);
				arrowCenter.set(fourthPoint.x - arrowCenter.x, fourthPoint.y - arrowCenter.y);
				//arrowCenter.set(-arrowCenter.x+origin.getMidX(), -arrowCenter.y+origin.getMidY());
				calculateArrowHead(18);
			}
			else if(type == ConnectionType.Line){
				arrowCenter.set(edgeVector);
				arrowCenter.setLength(destination.getRadius());
				calculateArrowHead(0);
			}
			else{	
				this.firstPoint.set(origin.getX()+origin.getRadius(), origin.getY()+origin.getRadius());
				
				this.secondPoint.set(edgeVector);
				this.secondPoint.setLength(this.secondPoint.len()/2);
				
				float midX = this.secondPoint.x+this.origin.getX()+this.origin.getRadius();
				float midY = this.secondPoint.y+this.origin.getY()+this.origin.getRadius();
				
				this.secondPoint.scl(-1);
				this.secondPoint.setAngle(this.secondPoint.angle()-90);
				this.secondPoint.setLength(30);
				this.secondPoint.set(midX+this.secondPoint.x, midY+this.secondPoint.y);
				
				this.thirdPoint.set(destination.getX()+destination.getRadius(), destination.getY()+destination.getRadius());
				
				curve.set(firstPoint, secondPoint, thirdPoint);
				curve.valueAt(arrowCenter, 1-((destination.getRadius())/curve.approxLength(5)));
				arrowCenter.set(thirdPoint.x-arrowCenter.x, thirdPoint.y-arrowCenter.y);
				calculateArrowHead(0);
			}
		}
		
		private void calculateArrowHead(float rotation){	
			arrowCenter.scl(-1, -1);
			arrowCenter.setLength(destination.isFinal() ? destination.getRadius()+5 : destination.getRadius());
			arrowStart.set(destination.getMidX()+arrowCenter.x, destination.getMidY()+arrowCenter.y);
			
			arrowLeftSide.set(arrowCenter);
			arrowLeftSide.setLength(ARROW_HEAD_SIZE);
			arrowLeftSide.setAngle(arrowLeftSide.angle()+ARROW_HEAD_ANGLE);
			arrowLeftSide.rotate(rotation);
			
			arrowRightSide.set(arrowCenter);
			arrowRightSide.setLength(ARROW_HEAD_SIZE);
			arrowRightSide.setAngle(arrowRightSide.angle()-ARROW_HEAD_ANGLE);
			arrowRightSide.rotate(rotation);
			
			arrowCenter.setLength(ARROW_HEAD_SIZE/3);	
			arrowCenter.rotate(rotation);
		}
		
		@Override
		public void drawDebug(ShapeRenderer shapeRenderer){	
			if(this.type == ConnectionType.Curve){
				this.lineStart.set(firstPoint);
				
				for(float i = 0; i <= 1.1f; i+=0.1f)
				{
					curve.valueAt(lineEnd, i);
					shapeRenderer.line(lineStart, lineEnd);
					lineStart.set(lineEnd.x, lineEnd.y);
				}
			}
			else if(this.type == ConnectionType.Line){
				shapeRenderer.line(origin.getMidX(), origin.getMidY(), destination.getMidX(), destination.getMidY());
			}
			else if(this.type == ConnectionType.Loop){
				this.lineStart.set(firstPoint);
				
				for(float i = 0; i <= 1.05f; i+=0.05f)
				{
					curve.valueAt(lineEnd, i);
					shapeRenderer.line(lineStart, lineEnd);
					lineStart.set(lineEnd.x, lineEnd.y);
				}
			}
			
			this.drawArrowHead(shapeRenderer);
		}
		
		private void drawArrowHead(ShapeRenderer shapeRenderer){
			shapeRenderer.triangle(arrowStart.x, arrowStart.y, arrowStart.x+arrowCenter.x, arrowStart.y+arrowCenter.y, 
					arrowStart.x+arrowLeftSide.x, arrowStart.y+arrowLeftSide.y);
			
			shapeRenderer.triangle(arrowStart.x, arrowStart.y, arrowStart.x+arrowCenter.x, arrowStart.y+arrowCenter.y, 
					arrowStart.x+arrowRightSide.x, arrowStart.y+arrowRightSide.y);
		}
	}
}
