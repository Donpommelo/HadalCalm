package com.mygdx.hadal.schmucks.bodies.hitboxes;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Stats;

/**
 * A Ranged hitbox is really just a totally normal hitbox.
 * This subclass exists as a means to implement various ranged-specific stats that modify projectile properties.
 * @author Skurgarita Smalppepper
 */
public class RangedHitbox extends Hitbox {

	public RangedHitbox(PlayState state, Vector2 startPos, Vector2 size, float lifespan, Vector2 startVelo, short filter, boolean sensor, boolean procEffects, Schmuck creator, Sprite sprite) {
		super(state, startPos, size,
			lifespan * (1 + creator.getBodyData().getStat(Stats.RANGED_PROJ_LIFESPAN)),
				state.isServer() ? startVelo.scl(1 + creator.getBodyData().getStat(Stats.RANGED_PROJ_SPD)) : startVelo,
				filter, sensor, procEffects, creator, sprite);

		//apply user's projectile modifiers. (clients ignore proj spd because that's already counted for when server sends attack)
		setScale(getScale() + creator.getBodyData().getStat(Stats.RANGED_PROJ_SIZE));
		setGravity(getGravity() + creator.getBodyData().getStat(Stats.RANGED_PROJ_GRAVITY));
		setDurability((int) (getDurability() + creator.getBodyData().getStat(Stats.RANGED_PROJ_DURABILITY)));
		setRestitution(getRestitution() + creator.getBodyData().getStat(Stats.RANGED_PROJ_RESTITUTION));
	}
}