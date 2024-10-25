package com.mygdx.hadal.statuses;

import com.mygdx.hadal.constants.Stats;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.managers.EffectEntityManager;
import com.mygdx.hadal.requests.ParticleCreate;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * Slodged units move slower in the ground and air
 * @author Brenzales Beldason
 */
public class Slodged extends Status {

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
			EffectEntityManager.getParticle(state, new ParticleCreate(particle, inflicted.getSchmuck())
					.setLifespan(duration)
					.setShowOnInvis(true));
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
