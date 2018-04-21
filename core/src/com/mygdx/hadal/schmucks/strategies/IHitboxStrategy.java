package com.mygdx.hadal.schmucks.strategies;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.hadal.schmucks.userdata.HadalData;

public interface IHitboxStrategy {

	public void create();
	
	public void controller(float delta);
	
	public void push(float impulseX, float impulseY);
	
	public void onHit(HadalData fixB);
	
	public void die();
	
	public void render(SpriteBatch batch);
}
