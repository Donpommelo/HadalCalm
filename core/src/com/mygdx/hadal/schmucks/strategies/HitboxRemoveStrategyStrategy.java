package com.mygdx.hadal.schmucks.strategies;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class HitboxRemoveStrategyStrategy extends HitboxStrategy{
	
	private Class<? extends HitboxStrategy> toRemove;
	private boolean removed;
	
	public HitboxRemoveStrategyStrategy(PlayState state, Hitbox proj, BodyData user, 
			Class<? extends HitboxStrategy> toRemove) {
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
