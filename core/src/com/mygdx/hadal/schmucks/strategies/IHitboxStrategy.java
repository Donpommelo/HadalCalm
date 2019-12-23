package com.mygdx.hadal.schmucks.strategies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.userdata.HadalData;

public interface IHitboxStrategy {

	public void create();
	
	public void controller(float delta);
	
	public void push(Vector2 push);
	
	public void onHit(HadalData fixB);
	
	public void die();
	
	public void render(SpriteBatch batch);
}
