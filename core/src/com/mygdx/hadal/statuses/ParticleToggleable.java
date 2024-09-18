package com.mygdx.hadal.statuses;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayStateClient;
import com.mygdx.hadal.states.PlayState;

/**
 *
 * @author Liggnut Leblatt
 */
public class ParticleToggleable extends Status {

	private final Particle particleType;
	private static final float LINGER = 0.0f;

	private ParticleEntity particle;
	private boolean activated;

	public ParticleToggleable(PlayState state, BodyData p, Particle particle) {
		super(state, p);
		this.particleType = particle;
	}

	public ParticleToggleable(PlayState state, BodyData p) {
		this(state, p, Particle.NOTHING);
	}

	@Override
	public void timePassing(float delta) {
		super.timePassing(delta);
		if (particle == null) {
			createParticle();

			if (!state.isServer()) {
				((PlayStateClient) state).addEntity(particle.getEntityID(), particle, false, PlayStateClient.ObjectLayer.EFFECT);
			}
		}

		if (activated) {
			particle.turnOn();
		} else {
			particle.turnOff();
		}
	}

	@Override
	public void onRemove() {
		if (particle != null) {
			if (state.isServer()) {
				particle.queueDeletion();
			} else {
				((PlayStateClient) state).removeEntity(particle.getEntityID());
			}
			particle = null;
		}
	}

	public void createParticle() {
		setParticle(new ParticleEntity(state, inflicted.getSchmuck(), particleType, LINGER, 0.0f,
				false, SyncType.NOSYNC));
	}

	public void setActivated(boolean activated) { this.activated = activated; }

	public void setParticle(ParticleEntity particle) { this.particle = particle; }
}
