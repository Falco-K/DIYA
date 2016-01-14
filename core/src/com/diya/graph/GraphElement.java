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
	
	final static BitmapFont labelFont;
	final static Color labelFontColor;
	final static LabelStyle labelStyle;
	
	final static TextureRegion nodeBackground;
	final static TextureRegion nodeFinalBackground;
	final static TextureRegion nodeCircle;
	final static TextureRegion nodeFinalCircle;


	static{
		Texture texture = new Texture(Gdx.files.internal("ArialDF.png"), true);
		texture.setFilter(TextureFilter.MipMapLinearNearest, TextureFilter.Linear);
		
		labelFont = new BitmapFont(Gdx.files.internal("ArialDF.fnt"), new TextureRegion(texture), false);

		labelFontColor = Color.BLACK;
		
		labelStyle = new Label.LabelStyle(labelFont, labelFontColor);
		
		nodeBackground = new TextureRegion(new Texture(Gdx.files.internal("NodeBackground.png")));
		nodeFinalBackground = new TextureRegion(new Texture(Gdx.files.internal("NodeFinalBackground.png")));
		nodeCircle = new TextureRegion(new Texture(Gdx.files.internal("NodeCircle.png")));
		nodeFinalCircle = new TextureRegion(new Texture(Gdx.files.internal("NodeFinalCircle.png")));
	}
	
	Graph graph;
	boolean checkBoundaries;
	
	public GraphElement(Graph graph){
		this.graph = graph;
		checkBoundaries = true;
	}
	
	@Override
	public void moveBy(float x, float y){
		
		if(checkBoundaries){
			if(this.getX()+x < graph.getX()){
				x = graph.getX()-this.getX();
			}
			if(this.getY()+y < graph.getY()){
				y = graph.getY()-this.getY();
			}
			if(this.getRight()+x > graph.getWidth()){
				x = graph.getWidth()-this.getRight();
			}
			if(this.getTop()+y > graph.getHeight()){
				y = graph.getHeight()-this.getTop();
			}
		}
		
		super.moveBy(x, y);
	}
	
	@Override
	public void setPosition(float x, float y){
		
		if(checkBoundaries){
			if(x < graph.getX()){
				x = graph.getX();
			}
			if(y < graph.getY()){
				y = graph.getY();
			}
			if(x+this.getWidth() > graph.getWidth()){
				x = graph.getWidth()-this.getWidth();
			}
			if(y+this.getHeight() > graph.getHeight()){
				y = graph.getHeight()-this.getHeight();
			}
		}

		super.setPosition(x, y);
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
