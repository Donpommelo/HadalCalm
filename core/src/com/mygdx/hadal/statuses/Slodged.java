package com.mygdx.hadal.statuses;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayStateClient;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.constants.Stats;

/**
 * Slodged units move slower in the ground and air
 * @author Brenzales Beldason
 */
public class Slodged extends Status {

	private static final float LINGER = 2.0f;

	//this is the magnitude of the slow.
	private final float slow;

	//this is the particle that is played over the victim
	private final Particle particle;

	public Slodged(PlayState state, float i, float slow, BodyData p, BodyData v, Particle particle) {
		super(state, i, false, p, v);
		this.slow = slow;
		this.particle = particle;
	}

	@Override
	public void onInflict() {
		if (!Particle.NOTHING.equals(particle)) {
			ParticleEntity particleEntity = new ParticleEntity(state, inflicted.getSchmuck(), particle, LINGER, duration + LINGER,
					true, SyncType.NOSYNC)
					.setPrematureOff(LINGER)
					.setShowOnInvis(true);
			if (!state.isServer()) {
				((PlayStateClient) state).addEntity(particleEntity.getEntityID(), particleEntity, false, PlayStateClient.ObjectLayer.EFFECT);
			}
		}
	}

	@Override
	public void statChanges() {
		inflicted.setStat(Stats.AIR_SPD, -slow);
		inflicted.setStat(Stats.GROUND_SPD, -slow);
		inflicted.setStat(Stats.JUMP_POW, -slow);
	}
	
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
