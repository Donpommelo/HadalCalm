package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Status;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox inflict a status on users that it makes contact with
 * @author Zachary Tu
 *
 */
public class ContactUnitStatus extends HitboxStrategy {
	
	//the status that is to be inflicted
	private Status s;
	
	public ContactUnitStatus(PlayState state, Hitbox proj, BodyData user, Status s) {
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
