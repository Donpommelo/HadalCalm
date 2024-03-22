package com.mygdx.hadal.actors;

import com.badlogic.gdx.graphics.g2d.Batch;

import static com.mygdx.hadal.managers.SkinManager.SIMPLE_PATCH;

/**
 * Static background actor for various windows and ui elements.
 * @author Bogcliff Broldebury
 */
public class MenuWindow extends AHadalActor {
	
	public MenuWindow(int x, int y, int width, int height) {
		super(x, y, width, height);
	}
	
	@Override
    public void draw(Batch batch, float alpha) {
		SIMPLE_PATCH.draw(batch, getX(), getY(), getWidth(), getHeight());
    }
}
