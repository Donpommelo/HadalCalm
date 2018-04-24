package com.mygdx.hadal.schmucks.bodies.hitboxes;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;

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
				(int) (width * (1 + creator.getBodyData().getMeleeRange())), 
				(int) (height * (1 + creator.getBodyData().getMeleeArcSize())),
				0, 
				lifespan * backswing * (1 + creator.getBodyData().getMeleeSwingInterval()), 
				0, 0, startAngle, filter, true, procEffects, creator);
		this.center = center.scl(1 + creator.getBodyData().getMeleeRange());
	}
	
	/**
	 * This just makes sure the melee hitbox tracks the position of the user.
	 */
	@Override
	public void controller(float delta) {
				
		Vector2 hbLocation = creator.getBody().getPosition().add(center);
		this.body.setTransform(hbLocation, startVelo.angleRad());
		
		//Melee hboes should not persist after owner's disposal
		if (!creator.isAlive()) {
			queueDeletion();
		}
		
		lifeSpan -= delta;
		if (lifeSpan <= 0) {
			queueDeletion();
		}
	}

}
