package com.mygdx.hadal.statuses;

import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;


public class Blinded extends Status {

	public Blinded(PlayState state, float i, BodyData p, BodyData v) {
		super(state, i, false, p, v);

		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().setBlinded(i);
		}
	}
	
	@Override
	public void timePassing(float delta) {
		super.timePassing(delta);
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().setBlinded(duration);
		}
	}

	@Override
	public void onRemove() {
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().setBlinded(0);
		}
	}
	
	@Override
	public void onDeath(BodyData perp) {
		if (inflicted instanceof PlayerBodyData playerData) {
			playerData.getPlayer().setBlinded(0);
		}
	}
	
	@Override
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
