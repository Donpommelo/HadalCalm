package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Slodged;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox inflicts a slow on users that it makes contact with
 * @author Zachary Tu
 *
 */
public class ContactUnitSlow extends HitboxStrategy {
	
	//the damage and duration of the burn
	private float duration, slow;
	
	public ContactUnitSlow(PlayState state, Hitbox proj, BodyData user, float duration, float slow) {
		super(state, proj, user);
		this.duration = duration;
		this.slow = slow;
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB instanceof BodyData) {
			((BodyData) fixB).addStatus(new Slodged(state, duration, slow, creator, (BodyData) fixB));
		}
	}
}
