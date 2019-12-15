package com.mygdx.hadal.schmucks.strategies;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;

public class HitboxOnContactUnitStatusStrategy extends HitboxStrategy{
	
	private Status s;
	
	public HitboxOnContactUnitStatusStrategy(PlayState state, Hitbox proj, BodyData user, Status s) {
		super(state, proj, user);
		this.s = s;
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB instanceof BodyData) {
			s.setInflicted(((BodyData)fixB));
			((BodyData)fixB).addStatus(s);
		}
	}
}
