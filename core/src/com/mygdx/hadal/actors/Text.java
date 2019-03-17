package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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
	
	private boolean mousedOver;

	public Text(AssetManager assetManager, String text, int x, int y) {
		super(assetManager, x, y);
		this.text = text;
		font = HadalGame.SYSTEM_FONT_UI;
		color = HadalGame.DEFAULT_TEXT_COLOR;
		font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		mousedOver = false;
		updateHitBox();
	}
	
	public Text(AssetManager assetManager, String text, int x, int y, Color color) {
		this(assetManager, text, x, y);
		this.color = color;
	}

	@Override
    public void draw(Batch batch, float alpha) {
		
		if (mousedOver) {
			//TODO
		}
		
		 font.getData().setScale(scale);
		 font.setColor(color);
         font.draw(batch, text, getX(), getY() + layout.height);
         
         //Return scale and color to default values.
         font.getData().setScale(1.0f);
         font.setColor(HadalGame.DEFAULT_TEXT_COLOR);
    }
	
	@Override
	public void updateHitBox() {
		font.getData().setScale(scale);
		layout = new GlyphLayout(font, text);
		setWidth(layout.width);
		setHeight(layout.height);
		super.updateHitBox();
		font.getData().setScale(1.0f);
	}
	
	public void addMouseOverStuff() {
		this.addListener(new ClickListener() {
			
			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				super.enter(event, x, y, pointer, fromActor);
				mousedOver = true;
			}
			
			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				super.exit(event, x, y, pointer, toActor);
				mousedOver = false;
			}
		});
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		updateHitBox();
	}
	
	@Override
	public Color getColor() {
		return color;
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
	}
	
	public float getScale() {
		return scale;
	}

	@Override
	public void setScale(float scale) {
		this.scale = scale;
		updateHitBox();
	}
}
