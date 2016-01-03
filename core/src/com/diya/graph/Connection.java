package com.diya.graph;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Bezier;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Connection extends Actor{

	static final int ARROW_HEAD_SIZE = 16;
	static final int ARROW_HEAD_ANGLE = 30;
	static final int ARROW_HEAD_OFFSET = 32;
	

	static final Vector2 lineStart;
	static final Vector2 calculatingVector;

	static final Vector2 arrowLeftSide;
	static final Vector2 arrowRightSide;
	static final Vector2 arrowCenter;
	static final Vector2 arrowStart;
	
	final Bezier<Vector2> curve;
	final Vector2 edgeVector;
	
	final Vector2 firstPoint;
	final Vector2 secondPoint;
	final Vector2 thirdPoint;
	final Vector2 fourthPoint;
	
	float arrowStartX;
	float arrowStartY;
	float arrowEndX;
	float arrowEndY;
	float arrowRightX;
	float arrowRightY;
	float arrowLeftX;
	float arrowLeftY;
	
	Node origin;
	Node destination;
	
	ConnectionType type;
	
	static{
		arrowLeftSide = new Vector2();
		arrowRightSide = new Vector2();
		arrowCenter = new Vector2();
		arrowStart = new Vector2();
		
		lineStart = new Vector2();
		calculatingVector = new Vector2();
	}
	
	public Connection(Node origin, Node destination, Vector2 edgeVector){
		this.curve = new Bezier<Vector2>();
		firstPoint = new Vector2();
		secondPoint = new Vector2();
		thirdPoint = new Vector2();
		fourthPoint = new Vector2();
		
		this.edgeVector = edgeVector;

		this.origin = origin;
		this.destination = destination;
	}
	
	public void setConnectionType(ConnectionType type){
		this.type = type;
		calculateConnection();
	}
	
	public ConnectionType getConnectionType(){
		return this.type;
	}
	
	public void calculateConnection(){
		if(type == ConnectionType.Loop){
			calculateLoop();
		}
		else if(type == ConnectionType.Line){
			calculateLine();
		}
		else if(type == ConnectionType.Curve){	
			calculateCurve();
		}
		
	}
	
	public void calculateLoop(){
		firstPoint.set(origin.getMidX(), origin.getMidY());
		
		float midX = origin.getMidX()+edgeVector.x;
		float midY = origin.getMidY()+edgeVector.y;
		
		calculatingVector.set(edgeVector);
		calculatingVector.setLength(calculatingVector.len());
		calculatingVector.setAngle(calculatingVector.angle()+90);
		secondPoint.set(midX+calculatingVector.x, midY+calculatingVector.y);
		calculatingVector.setAngle(calculatingVector.angle()+180);
		
		thirdPoint.set(midX+calculatingVector.x, midY+calculatingVector.y);
		
		fourthPoint.set(destination.getMidX(), destination.getMidY());
		
		curve.set(firstPoint, secondPoint, thirdPoint, fourthPoint);

		curve.valueAt(calculatingVector, 0.88f);
		calculateArrowHead(18, calculatingVector.x-fourthPoint.x, calculatingVector.y-fourthPoint.y);
	}
	
	public void calculateLine(){
		calculateArrowHead(0, -edgeVector.x, -edgeVector.y);
	}
	
	public void calculateCurve(){
		firstPoint.set(origin.getMidX(), origin.getMidY());
		
		secondPoint.set(edgeVector);
		secondPoint.setLength(secondPoint.len()/2);
		
		float midX = secondPoint.x+origin.getMidX();
		float midY = secondPoint.y+origin.getMidY();
		
		secondPoint.scl(-1);
		secondPoint.setAngle(secondPoint.angle()-90);
		secondPoint.setLength(40);
		secondPoint.set(midX+secondPoint.x, midY+secondPoint.y);
		
		thirdPoint.set(destination.getMidX(), destination.getMidY());
		
		curve.set(firstPoint, secondPoint, thirdPoint);
		curve.valueAt(calculatingVector, 1-(destination.getOuterRadius()/curve.approxLength(2)));

		calculateArrowHead(0, calculatingVector.x-thirdPoint.x, calculatingVector.y-thirdPoint.y);
	}
	
	private void calculateArrowHead(float rotation, float arrowOffsetX, float arrowOffsetY){
		arrowCenter.set(arrowOffsetX, arrowOffsetY);
		arrowCenter.setLength(destination.getOuterRadius());
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
		
		arrowStartX = arrowStart.x;
		arrowStartY = arrowStart.y;
		arrowEndX = arrowStart.x+arrowCenter.x;
		arrowEndY = arrowStart.y+arrowCenter.y;
		arrowLeftX = arrowStart.x+arrowLeftSide.x;
		arrowLeftY = arrowStart.y+arrowLeftSide.y;
		arrowRightX = arrowStart.x+arrowRightSide.x;
		arrowRightY = arrowStart.y+arrowRightSide.y;
	}
	
	@Override
	public void drawDebug(ShapeRenderer shapeRenderer){	
		if(this.type == ConnectionType.Loop){
			drawCurve(shapeRenderer, 0.05f);
		}
		else if(this.type == ConnectionType.Line){
			drawLine(shapeRenderer);
		}
		else if(this.type == ConnectionType.Curve){
			drawCurve(shapeRenderer, 0.1f);
		}
		
		this.drawArrowHead(shapeRenderer);
	}
	
	private void drawLine(ShapeRenderer shapeRenderer){
		shapeRenderer.line(origin.getMidX(), origin.getMidY(), destination.getMidX(), destination.getMidY());
	}
	
	private void drawCurve(ShapeRenderer shapeRenderer, float distance){
		lineStart.set(curve.points.get(0));
		
		for(float i = distance; i <= 1+distance; i+=distance)
		{
			curve.valueAt(calculatingVector, i);
			shapeRenderer.line(lineStart, calculatingVector);
			lineStart.set(calculatingVector.x, calculatingVector.y);
		}
	}
	
	private void drawArrowHead(ShapeRenderer shapeRenderer){
		//Left half of the arrow
		shapeRenderer.triangle(arrowStartX, arrowStartY, arrowEndX, arrowEndY, arrowLeftX, arrowLeftY);
		//Right half of the arrow
		shapeRenderer.triangle(arrowStartX, arrowStartY, arrowEndX, arrowEndY, arrowRightX, arrowRightY);
	}
}
