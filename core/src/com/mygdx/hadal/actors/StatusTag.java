package com.mygdx.hadal.actors;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.statuses.Status;

public class StatusTag extends AHadalActor {

	private Status status;
	
	private BitmapFont font;
	
	private float scale = 0.4f;
	private Color color;
	
	private TextureAtlas atlas;
	
	private TextureRegion base, ready, overlay;
	
	private boolean mouseOver;
	
	public StatusTag(AssetManager assetManager, Status status) {
		super(assetManager);
		this.status = status;
		
		font = HadalGame.SYSTEM_FONT_UI;
		color = Color.WHITE;
		
		this.atlas = (TextureAtlas) HadalGame.assetManager.get(AssetList.UIATLAS.toString());
		this.base = atlas.findRegion("UI_momentum_base");
		this.ready = atlas.findRegion("UI_momentum_ready");
		this.overlay = atlas.findRegion("UI_momentum_overlay");
		
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
        	 font.draw(batch, status.getName(), getX(), getY() - 25);
         }
    }

	public Status getStatus() {
		return status;
	}
}
