package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Ablaze;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox inflicts a burn on users that it makes contact with
 * @author Jargarine Jemherst
 */
public class ContactUnitBurn extends HitboxStrategy {
	
	//the damage per second and duration of the burn
	private final float duration, damage;
	
	public ContactUnitBurn(PlayState state, Hitbox proj, BodyData user, float duration, float damage) {
		super(state, proj, user);
		this.duration = duration;
		this.damage = damage;
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB instanceof BodyData bodyData && state.isServer()) {
			bodyData.addStatus(new Ablaze(state, duration, creator, bodyData, damage));
		}
	}
}
