package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy randomly changes a hbox's velocity after it is created.
 * @author Damp Darbchamp
 */
public class Spread extends HitboxStrategy {
	
	public Spread(PlayState state, Hitbox proj, BodyData user, int spread) {
		super(state, proj, user);
		if (hbox.getState().isServer()) {
			float newDegrees = hbox.getStartVelo().angleDeg() + (MathUtils.random(-spread, spread + 1));
			hbox.setStartVelo(hbox.getStartVelo().setAngleDeg(newDegrees));
		}
	}
}
