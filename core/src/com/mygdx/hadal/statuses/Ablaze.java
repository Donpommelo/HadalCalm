package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * Ablaze units receive damage over time.
 * @author Glamhock Glecnicbasket
 */
public class Ablaze extends Status {

	//this is the damage per proc of the unit
	private final float damage;

	//this is the effect/item/weapon source of the burn
	private final DamageSource source;

	private static final float linger = 1.0f;

	private float procCdCount;

	public Ablaze(PlayState state, float i, BodyData p, BodyData v, float damage, DamageSource source) {
		super(state, i, false, p, v);
		this.procCdCount = 0;
		this.damage = damage;
		this.source = source;
	}

	@Override
	public void onInflict() {
		new ParticleEntity(state, inflicted.getSchmuck(), Particle.FIRE, linger, duration + linger,
				true, SyncType.CREATESYNC).setPrematureOff(linger);
	}

	private static final float procCd = 0.5f;
	@Override
	public void timePassing(float delta) {
		super.timePassing(delta);
		if (procCdCount >= procCd) {
			procCdCount -= procCd;
			inflicted.receiveDamage(damage, new Vector2(), inflicter, true, null, source, DamageTag.FIRE);
		}
		procCdCount += delta;
	}
	
	@Override
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
