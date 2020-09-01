package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * @author Zachary Tu
 */
public class Pushable extends HitboxStrategy {
	
	public Pushable(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}
	
	@Override
	public void receiveDamage(float basedamage, Vector2 knockback) {
		if (hbox.isAlive()) {
			hbox.push(knockback);
		}
	}
}
