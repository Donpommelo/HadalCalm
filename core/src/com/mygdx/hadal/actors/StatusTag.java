package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.statuses.Status;

/**
 * A Status tag is a ui element that corresponds to a single visible status in the player's status bar.
 * @author Zachary Tu
 *
 */
public class StatusTag extends AHadalActor {

	private Status status;
	
	private BitmapFont font;
	
	private float scale = 0.25f;
	private Color color;
	
	private TextureRegion base, ready, overlay;
	
	private boolean mouseOver;
	
	public StatusTag(AssetManager assetManager, Status status) {
		super(assetManager);
		this.status = status;
		
		font = HadalGame.SYSTEM_FONT_UI;
		color = Color.WHITE;
		
		this.base = GameStateManager.uiAtlas.findRegion("UI_momentum_base");
		this.ready = GameStateManager.uiAtlas.findRegion("UI_momentum_ready");
		this.overlay = GameStateManager.uiAtlas.findRegion("UI_momentum_overlay");
		
		mouseOver = false;
		
		addListener(new ClickListener() {
			@Override
			public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
				super.enter(event, x, y, pointer, fromActor);
				mouseOver = true;
			}

			@Override
			public void exit (InputEvent event, float x, float y, int pointer, Actor toActor) {
				super.enter(event, x, y, pointer, toActor);
				mouseOver = false;
			}
		});
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		batch.draw(base, getX(), getY(), getWidth(), getHeight());
		batch.draw(ready, getX(), getY(), getWidth(), getHeight());
		batch.draw(overlay, getX(), getY(), getWidth(), getHeight());
         
         if (mouseOver) {
        	 font.setColor(color);
        	 font.getData().setScale(scale);
        	 font.draw(batch, status.getName() + ": " + status.getDescr(), getX(), getY() - 25);
         }
    }

	public Status getStatus() {
		return status;
	}
}
