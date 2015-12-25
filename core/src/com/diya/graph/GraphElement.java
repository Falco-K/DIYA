package com.diya.graph;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

public abstract class GraphElement extends Group{
	
	static BitmapFont labelFont = null;
	static ShaderProgram fontShader = null;
	static Color labelFontColor = null;
	static LabelStyle labelStyle = null;

	public GraphElement(){
		this.setStaticFields();
	}
	
	private void setStaticFields(){
		if(labelFont == null){
			Texture texture = new Texture(Gdx.files.internal("ArialDF.png"), true);
			texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
			
			labelFont = new BitmapFont(Gdx.files.internal("ArialDF.fnt"), new TextureRegion(texture), false);
			
			fontShader = new ShaderProgram(Gdx.files.internal("font.vert"), Gdx.files.internal("font.frag"));
			if (!fontShader.isCompiled()) {
			    Gdx.app.error("fontShader", "compilation failed:\n" + fontShader.getLog());
			}
		}
		if(labelFontColor == null){
			labelFontColor = Color.BLACK;
		}
		if(labelStyle == null){
			labelStyle = new Label.LabelStyle(labelFont, labelFontColor);
		}
	}
	/*
	public static void generateFont(){
	   	FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("OpenSans-Regular.ttf"));
	   	FreeTypeFontParameter parameter = new FreeTypeFontParameter();
	   	parameter.size = 12;
	   	parameter.color = Color.BLACK;
	   	labelFont = generator.generateFont(parameter);
	   	generator.dispose();
	}*/
	
	public abstract void drawDebug(ShapeRenderer shapeRenderer);
	public abstract void draw(Batch batch, float parentAlpha);
}
