package com.mygdx.hadal.statuses;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class Poisoned extends Status {

	private ParticleEntity poison;
	
	public Poisoned(PlayState state, float i, BodyData p, BodyData v) {
		super(state, i, false, p, v);
	}

	@Override
	public void onRemove(Status s) {
		if (s.equals(this) && poison != null) {
			poison.setDespawn(true);
			poison.turnOff();
		}
	}
	
	@Override
	public void timePassing(float delta) {
		super.timePassing(delta);
		if (poison == null) {
			poison = new ParticleEntity(state, inflicted.getSchmuck(), Particle.POISON, duration, 0.0f, true, particleSyncType.TICKSYNC);
		}
	}
	
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
