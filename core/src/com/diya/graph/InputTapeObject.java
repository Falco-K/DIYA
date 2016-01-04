package com.diya.graph;

import java.util.ArrayList;
import java.util.EnumSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.diya.ConstructionMenuInterface;
import com.diya.ConstructionMenuOption;
import com.diya.ConstructionStage;

import diya.model.automata.components.InputTape;

public class InputTapeObject extends GraphElement implements ConstructionMenuInterface{

	InputTape inputTape;
	ArrayList<Cell> cells;
	TextButton stepButton;
	TextButton runButton;
	TextButton resetButton;
	
	final float cellWidth = 32;
	final float cellHeight = 64;
	
	int currentCell;
	boolean accepted;
	boolean finished;
	
	public InputTapeObject(InputTape inputTape){
		this.inputTape = inputTape;
		this.setPosition(inputTape.getX(), inputTape.getY());

		this.setTransform(false);
		cells = new ArrayList<Cell>();
		accepted = false;
		
		this.addListener(new DragListener(){
			
			float offsetX;
			float offsetY;
			boolean dragged = false;
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if(button == Input.Buttons.LEFT && event.getTarget() instanceof InputTapeObject){
					offsetX = x;
					offsetY = y;
					return true;
				}
				
				return false;
			}
			
			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer){
				moveBy(x-offsetX, y-offsetY);
				dragged = true;
			}
			
			@Override
		    public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				if(dragged){
					dragged = false;
				}
			}
		});
		
		Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
		stepButton = new TextButton("Step", skin);
		stepButton.setSize(cellHeight, cellWidth);
		stepButton.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if(button == Input.Buttons.LEFT && finished == false){
					readNextCell();
					return true;
				}
				
				return false;
			}
		});
		
		
		final Timer doSteps = new Timer();
		doSteps.scheduleTask(new Task(){
			@Override
			public void run() {
				readNextCell();
			}
			
		}, 0, 2);
		doSteps.stop();
		
		runButton = new TextButton("Run", skin);
		runButton.setSize(cellHeight, cellWidth);
		runButton.setPosition(0, cellWidth);
		runButton.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if(button == Input.Buttons.LEFT){
					TextButton temp = ((TextButton)event.getListenerActor());
					if(temp.getText().toString().equals("Run")){
						temp.setText("Pause");
						doSteps.start();
					}
					else{
						temp.setText("Run");
						doSteps.stop();
					}
					return true;
				}
				
				return false;
			}
		});
		
		resetButton = new TextButton("Reset", skin);
		resetButton.setSize(cellHeight, cellHeight);
		resetButton.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if(button == Input.Buttons.LEFT){
					if(runButton.getText().toString().equals("Pause")){
						InputEvent inputEvent = new InputEvent();
						inputEvent.setType(Type.touchDown);
						inputEvent.setListenerActor(runButton);
						runButton.fire(inputEvent);
					}
					
					((ConstructionStage)event.getStage()).sendCommand("reset");
					resetCellHighlightning();
					return true;
				}
				
				return false;
			}
		});
		
		clear();
	}
	
	public void addCell(String input){
		Cell temp = new Cell(input);
		cells.add(temp);
		this.addActor(temp);
		temp.setPosition(this.getWidth()-resetButton.getWidth()-temp.getWidth(), 0);
		this.setSize(this.getWidth()+temp.getWidth(), cellHeight);
		this.resetCellHighlightning();
		
		String currentContent = getInput();
		if(input.length() > 0){
			((ConstructionStage)this.getStage()).sendCommand("setinput "+currentContent);
		}
	}
	
	public void resetCellHighlightning(){
		currentCell = 0;
		for(Cell aCell: cells){
			aCell.setHighlight(false);
		}
		accepted = false;
		finished = false;
	}
	
	public void readNextCell(){
		((ConstructionStage)this.getStage()).sendCommand("dostep");
		
		if(currentCell >= cells.size()){
			return;
		}
		
		cells.get(currentCell).setHighlight(true);
		currentCell++;
	}
	
	public String getInput(){
		String input = "";
		for(Cell aCell : cells){
			if(input.equals("") == false){
				input+=" ";
			}
			input+=aCell.getContent();
		}
		
		return input;
	}
	
	public void clear(){
		cells.clear();
		clearChildren();
		
		this.addActor(stepButton);
		this.addActor(runButton);
		setSize(stepButton.getWidth()+cellWidth*1+resetButton.getWidth(), cellHeight);
		this.addActor(resetButton);
		this.addCell("");
	}
	
	public void finished(boolean accepted){
		this.finished = true;
		this.accepted = accepted;
	}
	
	@Override
	public void sizeChanged(){
		resetButton.setPosition(this.getWidth()-resetButton.getWidth(), 0);
	}
	
	@Override
	public void drawDebug(ShapeRenderer shapeRenderer) {
		this.drawDebugBounds(shapeRenderer);
		for(int i = 0; i < cells.size(); i++){
			cells.get(i).drawDebug(shapeRenderer);
		}
		
		if(finished)
		{
			shapeRenderer.set(ShapeType.Filled);
	
			if(accepted){
				shapeRenderer.setColor(Color.GREEN);
				shapeRenderer.rect(this.getX()+this.getWidth()-resetButton.getWidth()-cellWidth, this.getY(), cellWidth, cellHeight);
			}
			else{
				shapeRenderer.setColor(Color.RED);
				shapeRenderer.rect(this.getX()+this.getWidth()-resetButton.getWidth()-cellWidth, this.getY(), cellWidth, cellHeight);
			}
			shapeRenderer.set(ShapeType.Line);
		}
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		this.drawChildren(batch, parentAlpha);
	}

	@Override
	public EnumSet<ConstructionMenuOption> getMenuOptions() {
		return EnumSet.of(ConstructionMenuOption.Close, ConstructionMenuOption.AddSymbol, ConstructionMenuOption.Clear);
	}

	@Override
	public String getSelectedOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSelectedOption(ConstructionMenuOption option, CharSequence text) {
		if(option == ConstructionMenuOption.AddSymbol){
			this.addCell(text.toString());
		}
		else if(option == ConstructionMenuOption.Clear){
			clear();
		}
	}

	@Override
	public void setActive() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInactive() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float preferredMenuPositionX() {
		return this.getX()+stepButton.getWidth()+cellWidth/2;
	}

	@Override
	public float preferredMenuPositionY() {
		return this.getY()+this.getHeight()/2;
	}
	
	class Cell extends Group{
		Label cellLabel;
		boolean highlight;
		
		public Cell(String content){
			this.setSize(32, 64);
			cellLabel = new Label(content, labelStyle);
			cellLabel.setFillParent(true);
			cellLabel.setAlignment(Align.center);
			this.addActor(cellLabel);
			this.setTouchable(Touchable.disabled);
			this.setTransform(false);
			this.highlight = false;
		}
		
		public void setHighlight(boolean highlight){
			this.highlight = highlight;
		}
		
		public String getContent(){
			return cellLabel.getText().toString();
		}
		
		@Override
		public void drawDebug(ShapeRenderer shapeRenderer){
			//this.drawDebugBounds(shapeRenderer);
			if(highlight == true){
				shapeRenderer.set(ShapeType.Filled);
				Color color = shapeRenderer.getColor();
				shapeRenderer.setColor(0.9f, 0.9f, 0.9f, 0.3f);
				shapeRenderer.box(this.getX()+this.getParent().getX(), this.getY()+this.getParent().getY(), 0, this.getWidth(), this.getHeight(), 0);
				shapeRenderer.setColor(color);
				shapeRenderer.set(ShapeType.Line);
			}
		}
		
		@Override
		public void draw(Batch batch, float parentAlpha){
			batch.setShader(fontShader);
			this.drawChildren(batch, parentAlpha);
			batch.setShader(null);
		}
	}
}
