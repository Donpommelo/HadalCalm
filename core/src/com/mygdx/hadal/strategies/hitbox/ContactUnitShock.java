package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Shocked;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox inflicts a shock on users that it makes contact with.
 * Shock creates chain lightning that jumps to nearby targets.
 * @author Zachary Tu
 */
public class ContactUnitShock extends HitboxStrategy {
	
	//the damage of each chain shock
	private float damage;
	
	//the distance that the lightning will chain to and the number of total lightning jumps that will occur
	private int radius, chain;
	
	//the hbox filter of the user. This determines which targets the chain lightning will jump to.
	private short filter;
	
	//has this strategy activated yet? This makes sure we do not activate the effect multiple times
	private boolean shocked;
	
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
