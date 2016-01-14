package com.diya;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CameraWrapper {
	private int worldWidth;
	private int worldHeight;;
	//private float rotationSpeed;
	
	private OrthographicCamera cam;

	public CameraWrapper(int worldWidth, int worldHeight, int viewWidth, int viewHeight, float rotationSpeed, float aspectRatio){

		this.worldWidth = worldWidth;
		this.worldHeight = worldHeight;

		//this.rotationSpeed = rotationSpeed;
		
		this.cam = new OrthographicCamera(viewWidth, viewHeight * aspectRatio);
		//this.cam.position.set(cam.viewportWidth / 2f-10, cam.viewportHeight / 2f-10, 0);

		this.cam.zoom = 1f;
		this.cam.update();
	}
	
	public CameraWrapper(int worldWidth, int worldHeight, int viewWidth, int viewHeight){
		this(worldWidth, worldHeight, viewWidth, viewHeight, 2, 1);
	}
	
	public CameraWrapper(int worldWidth, int worldHeight){
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
        if(Gdx.input.isKeyPressed(Input.Keys.R)){
        	cam.zoom = 1;
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
	
	public Matrix4 getProjectionMatrix(){
		return cam.combined;
	}

	public int getWorldWidth(){
		return this.worldWidth;
	}
	
	public int getWorldHeight(){
		return this.worldHeight;
	}
	
	public OrthographicCamera getCamera(){
		return cam;
	}
	
	private void updateCamera(){
		float maxzoom = (worldWidth+cam.viewportWidth/2)/(cam.viewportWidth);
        cam.zoom = MathUtils.clamp(cam.zoom, 0.2f, maxzoom);
		
        float effectiveViewportWidth = cam.viewportWidth * cam.zoom;
        float effectiveViewportHeight = cam.viewportHeight * cam.zoom;

        cam.position.x = MathUtils.clamp(cam.position.x, effectiveViewportWidth / 2f-cam.viewportWidth/2f, worldWidth+cam.viewportWidth/2- effectiveViewportWidth / 2f);
        cam.position.y = MathUtils.clamp(cam.position.y, effectiveViewportHeight / 2f-cam.viewportHeight/2f, worldHeight+cam.viewportHeight/2- effectiveViewportHeight / 2f);
        
        this.cam.update();
	}
}
