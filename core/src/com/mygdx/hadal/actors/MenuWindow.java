package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.mygdx.hadal.managers.GameStateManager;

/**
 * Static background actor for various windows and ui elements.
 */
public class MenuWindow extends AHadalActor{
	
	public MenuWindow(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
        GameStateManager.getSimplePatch().draw(batch, getX(), getY(), getWidth(), getHeight());
    }
}
