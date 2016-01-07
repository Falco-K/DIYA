package com.diya;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.diya.graph.*;

public class ConstructionMenu extends Group {

	final static TextureRegion menuCircle;
	
	final static TextureRegionDrawable setSymbolButtonGfx;
	final static TextureRegionDrawable addSymbolButtonGfx;
	final static TextureRegionDrawable clearButtonGfx;
	final static TextureRegionDrawable closeButtonGfx;
	final static TextureRegionDrawable removeButtonGfx;
	final static TextureRegionDrawable setFinalButtonGfx;
	final static TextureRegionDrawable setInitialButtonGfx;
	
	static{
		menuCircle = new TextureRegion(new Texture(Gdx.files.internal("MenuCircle.png")));
		
		setSymbolButtonGfx = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ButtonMenuSetSymbol.png"))));
		addSymbolButtonGfx = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ButtonMenuAddSymbol.png"))));
		clearButtonGfx = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ButtonMenuClear.png"))));
		closeButtonGfx = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ButtonMenuClose.png"))));
		removeButtonGfx = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ButtonMenuRemove.png"))));
		setFinalButtonGfx = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ButtonMenuSetFinal.png"))));
		setInitialButtonGfx = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ButtonMenuSetInitial.png"))));
	}
	
	ConstructionMenuInterface currentObject;
	Skin skin;
	HashMap<String, Button> possibleButtons;
	ArrayList<Button> currentButtons;
	String[] symbols;
	ArrayList<String> output;
	
	Circle circle;
	
	Vector2 calculatingVector;
	
	public ConstructionMenu(String[] symbols){
		this.setVisible(false);
		
		this.skin = new Skin(Gdx.files.internal("uiskin.json"));
		this.circle = new Circle();
		this.circle.setRadius(64);
		this.symbols = symbols;
		this.output = new ArrayList<String>();
		
		this.setBounds(0, 0, 128, 128);
		this.setTransform(false);
		this.setOrigin(Align.center);
		
		this.setTouchable(Touchable.childrenOnly);
		
		this.possibleButtons = new HashMap<String, Button>();
		this.currentButtons = new ArrayList<Button>();
		this.calculatingVector = new Vector2();
		
		float buttonSize = 48;
		
		Button removeButton = new Button(removeButtonGfx);
		removeButton.setVisible(false);
		removeButton.setColor(Color.RED);
		removeButton.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if(currentObject instanceof Edge){
					Edge temp = (Edge) currentObject;
					((ConstructionStage)temp.getStage()).sendCommand("removetransition "+temp.getOriginName()+" "+temp.getDestinationName());
				}
				else if(currentObject instanceof Node){
					Node temp = (Node) currentObject;
					((ConstructionStage)temp.getStage()).sendCommand("removestate "+temp.getName());
				}
				
				((ConstructionMenu)event.getListenerActor().getParent()).close();
				event.stop();
				return true;
			}
		});
		
		this.addActor(removeButton);
		possibleButtons.put(ConstructionMenuOption.Remove.toString(), removeButton);
		
		Button closeButton = new Button(closeButtonGfx);
		closeButton.setVisible(false);
		closeButton.setColor(Color.GREEN);
		closeButton.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){				
				((ConstructionMenu)event.getListenerActor().getParent()).close();
				event.stop();
				return true;
			}
		});
		
		this.addActor(closeButton);
		possibleButtons.put(ConstructionMenuOption.Close.toString(), closeButton);
		
		Button setInitialButton = new Button(setInitialButtonGfx);
		setInitialButton.setVisible(false);
		setInitialButton.addListener(new ClickListener(){
			boolean switchColor;
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				currentObject.setSelectedOption(ConstructionMenuOption.SetStart, "");
				((ConstructionMenu)(event.getListenerActor().getParent())).setSelectedOptions();
				event.stop();
				return true;
			}
		});
		this.addActor(setInitialButton);
		possibleButtons.put(ConstructionMenuOption.SetStart.toString(), setInitialButton);
		
		Button setFinalButton = new Button(setFinalButtonGfx);
		setFinalButton.setVisible(false);
		setFinalButton.addListener(new ClickListener(){
			boolean switchColor;
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				currentObject.setSelectedOption(ConstructionMenuOption.SetFinal, "");
				((ConstructionMenu)(event.getListenerActor().getParent())).setSelectedOptions();
				event.stop();
				return true;
			}
		});
		this.addActor(setFinalButton);
		possibleButtons.put(ConstructionMenuOption.SetFinal.toString(), setFinalButton);
		
		Button clearButton = new Button(clearButtonGfx);
		clearButton.setVisible(false);
		clearButton.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				currentObject.setSelectedOption(ConstructionMenuOption.Clear, "");
				event.getListenerActor().setColor(Color.RED);
				event.stop();
				return true;
			}
			
			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button){
				event.getListenerActor().setColor(Color.WHITE);
			}
		});
		this.addActor(clearButton);
		possibleButtons.put(ConstructionMenuOption.Clear.toString(), clearButton);
		
		
		
		TextButton.TextButtonStyle textButtonStyleSetSymbol = new TextButton.TextButtonStyle();
		textButtonStyleSetSymbol.up = setSymbolButtonGfx;
		textButtonStyleSetSymbol.font = skin.getFont("default-font");
		
		TextButton.TextButtonStyle textButtonStyleAddSymbol = new TextButton.TextButtonStyle();
		textButtonStyleAddSymbol.up = addSymbolButtonGfx;
		textButtonStyleAddSymbol.font = skin.getFont("default-font");

		
		for(String aSymbol : symbols){
			TextButton temp = new TextButton(aSymbol, textButtonStyleSetSymbol);
			temp.align(Align.center);
			temp.setVisible(false);
			temp.setSize(buttonSize, buttonSize);
			temp.addListener(new ClickListener(){
				boolean switchColor;
				
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
					currentObject.setSelectedOption(ConstructionMenuOption.SetSymbol, ((TextButton)event.getListenerActor()).getText());
					((ConstructionMenu)(event.getListenerActor().getParent())).setSelectedOptions();
					
					event.stop();
					return true;
				}
			});
			this.addActor(temp);
			possibleButtons.put(ConstructionMenuOption.SetSymbol.toString()+aSymbol, temp);
			
			TextButton temp2 = new TextButton(aSymbol, textButtonStyleAddSymbol);
			temp2.setVisible(false);
			temp2.setSize(buttonSize, buttonSize);
			temp2.addListener(new ClickListener(){			
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
					currentObject.setSelectedOption(ConstructionMenuOption.AddSymbol, ((TextButton)event.getListenerActor()).getText());
					event.getListenerActor().setColor(Color.RED);
					
					event.stop();
					return true;
				}
				
				@Override
				public void touchUp(InputEvent event, float x, float y, int pointer, int button){
					event.getListenerActor().setColor(Color.WHITE);
				}
			});
			this.addActor(temp2);
			possibleButtons.put(ConstructionMenuOption.AddSymbol.toString()+aSymbol, temp2);
		}
		
		TextButton temp = new TextButton("\u03B5", textButtonStyleSetSymbol);
		temp.setVisible(false);
		temp.addListener(new ClickListener(){
			boolean switchColor;
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				currentObject.setSelectedOption(ConstructionMenuOption.SetSymbol, ((TextButton)event.getListenerActor()).getText());
				((ConstructionMenu)(event.getListenerActor().getParent())).setSelectedOptions();
				
				event.stop();
				return true;
			}
		});
		this.addActor(temp);
		possibleButtons.put(ConstructionMenuOption.SetEmptyWord.toString(), temp);
	}
	
	@Override
	public void positionChanged(){
		setCurrentObjectActive();
	}
	
	@Override
	public void sizeChanged(){
		circle.setRadius(this.getWidth()/2);
	}
	
	@Override
	public void act(float delta){
		if(currentObject == null || this.isVisible() == false){
			return;
		}
		
		int buttonCount = currentButtons.size();
		calculatingVector.set(0, -circle.radius);
		calculatingVector.rotate(-45);
		this.setPosition(currentObject.preferredMenuPositionX()-circle.radius, currentObject.preferredMenuPositionY()-circle.radius);
		
		for(Button currentButton : currentButtons){
			currentButton.setPosition(circle.radius+calculatingVector.x-currentButton.getWidth()/2, circle.radius+calculatingVector.y-currentButton.getHeight()/2);
			currentButton.setVisible(true);
			currentButton.toFront();
			calculatingVector.rotate(360/buttonCount);
		}
		
	}
	
	@Override
	public void drawDebug(ShapeRenderer shapeRenderer){
		this.drawDebugChildren(shapeRenderer);
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha){
		if(!this.isVisible()) return;
		
		batch.begin();
		batch.setColor(Color.RED);
		batch.draw(menuCircle, this.getX(), this.getY());
		batch.setColor(Color.WHITE);
		this.drawChildren(batch, parentAlpha);
		batch.end();
		batch.setColor(Color.WHITE);
	}
	
	public void addOutput(String output){
		this.output.add(output);
	}
	
	public void open(float x, float y, ConstructionMenuInterface currentObject){
		this.currentObject = currentObject;
		enable();
	}
	
	public void close(){
		disable();
	}
	
	public boolean isEnabled(){
		return this.isVisible();
	}
	
	private void setSeletableOptions(){
		EnumSet<ConstructionMenuOption> options = currentObject.getMenuOptions();
		clearCurrentButtons();
		
		for(ConstructionMenuOption option : options){
			if(option == ConstructionMenuOption.SetSymbol || option == ConstructionMenuOption.AddSymbol){
				for(String symbol : symbols){
					Button temp = possibleButtons.get(option.toString()+symbol);
					currentButtons.add(temp);
				}
			}
			else{
				currentButtons.add(possibleButtons.get(option.toString()));
			}
		}
	}
	
	private void clearCurrentButtons(){
		for(Button aButton : currentButtons){
			aButton.setVisible(false);
		}
		
		currentButtons.clear();
	}
	
	private void enable(){
		//this.toFront();
		this.setVisible(true);
		this.output.clear();
		this.toFront();
		this.setSeletableOptions();
		this.setSelectedOptions();
	}
	
	private void disable(){
		this.setVisible(false);
	}
	
	private void setCurrentObjectActive(){
		if(currentObject != null){
			currentObject.setActive();
		}
	}

	private void setSelectedOptions(){
		Set<String> keys = possibleButtons.keySet();
		for(String aButton : keys){
			if(aButton.equals(ConstructionMenuOption.Close.toString())){
				possibleButtons.get(aButton).setColor(Color.GREEN);
			}
			else{
				possibleButtons.get(aButton).setColor(Color.WHITE);
			}
		}
		
		String constructionMenuOptions = currentObject.getSelectedOptions();
		if(constructionMenuOptions != null && constructionMenuOptions.equals("") == false){
			for(String option : constructionMenuOptions.split(",")){
				if(possibleButtons.keySet().contains(option) == false){
					break;
				}

				possibleButtons.get(option).setColor(Color.RED);
			}
		}
	}
}
