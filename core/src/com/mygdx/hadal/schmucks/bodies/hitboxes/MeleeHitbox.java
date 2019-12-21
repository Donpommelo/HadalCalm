package com.mygdx.hadal.schmucks.bodies.hitboxes;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Stats;

/**
 * A MeleeHitbox is a hitbox used by melee weapons.
 * The only distinction between this and a regular hitbox is that this is affixed to the user's body
 * @author Zachary Tu
 *
 */
public class MeleeHitbox extends Hitbox {

	//This is the point on the hitbox body that will be attached to the user.
	public Vector2 center;
	
	/**
	 * Same as normal hitbox man
	 */
	public MeleeHitbox(PlayState state, float x, float y, int width, int height, float lifespan, float backswing,
			Vector2 startAngle, Vector2 center, boolean procEffects, short filter, Schmuck creator) {
		super(state, x, y, 
				(int) (width * (1 + creator.getBodyData().getStat(Stats.MELEE_RANGE))), 
				(int) (height * (1 + creator.getBodyData().getStat(Stats.MELEE_ARC_SIZE))),
				lifespan * backswing * (1 + creator.getBodyData().getStat(Stats.MELEE_ATK_INT)), 
				startAngle, filter, true, procEffects, creator);
		this.center = center.scl(1 + creator.getBodyData().getStat(Stats.MELEE_RANGE));
	}
	
	/**
	 * This just makes sure the melee hitbox tracks the position of the user.
	 */
	@Override
	public void controller(float delta) {
				
		super.controller(delta);
		
		
		
		//Melee hboxes should not persist after owner's disposal. Otherwise, track location
		if (!creator.isAlive()) {
			queueDeletion();
		} else {
			Vector2 hbLocation = creator.getPosition().add(center);
			setTransform(hbLocation, startVelo.angleRad());
		}
		
		lifeSpan -= delta;
		if (lifeSpan <= 0) {
			queueDeletion();
		}
	}
}
