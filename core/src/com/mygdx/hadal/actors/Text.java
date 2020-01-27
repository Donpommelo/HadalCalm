package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.mygdx.hadal.HadalGame;

/**
 * Simple actor that displays floating text. Not suitable for long messages.
 */
public class Text extends AHadalActor {
	
	protected String text;
	protected BitmapFont font;
	protected Color color;
	protected GlyphLayout layout;

	protected float scale = 1.0f;
	
	public Text(String text, int x, int y) {
		super(x, y);
		this.text = text;
		font = HadalGame.SYSTEM_FONT_UI;
		color = HadalGame.DEFAULT_TEXT_COLOR;
		
		updateHitBox();
	}
	
	public Text(String text, int x, int y, Color color) {
		this(text, x, y);
		this.color = color;
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		
		 font.getData().setScale(scale);
		 font.setColor(color);
         font.draw(batch, text, getX(), getY() + layout.height);
         
         //Return scale and color to default values.
         font.getData().setScale(1.0f);
         font.setColor(HadalGame.DEFAULT_TEXT_COLOR);
    }
	
	public void updateHitBox() {
		font.getData().setScale(scale);
		layout = new GlyphLayout(font, text);
		setWidth(layout.width);
		setHeight(layout.height);
		font.getData().setScale(1.0f);
	}
	
	public String getText() { return text; }

	public void setText(String text) {
		this.text = text;
		updateHitBox();
	}
	
	@Override
	public Color getColor() { return color; }

	@Override
	public void setColor(Color color) { this.color = color; }
	
	public float getScale() { return scale; }

	@Override
	public void setScale(float scale) {
		this.scale = scale;
		updateHitBox();
	}
}
