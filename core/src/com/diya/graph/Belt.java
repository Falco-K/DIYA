package com.diya.graph;

import java.util.ArrayList;
import java.util.EnumSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputEvent.Type;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.Timer.Task;
import com.diya.ConstructionMenuInterface;
import com.diya.ConstructionMenuOption;
import com.diya.ConstructionStage;

import diya.model.automata.components.InputTape;
import diya.model.automata.components.Tape;
import diya.model.automata.events.TapeUpdatedEvent;
import diya.model.language.Symbol;

public class Belt extends GraphElement implements ConstructionMenuInterface{

	final static TextureRegion cellGfx;
	final static TextureRegion highlightedCell;
	final static TextureRegionDrawable stopButtonUp;
	final static TextureRegionDrawable stopButtonDown;
	final static TextureRegionDrawable runButton;
	final static TextureRegionDrawable pauseButton;
	final static TextureRegionDrawable stepButtonUp;
	final static TextureRegionDrawable stepButtonDown;
	
	static{
		cellGfx = new TextureRegion(new Texture(Gdx.files.internal("Cell.png")));
		highlightedCell = new TextureRegion(new Texture(Gdx.files.internal("HighlightedCell.png")));
		
		stopButtonUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("StopButtonUp.png"))));
		stopButtonDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("StopButtonDown.png"))));
		runButton = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("RunButton.png"))));
		pauseButton = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("PauseButton.png"))));
		stepButtonUp = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("StepButtonUp.png"))));
		stepButtonDown = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("StepButtonDown.png"))));
	}
	
	Tape tape;
	ArrayList<Cell> cells;
	
	Button stepButton;
	Button runPauseButton;
	Button stopButton;
	
	final float cellWidth = 32;
	final float cellHeight = 64;
	
	int currentCell;
	boolean accepted;
	boolean finished;
	
	boolean cellAmountChanged;
	
	public Belt(Tape inputTape){
		this.tape = inputTape;
		this.setPosition(inputTape.getX(), inputTape.getY());
		this.cellAmountChanged = false;
		
		cells = new ArrayList<Cell>();
		accepted = false;
		
		this.addListener(new DragListener(){
			
			float offsetX;
			float offsetY;
			boolean dragged = false;
			
			
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if(button == Input.Buttons.LEFT && event.getTarget() instanceof Belt){
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
		
		//Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
		stepButton = new Button(stepButtonUp, stepButtonDown);
		stepButton.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if(button == Input.Buttons.LEFT && finished == false){
					doAutomatonStep();
					return true;
				}
				
				return false;
			}
		});
		
		
		final Timer doSteps = new Timer();
		doSteps.scheduleTask(new Task(){
			@Override
			public void run() {
				doAutomatonStep();
			}
			
		}, 0, 2);
		doSteps.stop();
		
		final Button.ButtonStyle runButtonStyle = new Button.ButtonStyle(runButton, null, null);
		final Button.ButtonStyle pauseButtonStyle = new Button.ButtonStyle(pauseButton, null, null);	
		
		runPauseButton = new Button(runButton);
		runPauseButton.setPosition(0, cellWidth);
		runPauseButton.setUserObject(Boolean.FALSE);
		runPauseButton.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if(button == Input.Buttons.LEFT){
					Button temp = (Button)event.getListenerActor();
					if((Boolean)temp.getUserObject() == Boolean.FALSE){
						temp.setStyle(pauseButtonStyle);
						temp.setUserObject(true);
						doSteps.start();
					}
					else{
						temp.setStyle(runButtonStyle);
						temp.setUserObject(false);
						doSteps.stop();
					}
					return true;
				}
				
				return false;
			}
		});
		
		stopButton = new Button(stopButtonUp, stopButtonDown);
		stopButton.setSize(cellHeight, cellHeight);
		stopButton.addListener(new ClickListener(){
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button){
				if(button == Input.Buttons.LEFT){
					if((Boolean)runPauseButton.getUserObject() == Boolean.TRUE){
						InputEvent inputEvent = new InputEvent();
						inputEvent.setType(Type.touchDown);
						inputEvent.setListenerActor(runPauseButton);
						runPauseButton.fire(inputEvent);
					}
					
					((ConstructionStage)event.getStage()).sendCommand("reset");
					resetCellHighlightning();
					return true;
				}
				
				return false;
			}
		});
		
		clearCells();
	}
	
	public void addCell(String input){
		Cell temp = new Cell(input);
		if(cells.size() > 1){
			cells.add(cells.size()-1, temp);
		}
		else{
			cells.add(temp);
		}
		
		this.addActor(temp);
		
		this.setSize(cells.size()*cellWidth+cellWidth*4, cellHeight);
		this.resetCellHighlightning();
		
		cellAmountChanged = true;
	}
	
	public void resetCellHighlightning(){
		currentCell = 0;
		for(Cell aCell: cells){
			aCell.setHighlight(false);
		}
		accepted = false;
		finished = false;
	}
	
	public void doAutomatonStep(){
		((ConstructionStage)this.getStage()).sendCommand("dostep");
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

	private void clearCells(){
		cells.clear();
		clearChildren();
		
		this.addActor(stepButton);
		this.addActor(runPauseButton);
		setSize(stepButton.getWidth()+stopButton.getWidth(), cellHeight);
		this.addActor(stopButton);
		this.addCell("");
		this.addCell("");
		
		this.cellAmountChanged = true;
	}
	
	public void finished(boolean accepted){
		this.finished = true;
		this.accepted = accepted;
	}
	
	public void updateBelt(){
		this.clearCells();
		
		for(Symbol aSymbol : tape){
			this.addCell(aSymbol.toString());
		}
		
		cells.get(tape.getCurrentHeadPosition()+1).setHighlight(true);
	}
	
	@Override
	public void sizeChanged(){
		stopButton.setPosition(this.getWidth()-stopButton.getWidth(), 0);
	}
	
	@Override
	public void act(float delta){
		if(cellAmountChanged){
			float startX = cellWidth*2;
			for(int i = 0; i < cells.size(); i++){
				cells.get(i).setPosition(startX, 0);
				startX+= cellWidth;
			}
			
			cellAmountChanged = false;
		}
	}
	
	@Override
	public void drawDebug(ShapeRenderer shapeRenderer) {

	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		this.applyTransform(batch, this.computeTransform());
		for(Cell aCell : cells){
			aCell.draw(batch, parentAlpha);
		}
		this.resetTransform(batch);
	}
	
	public void drawLabels(Batch batch, float parentAlpha){
		this.applyTransform(batch, this.computeTransform());
		for(Cell aCell : cells){
			aCell.drawLabel(batch, parentAlpha);
		}
		this.resetTransform(batch);
	}
	
	public void drawButtons(Batch batch, float parentAlpha){
		this.applyTransform(batch, this.computeTransform());
		stepButton.draw(batch, parentAlpha);;
		runPauseButton.draw(batch, parentAlpha);
		stopButton.draw(batch, parentAlpha);
		this.resetTransform(batch);
	}

	@Override
	public EnumSet<ConstructionMenuOption> getMenuOptions() {
		return EnumSet.of(ConstructionMenuOption.CLOSE, ConstructionMenuOption.ADD_INPUT_SYMBOL, ConstructionMenuOption.CLEAR);
	}

	@Override
	public String getSelectedOptions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSelectedOption(ConstructionMenuOption option, CharSequence text) {
		if(option == ConstructionMenuOption.ADD_INPUT_SYMBOL){
			this.moveBy(-cellWidth, 0);
			((ConstructionStage)this.getStage()).sendCommand("setinput "+getInput()+" "+text);
		}
		else if(option == ConstructionMenuOption.CLEAR){
			this.moveBy((this.cells.size()-2)*cellWidth, 0);
			((ConstructionStage)this.getStage()).sendCommand("setinput");
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
		return this.getX()+this.getWidth()-cellWidth*2.5f;
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
			this.setTransform(false);

			cellLabel = new Label(content, labelStyle);
			cellLabel.setFillParent(true);
			cellLabel.setAlignment(Align.center);
			cellLabel.setOrigin(Align.center);
			cellLabel.setPosition(0, 0);

			this.addActor(cellLabel);
			this.setTouchable(Touchable.disabled);
			this.highlight = false;
		}
		
		public void setHighlight(boolean highlight){
			this.highlight = highlight;
		}
		
		public String getContent(){
			return cellLabel.getText().toString();
		}
		
		public void setCellText(String text){
			cellLabel.setText(text);
		}
		
		public void drawLabel(Batch batch, float parentAlpha){
			this.drawChildren(batch, parentAlpha);
		}
		
		@Override
		public void draw(Batch batch, float parentAlpha){
			if(finished){
				if(accepted){
					batch.setColor(Color.GREEN);
				}
				else{
					batch.setColor(Color.RED);
				}
			}else{
				batch.setColor(Color.BLACK);
			}
			
			batch.draw(cellGfx, this.getX(), this.getY());
			
			batch.setColor(Color.WHITE);
			if(highlight == true){
				batch.draw(highlightedCell, this.getX(), this.getY());
			}
		}
	}
}
