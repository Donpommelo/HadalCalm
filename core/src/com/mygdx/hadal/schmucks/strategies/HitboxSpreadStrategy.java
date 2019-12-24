package com.mygdx.hadal.schmucks.strategies;

import java.util.concurrent.ThreadLocalRandom;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * This strategy randomly changes a hboxsvelocity after it is created.
 * @author Zachary Tu
 *
 */
public class HitboxSpreadStrategy extends HitboxStrategy{
	
	private int spread;
	
	public HitboxSpreadStrategy(PlayState state, Hitbox proj, BodyData user, int spread) {
		super(state, proj, user);
		this.spread = spread;
	}
	
	@Override
	public void create() {
		float newDegrees = (float) (hbox.getStartVelo().angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
		hbox.getBody().setLinearVelocity(hbox.getLinearVelocity().setAngle(newDegrees));
	}
}
