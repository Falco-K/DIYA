package com.diya;

import java.util.ArrayList;
import java.util.Collection;
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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.SnapshotArray;
import com.diya.graph.*;

import diya.model.language.Alphabet;

public class ConstructionMenu extends Group {

	final static TextureRegion menuCircle;
	
	final static TextureRegionDrawable setSymbolButtonGfx;
	final static TextureRegionDrawable addSymbolButtonGfx;
	final static TextureRegionDrawable switchSymbolButtonGfx;
	final static TextureRegionDrawable clearButtonGfx;
	final static TextureRegionDrawable closeButtonGfx;
	final static TextureRegionDrawable removeButtonGfx;
	final static TextureRegionDrawable setFinalButtonGfx;
	final static TextureRegionDrawable setInitialButtonGfx;
	
	static{
		menuCircle = new TextureRegion(new Texture(Gdx.files.internal("MenuCircle.png")));
		
		setSymbolButtonGfx = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ButtonMenuSetSymbol.png"))));
		addSymbolButtonGfx = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ButtonMenuAddSymbol.png"))));
		switchSymbolButtonGfx = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ButtonMenuSwitchSymbol.png"))));
		clearButtonGfx = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ButtonMenuClear.png"))));
		closeButtonGfx = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ButtonMenuClose.png"))));
		removeButtonGfx = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ButtonMenuRemove.png"))));
		setFinalButtonGfx = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ButtonMenuSetFinal.png"))));
		setInitialButtonGfx = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("ButtonMenuSetInitial.png"))));
	}
	
	ConstructionMenuInterface currentObject;
	Skin skin;
	HashMap<String, WidgetGroup> possibleButtons;
	ArrayList<WidgetGroup> currentButtons;
	String[] inputSymbols;
	Alphabet[] transitionAlphabets;
	ArrayList<String> output;
	
	Circle circle;
	
	Vector2 calculatingVector;
	boolean allowEmptyInput;
	
	public ConstructionMenu(Alphabet inputSymbols, boolean allowEmptyInput, Alphabet... transitionAlphabets){
		this.setVisible(false);
		
		this.skin = new Skin(Gdx.files.internal("uiskin.json"));
		this.circle = new Circle();
		this.circle.setRadius(64);
		this.inputSymbols = inputSymbols.getAsStrings();
		this.transitionAlphabets = transitionAlphabets;
		this.allowEmptyInput = allowEmptyInput;
		this.output = new ArrayList<String>();
		
		this.setBounds(0, 0, 128, 128);
		this.setTransform(false);
		this.setOrigin(Align.center);
		
		this.setTouchable(Touchable.childrenOnly);
		
		this.possibleButtons = new HashMap<String, WidgetGroup>();
		this.currentButtons = new ArrayList<WidgetGroup>();
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
		possibleButtons.put(ConstructionMenuOption.REMOVE.toString(), removeButton);
		
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
		possibleButtons.put(ConstructionMenuOption.CLOSE.toString(), closeButton);
		
		Button setInitialButton = new Button(setInitialButtonGfx);
		setInitialButton.setVisible(false);
		setInitialButton.addListener(new ClickListener(){
			boolean switchColor;
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				currentObject.setSelectedOption(ConstructionMenuOption.SET_INITIAL, "");
				((ConstructionMenu)(event.getListenerActor().getParent())).setSelectedOptions();
				event.stop();
				return true;
			}
		});
		this.addActor(setInitialButton);
		possibleButtons.put(ConstructionMenuOption.SET_INITIAL.toString(), setInitialButton);
		
		Button setFinalButton = new Button(setFinalButtonGfx);
		setFinalButton.setVisible(false);
		setFinalButton.addListener(new ClickListener(){
			boolean switchColor;
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				currentObject.setSelectedOption(ConstructionMenuOption.SET_FINAL, "");
				((ConstructionMenu)(event.getListenerActor().getParent())).setSelectedOptions();
				event.stop();
				return true;
			}
		});
		this.addActor(setFinalButton);
		possibleButtons.put(ConstructionMenuOption.SET_FINAL.toString(), setFinalButton);
		
		Button clearButton = new Button(clearButtonGfx);
		clearButton.setVisible(false);
		clearButton.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				currentObject.setSelectedOption(ConstructionMenuOption.CLEAR, "");
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
		possibleButtons.put(ConstructionMenuOption.CLEAR.toString(), clearButton);
		
		
		
		TextButton.TextButtonStyle textButtonStyleSetSymbol = new TextButton.TextButtonStyle();
		textButtonStyleSetSymbol.up = setSymbolButtonGfx;
		textButtonStyleSetSymbol.font = skin.getFont("default-font");
		
		TextButton.TextButtonStyle textButtonStyleSwitchSymbol = new TextButton.TextButtonStyle();
		textButtonStyleSwitchSymbol.up = switchSymbolButtonGfx;
		textButtonStyleSwitchSymbol.font = skin.getFont("default-font");
		
		TextButton.TextButtonStyle textButtonStyleAddSymbol = new TextButton.TextButtonStyle();
		textButtonStyleAddSymbol.up = addSymbolButtonGfx;
		textButtonStyleAddSymbol.font = skin.getFont("default-font");

		
		for(int i = 0; i < transitionAlphabets.length; i++){
			if(i == 0){
				for(String aSymbol : transitionAlphabets[i].getAsStrings(true)){
					final ButtonCollection aCollection = new ButtonCollection();
					this.addActor(aCollection);
					aCollection.setVisible(false);
					
					TextButton temp = new TextButton(aSymbol, textButtonStyleSetSymbol);
					//temp.align(Align.center);
					temp.setVisible(false);
					temp.setSize(buttonSize, buttonSize);
					temp.addListener(new ClickListener(){
						boolean switchColor;
						
						@Override
						public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
							currentObject.setSelectedOption(ConstructionMenuOption.SET_TAPE_SYMBOL, event.getListenerActor().getParent().toString());
							((ConstructionMenu)(event.getListenerActor().getParent().getParent())).setSelectedOptions();
							
							event.stop();
							return true;
						}
					});
					
					temp.setName(aSymbol);
					aCollection.addActor(temp);
					possibleButtons.put(ConstructionMenuOption.SET_TAPE_SYMBOL.toString()+":"+aSymbol, aCollection);
				}
			}
			else{
				
				String[] symbols = transitionAlphabets[0].getAsStrings(true);
				for(int j = 0; j < symbols.length; j++)
				{
					final String[] innerSymbols =  transitionAlphabets[i].getAsStrings(true);

					TextButton temp = new TextButton(innerSymbols[0], textButtonStyleSwitchSymbol);
					//temp.align(Align.center);
					temp.setVisible(false);
					temp.setSize(buttonSize, buttonSize);
					temp.addListener(new ClickListener(){
						boolean switchColor;
						String[] symbols = innerSymbols;
						int cursor = 0;
						
						@Override
						public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
							((ConstructionMenu)(event.getListenerActor().getParent().getParent())).setSelectedOptions();
							event.getListenerActor().setColor(Color.RED);
							
							cursor++;
							if(cursor >= symbols.length){
								cursor = 0;
							}
							
							TextButton textButton = (TextButton) event.getListenerActor();
							textButton.setText(innerSymbols[cursor]);
							textButton.setName(innerSymbols[cursor]);
							currentObject.setSelectedOption(ConstructionMenuOption.SET_TAPE_SYMBOL, event.getListenerActor().getParent().toString());
							
							event.stop();
							return true;
						}
						
						@Override
						public void touchUp(InputEvent event, float x, float y, int pointer, int button){
							event.getListenerActor().setColor(Color.WHITE);
						}
					});
				
					temp.setName(innerSymbols[0]);
					possibleButtons.get(ConstructionMenuOption.SET_TAPE_SYMBOL.toString()+":"+symbols[j]).addActor(temp);
				}
			}
		}

		for(String aSymbol : this.inputSymbols){
			TextButton temp2 = new TextButton(aSymbol, textButtonStyleAddSymbol);
			temp2.setVisible(false);
			temp2.setSize(buttonSize, buttonSize);
			temp2.addListener(new ClickListener(){			
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
					currentObject.setSelectedOption(ConstructionMenuOption.ADD_INPUT_SYMBOL, ((TextButton)event.getListenerActor()).getText());
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
			possibleButtons.put(ConstructionMenuOption.ADD_INPUT_SYMBOL.toString()+":"+aSymbol, temp2);
		}
		
		TextButton temp = new TextButton("\u03B5", textButtonStyleSetSymbol);
		temp.setVisible(false);
		temp.addListener(new ClickListener(){
			boolean switchColor;
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				currentObject.setSelectedOption(ConstructionMenuOption.SET_TAPE_SYMBOL, ((TextButton)event.getListenerActor()).getText());
				((ConstructionMenu)(event.getListenerActor().getParent())).setSelectedOptions();
				
				event.stop();
				return true;
			}
		});
		this.addActor(temp);
		possibleButtons.put(ConstructionMenuOption.SET_EMPTY_WORD.toString(), temp);
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
		
		for(WidgetGroup currentButton : currentButtons){
			currentButton.setPosition(circle.radius+calculatingVector.x-currentButton.getHeight()/2, circle.radius+calculatingVector.y-currentButton.getHeight()/2);
			currentButton.setVisible(true);
			calculatingVector.rotate(360/buttonCount);
		}
		
		super.act(delta);
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
			if(option == ConstructionMenuOption.SET_TAPE_SYMBOL){
				for(String symbol : transitionAlphabets[0].getAsStrings()){
					WidgetGroup temp = possibleButtons.get(option.toString()+":"+symbol);
					if(temp != null){
						currentButtons.add(temp);
					}
				}
				
				if(allowEmptyInput){
					currentButtons.add(possibleButtons.get(ConstructionMenuOption.SET_EMPTY_WORD.toString()));
				}
			}
			else if(option == ConstructionMenuOption.ADD_INPUT_SYMBOL){
				for(String symbol : inputSymbols){
					WidgetGroup temp = possibleButtons.get(option.toString()+":"+symbol);
					currentButtons.add(temp);
				}
			}
			else{
				currentButtons.add(possibleButtons.get(option.toString()));
			}
		}
	}
	
	private void clearCurrentButtons(){
		for(WidgetGroup aButton : currentButtons){
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
			if(aButton.equals(ConstructionMenuOption.CLOSE.toString())){
				possibleButtons.get(aButton).setColor(Color.GREEN);
			}
			else{
				possibleButtons.get(aButton).setColor(Color.WHITE);
			}
		}
		
		String constructionMenuOptions = currentObject.getSelectedOptions();
		
		if(constructionMenuOptions != null && constructionMenuOptions.equals("") == false){
			for(String option : constructionMenuOptions.split(",")){
				
				if(option.startsWith(ConstructionMenuOption.SET_TAPE_SYMBOL.toString())){
					String optionPart = option.substring(0, option.indexOf('/'));
					String rule = option.substring(option.indexOf(':')+1);
					
					if(possibleButtons.keySet().contains(optionPart) == false){
						break;
					}

					possibleButtons.get(optionPart).setColor(Color.RED);
					possibleButtons.get(optionPart).setName(rule);
				}
				else{
					
					if(possibleButtons.keySet().contains(option) == false){
						break;
					}
					
					possibleButtons.get(option).setColor(Color.RED);
				}
			}
		}
	}
	
	public class ButtonCollection extends WidgetGroup
	{
		ArrayList<Actor> buttons;
		Actor mainActor;
		
		public ButtonCollection(){
			buttons = new ArrayList<Actor>();	
			this.setHeight(45);
		}
		
		@Override
		public void positionChanged(){
			SnapshotArray<Actor> children = this.getChildren();
			Vector2 temp = new Vector2(this.getX()+this.getHeight()/2-circle.radius, this.getY()+this.getHeight()/2-circle.radius);
			float length = (float) Math.sqrt(45*45);
			temp.setLength(length);
			Gdx.app.log("angle", this.getX()+" - "+this.getY());
			
			for(int i = 0; i < children.size; i++){
				if(i > 0){
					Actor currentButton = children.get(i);

					currentButton.setPosition(temp.x, temp.y);
					temp.setLength(length*i);
				}
			}
		}
		
		@Override
		public void setName(String name){
			String[] parts = name.split("/");
			
			if(parts.length != buttons.size()){
				return;
			}
			
			for(int i = 0; i < buttons.size(); i++){
				Actor actor = buttons.get(i);
				actor.setName(parts[i]);
				if(actor instanceof TextButton){
					((TextButton)actor).setText(parts[i]);
				}
			}
			
			super.setName(name);
		}
		
		@Override
		public void setColor(Color color){
			Actor actor = buttons.get(0);
			if(actor != null){
				actor.setColor(color);
			}
			
			for(int i = 1; i < buttons.size(); i++){
				if(color.toIntBits() == Color.WHITE.toIntBits()){
					buttons.get(i).setVisible(false);
				}
				else{
					buttons.get(i).setVisible(true);
				}
			}
		}
		
		@Override
		public void addActor(Actor actor){
			if(mainActor == null){
				mainActor = actor;
			}
			
			buttons.add(actor);
			super.addActor(actor);
		}
		
		@Override
		public void setVisible(boolean visible){

			for(Actor anActor : this.getChildren()){
				if(visible == true && anActor != mainActor && mainActor.getColor().toIntBits() == Color.WHITE.toIntBits()){
					anActor.setVisible(false);
				}
				else{
					anActor.setVisible(visible);
				}
			}
			
			super.setVisible(visible);
		}
		
		@Override
		public void act(float delta){
		
		}
		
		@Override
		public String toString(){
			String temp = "";
			
			for(int i = 0; i < buttons.size(); i++){
				temp+=buttons.get(i).getName();
				if(i != buttons.size()-1){
					temp+="/";
				}
			}
			
			return temp;
		}
	}
}
