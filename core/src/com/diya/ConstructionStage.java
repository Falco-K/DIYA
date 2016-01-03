package com.diya;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.diya.graph.Edge;
import com.diya.graph.Node;

import diya.controller.handler.DiyaController;
import diya.view.DiyaViewInterface;

public class ConstructionStage extends Stage implements GestureListener{
	final DiyaViewInterface view;
	final DiyaController controller;
	final Camera camera;
	
	final int worldHeight;
	final int worldWidth;
	
	ConstructionMenu constructionMenu;
	ShapeRenderer shapeRenderer;
	
	int stateCounter;
	Vector2 temp;
	String lastNode;
	
	boolean didPan;
	float offsetX;
	float offsetY;
	Actor hitActor;
	
	public ConstructionStage(final Camera camera, final DiyaViewInterface view, final DiyaController controller){
		super(camera.getViewPort());
		
		this.view = view;
		this.controller = controller;
		this.camera = camera;
		
		this.worldWidth = camera.getWorldWidth();
		this.worldHeight = camera.getWorldHeight();
		
		this.shapeRenderer = new ShapeRenderer();
		this.temp = new Vector2();
		this.lastNode = "";
		
		this.addListener(new InputListener(){
			String firstNode = "";
			float offsetX;
			float offsetY;
			Actor clickedActor;
			boolean dragged = false;
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				clickedActor = event.getStage().hit(x, y, true);
				
				this.offsetX = x;
				this.offsetY = y;

				if(clickedActor != null){
					if(button == Input.Buttons.RIGHT){
						constructionMenu.close();
						if(clickedActor instanceof Node){
							view.sendCommand("removestate "+((Node)clickedActor).getName());
						}
						else if(clickedActor instanceof Edge){
							view.sendCommand("removetransition "+((Edge)clickedActor).getOriginName()+" "+((Edge)clickedActor).getDestinationName());
						}
						firstNode = "";
						return false;
					}
					else if(button == Input.Buttons.LEFT){
					}
				}
				
				return true;
			}

			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer){
				firstNode = "";
				if(clickedActor == null){
					((ConstructionStage)event.getListenerActor().getStage()).moveBy(offsetX-x, offsetY-y);
				}
				
				dragged = true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button){
				if(dragged){
					dragged = !dragged;
					return;
				}
				
				if(clickedActor == null){
					
					if(constructionMenu.isEnabled() == true){	
						constructionMenu.close();
						firstNode = "";
					}
					else{
						
						if(button == Input.Buttons.LEFT){
							view.sendCommand("addstate s"+stateCounter+" false false "+x+" "+y);
							stateCounter++;
						}
					}
				}
				else if(clickedActor instanceof ConstructionMenuInterface){
					if(button == Input.Buttons.LEFT){
						if(clickedActor instanceof Node){
							if(firstNode.equals("")){
								firstNode = ((Node)clickedActor).getName();
							}
							else{
								constructionMenu.open(x, y, (ConstructionMenuInterface) clickedActor);
								view.sendCommand("addtransition "+firstNode+ " "+((Node)clickedActor).getName());
								firstNode = ((Node)clickedActor).getName();
							}
						}
						
						constructionMenu.open(x, y, (ConstructionMenuInterface) clickedActor);
					}
				}
			}
			
			@Override
			public boolean scrolled(InputEvent event, float x, float y, int amount){
				if(amount > 0){
					camera.zoomDown();
				}
				else if(amount < 0){
					camera.zoomUp();
				}
				return true;
			}
		});
	}
	
	public void setMenu(ConstructionMenu menu){
		this.constructionMenu = menu;
		this.addActor(menu);
		menu.toFront();
	}
	
	public void moveBy(float x, float y){
		this.camera.moveCamera(x, y);
	}
	
	@Override
	public void act(float delta){
		camera.updateCameraPositionViaKeys();
		super.act(delta);
	}
	
	@Override
	public void draw(){
		drawBackground();
		super.draw();
	}
	
	@Override
	public void dispose(){
		shapeRenderer.dispose();
		super.dispose();
	}
	
	public void sendCommand(String command){
		view.sendCommand(command);
	}
	
	private void drawBackground(){
        shapeRenderer.setProjectionMatrix(camera.getProjectionMatrix());
        shapeRenderer.begin(ShapeType.Filled);
        drawDrawingArea(shapeRenderer);
        shapeRenderer.end();
        shapeRenderer.begin(ShapeType.Line);
        drawBorders(shapeRenderer);
        shapeRenderer.end();
	}
	
	private void drawDrawingArea(ShapeRenderer shapeRenderer){
        shapeRenderer.setColor(1, 1, 1, 1);
		shapeRenderer.box(0, 0, 0, worldWidth, worldHeight, 0);
	}
	
	private void drawBorders(ShapeRenderer shapeRenderer){
		shapeRenderer.setColor(0f,0f,0f,1f);
		shapeRenderer.box(0, 0, 0, worldWidth, worldHeight, 0);
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		screenToStageCoordinates(temp.set(x, y));
		hitActor = this.hit(temp.x, temp.y, true);
		offsetX = temp.x;
		offsetY = temp.y;
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		/*
		Gdx.app.log("coor", x+":"+y);
		screenToStageCoordinates(temp.set(x,y));
		
		Actor hitActor = this.hit(temp.x, temp.y, true);
		
		if(didPan == true){
			didPan = !didPan;
			return true;
		}
		
		if(hitActor == null){
			lastNode = "";
			
			if(button == Input.Buttons.LEFT){
				if(constructionMenu.isEnabled()){
					constructionMenu.close();
					return true;
				}
				else{
					view.sendCommand("addstate s"+stateCounter+" "+temp.x+" "+temp.y);
					stateCounter++;
					return true;
				}
			}
		}
		else{
			if(hitActor instanceof ConstructionMenuInterface && button == Input.Buttons.LEFT){
				constructionMenu.open(temp.x, temp.y, (ConstructionMenuInterface) hitActor);
				if(hitActor instanceof Node){
					if(lastNode.equals("")){
						lastNode = ((Node)hitActor).getName();
					}
					else{
						view.sendCommand("addtransition "+lastNode+" "+((Node)hitActor).getName()+" a");
						lastNode = ((Node)hitActor).getName();
					}
				}
				else{
					lastNode = "";
				}
				
				return true;
			}
		}*/
		
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		/*screenToStageCoordinates(temp.set(x, y));
		lastNode = "";
		if(hitActor == null){
			this.moveBy(-deltaX, deltaY);
			this.didPan = true;
			return true;
		}
		else{
			if(hitActor instanceof Node || hitActor instanceof InputTapeObject){
				hitActor.moveBy(deltaX, -deltaY);
			}
		}*/
		
		return false;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}

	float tempDistance = -1;
	float oldRatio = 1;
	@Override
	public boolean zoom(float initialDistance, float distance) {
	
		float ratio = initialDistance / (distance+(initialDistance-distance)*0.95f);
		camera.setZoom(ratio);
		return true;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}
}
