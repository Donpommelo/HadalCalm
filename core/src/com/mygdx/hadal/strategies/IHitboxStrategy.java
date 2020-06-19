package com.mygdx.hadal.strategies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.userdata.HadalData;

/**
 * This interface is used by hbox strategies.
 * hbox strategies are attached to hboxes and perform certain actions at specific times of the hbox's lifecycle
 * @author Zachary Tu
 *
 */
public interface IHitboxStrategy {

	//this is run when the strategy is attached to the hbox. This is often used for instantiating strategy-specific fields.
	public void create();
	
	//this is run every engine tick that the hitbox is alive
	public void controller(float delta);
	
	//this is run when something applies a push to the hitbox
	public void push(Vector2 push);
	
	//this runs when the hitbox hits another entity
	public void onHit(HadalData fixB);
	
	//this is run when the hitbox dies
	public void die();
}
