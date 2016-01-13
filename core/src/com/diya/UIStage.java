package com.diya;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.badlogic.gdx.utils.viewport.Viewport;

import diya.controller.handler.DiyaController;
import diya.view.DiyaViewInterface;

public class UIStage extends Stage{

	final static TextureRegionDrawable undoGfx;
	final static TextureRegionDrawable redoGfx;
	final static TextureRegionDrawable settingsGfx;

	static{
		undoGfx = new TextureRegionDrawable(new TextureRegion(new Texture("ButtonUIUndo.png")));
		redoGfx = new TextureRegionDrawable(new TextureRegion(new Texture("ButtonUIRedo.png")));
		settingsGfx = new TextureRegionDrawable(new TextureRegion(new Texture("ButtonUISettings.png")));
	}
	
	final DiyaViewInterface view;
	final DiyaController controller;
	Console console;
	Skin skin;
	
	public UIStage(Viewport viewport, final DiyaViewInterface view, final DiyaController controller){
		super(viewport);
		
		this.skin = new Skin(Gdx.files.internal("uiskin.json"));
		this.view = view;
		this.controller = controller;
		
        Label.LabelStyle fpsStyle = new Label.LabelStyle();
        fpsStyle.font = new BitmapFont();
        fpsStyle.fontColor = Color.GRAY;
        final Label fpsLabel = new Label(String.valueOf(Gdx.graphics.getFramesPerSecond()), fpsStyle);
        fpsLabel.setPosition(viewport.getScreenWidth()-20, viewport.getScreenHeight()-fpsLabel.getStyle().font.getLineHeight()-5);
        fpsLabel.addAction(new Action(){

			@Override
			public boolean act(float delta) {
				fpsLabel.setText(String.valueOf(Gdx.graphics.getFramesPerSecond()));
				return false;
			}
        	
        });
        fpsLabel.setAlignment(Align.center);
        fpsLabel.setWidth(100);
 
        console = new Console(this, skin, 8);
        console.setVisible(false);
        //this.addActor(console);
        
        this.addListener(new InputListener(){
        	@Override
        	public boolean keyDown(InputEvent event, int keycode){
        		if(Keys.F3 == keycode){
        			console.setVisible(!console.isVisible());
        			if(console.isVisible()){
        				event.getStage().setKeyboardFocus(console.getConsoleInput());
        			}
        			return true;
        		}
        		
        		return false;
        	}
        });
        
        Table uiTable = new Table();
        uiTable.setFillParent(true);
        //uiTable.setDebug(true);
        uiTable.top();
        
        this.addActor(uiTable);
        
        Button undoButton = new Button(undoGfx);
        undoButton.setColor(Color.BLACK);
        undoButton.addListener(new ClickListener(){
        	@Override
        	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
        		if(button == Input.Buttons.LEFT){
	        		controller.actionPerformed(view, "undo");
	        		event.getListenerActor().setColor(Color.RED);
	        		return true;
        		}
        		
        		return false;
        	}
        	
        	@Override
         	public void touchUp(InputEvent event, float x, float y, int pointer, int button){
        		event.getListenerActor().setColor(Color.BLACK);
        	}
        });
        
        
        Button redoButton = new Button(redoGfx);
        redoButton.setColor(Color.BLACK);
        redoButton.addListener(new ClickListener(){
        	@Override
        	public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
        		if(button == Input.Buttons.LEFT){
	        		controller.actionPerformed(view, "redo");
	        		event.getListenerActor().setColor(Color.RED);
	        		return true;
        		}
        		
        		return false;
        	}
        	
        	@Override
         	public void touchUp(InputEvent event, float x, float y, int pointer, int button){
        		event.getListenerActor().setColor(Color.BLACK);
        	}
        });

        Button menuButton = new Button(settingsGfx);
        menuButton.addListener(new ClickListener(){
        	boolean switchColor = true;
        	
        	@Override
        	public void clicked(InputEvent event, float x, float y){
        		Gdx.input.setOnscreenKeyboardVisible(!console.isVisible());
        		console.setVisible(!console.isVisible());
        		event.getStage().setKeyboardFocus(console.getConsoleInput());
        		
        		if(switchColor){
        			event.getListenerActor().setColor(Color.RED);
        		}else{
        			event.getListenerActor().setColor(Color.WHITE);
        		}
        		
        		switchColor = !switchColor;
        	}
        });
        
        uiTable.add().width(100);
        uiTable.add().expandX();
        uiTable.add(undoButton).height(undoButton.getHeight());
        uiTable.add().width(10);
        uiTable.add(redoButton).width(undoButton.getWidth()).fill();
        uiTable.add().expandX();
        uiTable.add().width(100);
        uiTable.row();
        uiTable.add(console).colspan(uiTable.getColumns()).fill().top().expand();
        uiTable.row();
        uiTable.add(fpsLabel).width(100).center();
        uiTable.add().height(undoButton.getHeight()).colspan(5).expandX();
        uiTable.add(menuButton).height(undoButton.getHeight()).right();
	}
	
	public void printMessage(String message) {
		console.addMessage(message);
	}
	
	public void forwardCommand(String message){
		view.sendCommand(message);
	}
}
