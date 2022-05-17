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
 * Simple actor that displays floating text.
 * @author Slirmelo Stufferty
 */
public class Text extends AHadalActor {
	
	protected String text;
	protected BitmapFont font;
	protected BitmapFontCache cache;
	protected Color fontColor;
	protected GlyphLayout layout;

	protected float scale = 1.0f;
	
	//is this actor being moused over?
	private boolean mouseOver;
	
	//does the text wrap? If so, it is set to targetWidth length.
	private boolean wrap;
	private float targetWidth;

	//padding for window used if this text is a button
	private static final float pad = 15.0f;

	private boolean mouseWindow;

	public Text(String text, int x, int y) {
		super(x, y);
		this.text = text;

		font = HadalGame.FONT_UI;
		fontColor = HadalGame.DEFAULT_TEXT_COLOR;

		updateHitBox();
	}

	public Text(String text) { this(text, 0, 0); }

	@Override
    public void draw(Batch batch, float alpha) {

		//draw an additional window beneath this actor to indicate a button
		 if (mouseOver) {
		 	if (mouseWindow) {
				GameStateManager.getSimplePatch().draw(batch, getX() - pad / 2, getY(), getWidth() + pad, getHeight());
			}
		 }

		 //we use a text cache here so that we can more easily control the text's transparency
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
		layout.setText(font, text, fontColor, targetWidth, Align.left, wrap);
		setWidth(layout.width);
		setHeight(layout.height);

		cache = font.newFontCache();
		cache.clear();
		cache.setText(layout, 0, 0);

		font.getData().setScale(1.0f);
	}

	public Text setWrap(float targetWidth) {
		this.wrap = true;
		this.targetWidth = targetWidth;
		updateHitBox();
		return this;
	}

	public Text setButton(boolean mouseWindow) {
		this.mouseWindow = mouseWindow;
		this.addListener(new InputListener() {

			@Override
			public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				((Text) event.getTarget()).mouseOver = true;
			}

			@Override
			public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
				((Text) event.getTarget()).mouseOver = false;
			}
		});
		return this;
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
	public Color getColor() { return fontColor; }

	@Override
	public void setColor(Color color) {
		this.fontColor = color;
		updateHitBox();
	}

	@Override
	public void setScale(float scale) {
		this.scale = scale;
		updateHitBox();
	}
}
