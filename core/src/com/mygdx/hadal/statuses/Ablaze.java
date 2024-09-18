package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayStateClient;
import com.mygdx.hadal.states.PlayState;

/**
 * Ablaze units receive damage over time.
 * @author Glamhock Glecnicbasket
 */
public class Ablaze extends Status {

	private static final float LINGER = 1.0f;

	//this is the damage per proc of the unit
	private final float damage;

	//this is the effect/item/weapon source of the burn
	private final DamageSource source;

	private float procCdCount;

	public Ablaze(PlayState state, float i, BodyData p, BodyData v, float damage, DamageSource source) {
		super(state, i, false, p, v);
		this.procCdCount = 0;
		this.damage = damage;
		this.source = source;
	}

	@Override
	public void onInflict() {
		ParticleEntity particleEntity = new ParticleEntity(state, inflicted.getSchmuck(), Particle.FIRE, LINGER, duration + LINGER,
				true, SyncType.NOSYNC)
				.setPrematureOff(LINGER)
				.setShowOnInvis(true);
		if (!state.isServer()) {
			((PlayStateClient) state).addEntity(particleEntity.getEntityID(), particleEntity, false, PlayStateClient.ObjectLayer.EFFECT);
		}
	}

	private static final float PROC_CD = 0.5f;
	@Override
	public void timePassing(float delta) {
		super.timePassing(delta);
		if (procCdCount >= PROC_CD) {
			procCdCount -= PROC_CD;
			inflicted.receiveDamage(damage, new Vector2(), inflicter, true, null, source, DamageTag.FIRE);
		}
		procCdCount += delta;
	}
	
	@Override
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
