package com.mygdx.hadal.schmucks.bodies.hitboxes;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;

/**
 * A Ranged hitbox is really just a totally normal hitbox.
 * This subclass exists as a means to implement various ranged-specific stats that modify projectile properties.
 * @author Zachary Tu
 *
 */
public class RangedHitbox extends Hitbox {

	public RangedHitbox(PlayState state, float x, float y, int width, int height, float grav, float lifespan, int dura,
			float rest, Vector2 startVelo, short filter, boolean sensor, boolean procEffects, Schmuck creator) {
		super(state, x, y, 
				(int) (width * (1 + creator.getBodyData().getProjectileSize())), 
				(int) (height * (1 + creator.getBodyData().getProjectileSize())), 
				grav + creator.getBodyData().getProjectileGravity(), 
				lifespan * (1 + creator.getBodyData().getProjectileLifespan()),
				(int) (dura + creator.getBodyData().getProjectileDurability()), 
				rest + creator.getBodyData().getProjectileBounciness(), 
				startVelo.scl(1 + creator.getBodyData().getProjectileSpeed()), filter, sensor, procEffects, creator);
	}

}
