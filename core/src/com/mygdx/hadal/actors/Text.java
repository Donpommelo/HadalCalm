package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.BitmapFontCache;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.utils.Align;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * Simple actor that displays floating text. Not suitable for long messages.
 * @author Slirmelo Stufferty
 */
public class Text extends AHadalActor {
	
	protected String text;
	protected BitmapFont font;
	protected BitmapFontCache cache;
	protected Color color;
	protected GlyphLayout layout;

	protected float scale = 1.0f;
	
	//is this actor being moused over?
	private boolean hover;
	
	//does the text wrap? If so, it is set to targetWidth length.
	private final boolean wrap;
	private final float targetWidth;

	//padding used if this text is a button
	private static final float padding = 15.0f;
	
	public Text(String text, int x, int y, boolean button) {
		this(text, x, y, button, false, 0);
	}
	
	public Text(String text, int x, int y, boolean button, boolean wrap, float targetWidth) {
		super(x, y);
		this.text = text;
		this.wrap = wrap;
		this.targetWidth = targetWidth;

		font = HadalGame.SYSTEM_FONT_UI;
		color = HadalGame.DEFAULT_TEXT_COLOR;
		
		//if the actor is a button, we check if it is moused over to display some visual indication of its size
		if (button) {
			this.addListener(new InputListener() {

				@Override
				public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
					((Text) event.getTarget()).hover = true;
				}

				@Override
				public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					((Text) event.getTarget()).hover = false;
				}
			});
		}
		
		updateHitBox();
	}
	
	@Override
    public void draw(Batch batch, float alpha) {

		//draw an additional window beneath this actor to indicate a button
		 if (hover) {
			 GameStateManager.getSimplePatch().draw(batch, getX() - padding / 2, getY(),
				 getWidth() + padding, getHeight());
		 }
		 cache.setPosition(getX(), getY() + getHeight() / 2 + layout.height / 2);
		 cache.setAlphas(alpha);
		 cache.draw(batch);
    }

	/**
	 * This is run when the actor is created or the size/text is changed.
	 * This sets the hitbox of the actor according to the text size
	 */
	public void updateHitBox() {
		font.getData().setScale(scale);
		layout = new GlyphLayout();
		layout.setText(font, text, color, targetWidth, Align.left, wrap);
		setWidth(layout.width);
		setHeight(layout.height);

		cache = font.newFontCache();
		cache.clear();
		cache.setText(layout, 0, 0);

		font.getData().setScale(1.0f);
	}
	
	public void setText(String text) {
		this.text = text;
		updateHitBox();
	}

	public void setFont(BitmapFont font) {
		this.font = font;
		updateHitBox();
	}

	@Override
	public Color getColor() { return color; }

	@Override
	public void setColor(Color color) { this.color = color; }

	@Override
	public void setScale(float scale) {
		this.scale = scale;
		updateHitBox();
	}
}
