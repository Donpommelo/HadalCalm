package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Shocked;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox inflicts a shock on users that it makes contact with
 * @author Zachary Tu
 *
 */
public class ContactUnitShock extends HitboxStrategy {
	
	//the damage and duration of the burn
	private float damage;
	private int radius, chain;
	private short filter;
	private boolean shocked = false;
	
	public ContactUnitShock(PlayState state, Hitbox proj, BodyData user, float damage, int radius, int chain, short filter) {
		super(state, proj, user);
		this.damage = damage;
		this.radius = radius;
		this.chain = chain;
		this.filter = filter;
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (!shocked) {
			if (fixB instanceof BodyData) {
				shocked = true;
				((BodyData) fixB).addStatus(new Shocked(state, creator, (BodyData) fixB, damage, radius, chain, filter));
			}
		}
	}
}
