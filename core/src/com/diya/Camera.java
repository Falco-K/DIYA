package com.diya;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Camera {
	private int worldWidth;
	private int worldHeight;;
	//private float rotationSpeed;
	
	private OrthographicCamera cam;
	private FitViewport viewport;
	
	public Camera(int worldWidth, int worldHeight, int viewWidth, int viewHeight, float rotationSpeed, float aspectRatio){

		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;

		//this.rotationSpeed = rotationSpeed;
		
		this.cam = new OrthographicCamera(viewWidth, viewHeight * aspectRatio);
		//this.cam.position.set(cam.viewportWidth / 2f-10, cam.viewportHeight / 2f-10, 0);
		this.cam.position.set(0, 0, 0);
		this.cam.zoom = 1f;
		this.cam.update();
		
		this.viewport = new FitViewport(viewWidth, viewHeight, cam);
	}
	
	public Camera(int worldWidth, int worldHeight, int viewWidth, int viewHeight){
		this(worldWidth, worldHeight, viewWidth, viewHeight, 2, 1);
	}
	
	public Camera(int worldWidth, int worldHeight){
		this(worldWidth, worldHeight, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), 2, 1);
	}
	
	public void updateCameraPositionViaKeys(){
        if (Gdx.input.isKeyPressed(Input.Keys.PAGE_UP)) {
            cam.zoom += 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.PAGE_DOWN)) {
            cam.zoom -= 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            cam.translate(-3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            cam.translate(3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            cam.translate(0, -3, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            cam.translate(0, 3, 0);
        }
        /*
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            cam.rotate(-rotationSpeed, 0, 0, 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            cam.rotate(rotationSpeed, 0, 0, 1);
        }*/
        
        this.updateCamera();
	}
	
	public void zoomDown(){
		cam.zoom += 0.2;
		this.updateCamera();
	}
	
	public void zoomUp(){
		cam.zoom -= 0.2;
		this.updateCamera();
	}
	
	public void setZoom(float ratio){
		Gdx.app.log("old zoom", "zoom: "+cam.zoom);
		Gdx.app.log("Zoom", "ratio "+ratio);
		cam.zoom = cam.zoom * ratio;
		Gdx.app.log("new zoom", "Zoom: "+cam.zoom);
		this.updateCamera();
	}
	
	public void moveCamera(float x, float y){
		cam.translate(x, y);
		this.updateCamera();
	}
	
	public void updateViewport(float viewWidth, float viewHeight, int windowWidth, int windowHeight){
		/*
        cam.viewportWidth = viewWidth;
        cam.viewportHeight = viewHeight * aspectRatio;
        cam.update();*/
        viewport.update(windowWidth, windowHeight);
	}
	
	public void updateViewport(int windowWidth, int windowHeight){
		this.updateViewport(this.cam.viewportWidth, this.cam.viewportHeight, windowWidth, windowHeight);
	}
	
	public Matrix4 getProjectionMatrix(){
		return cam.combined;
	}
	
	public Viewport getViewPort(){
		return viewport;
	}
	
	public int getWorldWidth(){
		return this.worldWidth;
	}
	
	public int getWorldHeight(){
		return this.worldHeight;
	}
	
	private void updateCamera(){
        cam.zoom = MathUtils.clamp(cam.zoom, 0.1f,2);
		
        float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
        float effectiveViewportHeight = cam.viewportHeight * cam.zoom;

        cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f-cam.viewportWidth/2, worldWidth+cam.viewportWidth/2- effectiveViewportWidth / 2f);
        cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f-cam.viewportHeight/2, worldHeight+cam.viewportHeight/2- effectiveViewportHeight / 2f);
        
        this.cam.update();
	}
}
