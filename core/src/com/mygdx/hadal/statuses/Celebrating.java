package com.mygdx.hadal.statuses;

import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * Ablaze units receive damage over time.
 * @author Glamhock Glecnicbasket
 */
public class Celebrating extends Status {

	private static final float DURATION = 10.0f;

	private float procCdCount;

	public Celebrating(PlayState state, BodyData p, BodyData v) {
		super(state, DURATION, false, p, v);
	}

	private static final float PROC_CD = 1.0f;
	@Override
	public void timePassing(float delta) {
		super.timePassing(delta);
		if (procCdCount >= PROC_CD) {
			procCdCount -= PROC_CD;

			EffectEntityManager.getParticle(state, new ParticleCreate(Particle.PARTY, inflicted.getSchmuck())
					.setLifespan(duration)
					.setSyncType(SyncType.CREATESYNC));
		}
		procCdCount += delta;
	}
}
