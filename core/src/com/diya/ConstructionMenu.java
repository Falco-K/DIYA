package com.diya;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.diya.graph.*;

public class ConstructionMenu extends Group {
	
	CircleLayout circle;
	ConstructionMenuInterface currentObject;
	Skin skin;
	HashMap<String, Button> possibleButtons;
	ArrayList<Button> currentButtons;
	String[] symbols;
	ArrayList<String> output;
	
	Vector2 calculatingVector;
	
	public ConstructionMenu(String[] symbols){
		this.setVisible(false);
		
		this.skin = new Skin(Gdx.files.internal("uiskin.json"));
		this.circle = new CircleLayout();
		this.addActor(circle);
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
		
		TextButton closeButton = new TextButton("X", skin);
		closeButton.setVisible(false);
		closeButton.setSize(buttonSize, buttonSize);
		closeButton.setColor(Color.GREEN);
		closeButton.addListener(new ClickListener(){
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
		
		this.addActor(closeButton);
		possibleButtons.put(ConstructionMenuOption.Close.toString(), closeButton);	
		
		TextButton setStartButton = new TextButton(">", skin);
		setStartButton.setVisible(false);
		setStartButton.setSize(buttonSize, buttonSize);
		setStartButton.addListener(new ClickListener(){
			boolean switchColor;
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				Color current = ((TextButton)event.getListenerActor()).getColor();
				switchColor = !current.equals(Color.RED);
				
				if(switchColor == true){
					((TextButton)event.getListenerActor()).setColor(Color.RED);
				}
				else{
					((TextButton)event.getListenerActor()).setColor(Color.WHITE);			
				}
				
				currentObject.setSelectedOption(ConstructionMenuOption.SetStart, ((TextButton)event.getListenerActor()).getText());

				event.stop();
				return true;
			}
		});
		this.addActor(setStartButton);
		possibleButtons.put(ConstructionMenuOption.SetStart.toString(), setStartButton);
		
		TextButton setFinalButton = new TextButton("O", skin);
		setFinalButton.setVisible(false);
		setFinalButton.setSize(buttonSize, buttonSize);
		setFinalButton.addListener(new ClickListener(){
			boolean switchColor;
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				Color current = ((TextButton)event.getListenerActor()).getColor();
				switchColor = !current.equals(Color.RED);
				
				if(switchColor == true){
					((TextButton)event.getListenerActor()).setColor(Color.RED);
				}
				else{
					((TextButton)event.getListenerActor()).setColor(Color.WHITE);			
				}
				
				currentObject.setSelectedOption(ConstructionMenuOption.SetFinal, ((TextButton)event.getListenerActor()).getText());

				event.stop();
				return true;
			}
		});
		this.addActor(setFinalButton);
		possibleButtons.put(ConstructionMenuOption.SetFinal.toString(), setFinalButton);
		
		TextButton clearButton = new TextButton("<", skin);
		clearButton.setVisible(false);
		clearButton.setSize(buttonSize, buttonSize);
		clearButton.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				currentObject.setSelectedOption(ConstructionMenuOption.Clear, "");

				event.stop();
				return true;
			}
		});
		this.addActor(clearButton);
		possibleButtons.put(ConstructionMenuOption.Clear.toString(), clearButton);
		
		
		for(String aSymbol : symbols){
			TextButton temp = new TextButton(aSymbol, skin);
			temp.setVisible(false);
			temp.setSize(buttonSize, buttonSize);
			temp.addListener(new ClickListener(){
				boolean switchColor;
				
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
					Color current = ((TextButton)event.getListenerActor()).getColor();
					switchColor = !current.equals(Color.RED);
					
					if(switchColor == true){
						((TextButton)event.getListenerActor()).setColor(Color.RED);
					}
					else{
						((TextButton)event.getListenerActor()).setColor(Color.WHITE);			
					}
					
					currentObject.setSelectedOption(ConstructionMenuOption.SetSymbol, ((TextButton)event.getListenerActor()).getText());

					event.stop();
					return true;
				}
			});
			this.addActor(temp);
			possibleButtons.put(ConstructionMenuOption.SetSymbol.toString()+aSymbol, temp);
			
			TextButton temp2 = new TextButton(aSymbol, skin);
			temp2.setVisible(false);
			temp2.setSize(buttonSize, buttonSize);
			temp2.addListener(new ClickListener(){			
				@Override
				public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
					currentObject.setSelectedOption(ConstructionMenuOption.AddSymbol, ((TextButton)event.getListenerActor()).getText());
					
					event.stop();
					return true;
				}
			});
			this.addActor(temp2);
			possibleButtons.put(ConstructionMenuOption.AddSymbol.toString()+aSymbol, temp2);
		}
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
		calculatingVector.set(0, -circle.getRadius());
		calculatingVector.rotate(-45);
		this.setPosition(currentObject.preferredMenuPositionX()-circle.getRadius(), currentObject.preferredMenuPositionY()-circle.getRadius());
		
		for(Button currentButton : currentButtons){
			currentButton.setPosition(circle.getRadius()+calculatingVector.x-currentButton.getWidth()/2, circle.getRadius()+calculatingVector.y-currentButton.getHeight()/2);
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
		this.drawChildren(batch, parentAlpha);
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
		
		String options = currentObject.getSelectedOptions();
		if(options != null && options.equals("") == false){
			for(String option : options.split(",")){
				if(possibleButtons.keySet().contains(option) == false){
					break;
				}
				possibleButtons.get(option).setColor(Color.RED);
			}
		}
	}
	
	class CircleLayout extends Widget{
		Circle circle;
		
		public CircleLayout(){
			circle = new Circle();
			this.setTouchable(Touchable.disabled);
		}
		
		public void setRadius(float radius){
			circle.radius = radius;
		}
		
		public float getRadius(){
			return circle.radius;
		}
		
		@Override
		public void drawDebug(ShapeRenderer shapeRenderer){
			shapeRenderer.setColor(Color.RED);
			shapeRenderer.circle(this.getX()+circle.radius, this.getY()+circle.radius, circle.radius);
		}
	}
}
