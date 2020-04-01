package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy removes another specified strategy from the hitbox.
 * We do this on the earliest update tick.
 * @author Zachary Tu
 *
 */
public class RemoveStrategy extends HitboxStrategy {
	
	//this is the strategy that we want to remove
	private Class<? extends HitboxStrategy> toRemove;

	//have we removed the strategy yet?
	private boolean removed;
	
	public RemoveStrategy(PlayState state, Hitbox proj, BodyData user, Class<? extends HitboxStrategy> toRemove) {
		super(state, proj, user);
		this.toRemove = toRemove;
		removed = false;
	}
	
	@Override
	public void controller(float delta) {
		if (!removed) {
			removed = true;
			hbox.removeStrategy(toRemove);
			hbox.removeStrategy(this);
		}
	}
}
