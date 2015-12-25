package diya.model.automata.components;

public abstract class Component {
	private float posX;
	private float posY;
	
	public Component(float x, float y){
		this.posX = x;
		this.posY = y;
	}
	
	public float getX(){
		return posX;
	}
	
	public float getY(){
		return posY;
	}
	
	public void setPos(float x, float y){
		this.posX = x;
		this.posY = y;
	}
}
