package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
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

	//this is the effect/item/weapon source of the burn
	private final DamageSource source;

	public ContactUnitBurn(PlayState state, Hitbox proj, BodyData user, float duration, float damage, DamageSource source) {
		super(state, proj, user);
		this.duration = duration;
		this.damage = damage;
		this.source = source;
	}
	
	@Override
	public void onHit(HadalData fixB, Body body) {
		if (fixB instanceof BodyData bodyData) {
			bodyData.addStatus(new Ablaze(state, duration, creator, bodyData, damage, source));
		}
	}
}
