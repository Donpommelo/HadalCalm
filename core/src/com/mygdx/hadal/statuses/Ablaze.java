package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
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

	private float procCdCount;

	public Ablaze(PlayState state, float i, BodyData p, BodyData v, float damage, DamageSource source) {
		super(state, i, false, p, v);
		this.procCdCount = 0;
		this.damage = damage;
		this.source = source;
	}

	@Override
	public void onInflict() {
		EffectEntityManager.getParticle(state, new ParticleCreate(Particle.FIRE, inflicted.getSchmuck())
				.setLifespan(duration)
				.setShowOnInvis(true));
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
