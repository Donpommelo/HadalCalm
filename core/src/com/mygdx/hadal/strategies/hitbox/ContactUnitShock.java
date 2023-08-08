package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Shocked;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox inflicts a shock on users that it makes contact with.
 * Shock creates chain lightning that jumps to nearby targets.
 * @author Shobonnier Shilbatross
 */
public class ContactUnitShock extends HitboxStrategy {
	
	//the damage of each chain shock
	private final float damage;

	//the distance that the lightning will chain to and the number of total lightning jumps that will occur
	private final int radius, chain;
	
	//the hbox filter of the user. This determines which targets the chain lightning will jump to.
	private final short filter;

	//this is the effect/item/weapon source of the shock
	private final SyncedAttack source;

	//has this strategy activated yet? This makes sure we do not activate the effect multiple times
	private boolean shocked;
	
	public ContactUnitShock(PlayState state, Hitbox proj, BodyData user, float damage, int radius, int chain, short filter,
							SyncedAttack source) {
		super(state, proj, user);
		this.damage = damage;
		this.radius = radius;
		this.chain = chain;
		this.filter = filter;
		this.source = source;
	}
	
	@Override
	public void onHit(HadalData fixB, Body body) {
		if (!shocked && state.isServer()) {
			if (fixB instanceof BodyData bodyData) {
				shocked = true;
				bodyData.addStatus(new Shocked(state, creator, bodyData, damage, radius, chain, filter, source));
			}
		}
	}
}
