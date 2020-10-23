package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

import java.util.concurrent.ThreadLocalRandom;

/**
 * This strategy randomly changes a hbox's velocity after it is created.
 * @author Damp Darbchamp
 */
public class Spread extends HitboxStrategy {
	
	//this is the range of spread in degrees that the hbox can be set to
	private final int spread;
	
	public Spread(PlayState state, Hitbox proj, BodyData user, int spread) {
		super(state, proj, user);
		this.spread = spread;
	}
	
	@Override
	public void create() {
		float newDegrees = hbox.getStartVelo().angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1));
		hbox.setLinearVelocity(hbox.getLinearVelocity().setAngle(newDegrees));
	}
}
