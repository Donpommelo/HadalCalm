package com.mygdx.hadal.strategies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.battle.DamageTag;

/**
 * This interface is used by hbox strategies.
 * hbox strategies are attached to hboxes and perform certain actions at specific times of the hbox's lifecycle
 * @author Thunhilde Tirpingcraft
 */
public interface IHitboxStrategy {

	//this is run when the strategy is attached to the hbox. This is often used for instantiating strategy-specific fields.
	void create();
	
	//this is run every engine tick that the hitbox is alive
	void controller(float delta);
	
	//this is run when something applies a push to the hitbox
	void push(Vector2 push);
	
	//this runs when the hitbox hits another entity
	void onHit(HadalData fixB);
	
	//this runs the hbox "receives damage" usually by being hit by another hbox.
	void receiveDamage(BodyData perp, float basedamage, Vector2 knockback, DamageTag... tags);
	
	//this is run when the hitbox dies
	void die();
}
