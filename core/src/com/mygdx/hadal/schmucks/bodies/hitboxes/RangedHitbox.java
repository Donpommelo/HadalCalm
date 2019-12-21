package com.mygdx.hadal.schmucks.bodies.hitboxes;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Stats;

/**
 * A Ranged hitbox is really just a totally normal hitbox.
 * This subclass exists as a means to implement various ranged-specific stats that modify projectile properties.
 * @author Zachary Tu
 *
 */
public class RangedHitbox extends Hitbox {

	public RangedHitbox(PlayState state, float x, float y, int width, int height, float lifespan, Vector2 startVelo, short filter, boolean sensor, boolean procEffects, Schmuck creator) {
		super(state, x, y, width, height,
				lifespan * (1 + creator.getBodyData().getStat(Stats.RANGED_PROJ_LIFESPAN)),
				startVelo.scl(1 + creator.getBodyData().getStat(Stats.RANGED_PROJ_SPD)), filter, sensor, procEffects, creator);
		
		this.width *= (1 +  creator.getBodyData().getStat(Stats.RANGED_PROJ_SIZE));
		this.height *= (1 +  creator.getBodyData().getStat(Stats.RANGED_PROJ_SIZE));
		this.gravity += creator.getBodyData().getStat(Stats.RANGED_PROJ_GRAVITY);
		this.durability += creator.getBodyData().getStat(Stats.RANGED_PROJ_DURABILITY);
		this.restitution += creator.getBodyData().getStat(Stats.RANGED_PROJ_RESTITUTION);
	}
}