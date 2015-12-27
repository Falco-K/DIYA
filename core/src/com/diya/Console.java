package com.diya;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class Console extends WidgetGroup{
	
	private TextField consoleInput;
	private Label[] messages;
	private Table consoleOutput;

	public Console(final UIStage stage, Skin skin, int size){
		messages = new Label[size];
		consoleOutput = new Table(skin);

       	consoleInput = new TextField("", skin);
       	//consoleInput.setPosition(5, 5);
       	//consoleInput.setSize(300, 20);

		consoleInput.setTextFieldListener(new TextFieldListener(){
			@Override
			public void keyTyped(TextField textField, char c) {
				if(textField.isVisible() && (c == '\r' || c == '\n')){
					if(textField.getText().matches("") == false){
						addMessage(textField.getText());
						stage.forwardCommand(textField.getText());	
						textField.setText("");
					}
				}
			}
		});
		
		consoleInput.setTextFieldFilter(new TextFieldFilter(){
			@Override
			public boolean acceptChar(TextField textField, char c) {
				if(textField.isVisible()){
					return true;
				}
				else{
					return false;
				}
			}
		});
		
		consoleOutput.setColor(Color.BLACK);
		consoleOutput.setFillParent(true);
		consoleOutput.top();
		consoleOutput.setBackground(new NinePatchDrawable(getNinePatch()));
		this.addActor(consoleOutput);	
		consoleOutput.add(consoleInput).top().expandX().fillX().top();
		
		
		consoleOutput.row();
		
		for(int i = 0; i < messages.length; i++){
			messages[i] = new Label("", skin);
			consoleOutput.add(messages[i]).expandX().fillX();
			consoleOutput.row();
		}
	}
	
	public void addMessage(String message){
		for(int i = messages.length-1; i > 0; i--){
			messages[i].setText(messages[i-1].getText());
		}
		
		messages[0].setText(message);
	}
	
	public TextField getConsoleInput(){
		return consoleInput;
	}
	
	@Override
	public void setVisible(boolean visible){
		this.consoleInput.setVisible(visible);
		super.setVisible(visible);
	}
	
	/*@Override
	public void draw(Batch batch, float parentAlpha){
		this.drawChildren(batch, parentAlpha);
		super.draw(batch, parentAlpha);
	}*/
	
	private NinePatch getNinePatch() {
	    
		// Get the image
		final Texture t = new Texture(Gdx.files.internal("ng.png"));
	    
		NinePatch temp = new NinePatch( new TextureRegion(t, 1, 1 , t.getWidth() - 2, t.getHeight() - 2), 10, 10, 10, 10);
		Color color = new Color();
		color.set(1.0f, 1.0f, 1.0f, 0.3f);
		temp.setColor(color);
		// create a new texture region, otherwise black pixels will show up too, we are simply cropping the image
		// last 4 numbers respresent the length of how much each corner can draw,
		// for example if your image is 50px and you set the numbers 50, your whole image will be drawn in each corner
		// so what number should be good?, well a little less than half would be nice
	    return temp;
	}
}
