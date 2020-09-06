package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.hadal.HadalGame;

/**
 * Static background actor for title screen.
 */
public class Backdrop extends AHadalActor {
	
	private Texture backgroundTexture;
	private float width = HadalGame.CONFIG_WIDTH;
	private float height = HadalGame.CONFIG_HEIGHT;
	
	public Backdrop(String backdropName, float width, float height) {
		this(backdropName);
		this.width = width;
		this.height = height;
	}
	
	public Backdrop(String backdropName) {
		this.backgroundTexture = HadalGame.assetManager.get(backdropName);
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
        batch.draw(backgroundTexture, getX(), getY(), width, height);
    }
}
