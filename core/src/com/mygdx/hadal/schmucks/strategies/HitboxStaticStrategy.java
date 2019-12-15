package com.mygdx.hadal.schmucks.strategies;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class HitboxStaticStrategy extends HitboxStrategy{
	
	public HitboxStaticStrategy(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}
	
	@Override
	public void controller(float delta) {
		hbox.setLinearVelocity(0, 0);
	}
}
