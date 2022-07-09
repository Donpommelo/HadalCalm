package com.mygdx.hadal.statuses;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Stats;

/**
 * Slodged units move slower in the ground and air
 * @author Brenzales Beldason
 */
public class Slodged extends Status {

	//this is the magnitude of the slow.
	private final float slow;

	//this is the particle that is played over the victim
	private final Particle particle;

	private static final float linger = 2.0f;

	public Slodged(PlayState state, float i, float slow, BodyData p, BodyData v, Particle particle) {
		super(state, i, false, p, v);
		this.slow = slow;
		this.particle = particle;

		//need to set this to independent so its duration decrements for clients
		setClientIndependent(true);
	}

	@Override
	public void onInflict() {
		if (!Particle.NOTHING.equals(particle) && state.isServer()) {
			new ParticleEntity(state, inflicted.getSchmuck(), particle, linger, duration + linger,
					true, SyncType.CREATESYNC).setPrematureOff(linger);
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
