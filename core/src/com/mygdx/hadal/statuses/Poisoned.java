package com.mygdx.hadal.statuses;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class Poisoned extends Status {

	private static String name = "Poisoned";
	private static String descr = "";
	
	private ParticleEntity poison;
	
	public Poisoned(PlayState state, float i, BodyData p, BodyData v) {
		super(state, i, name, descr, false, true, p, v);
	}
	
	@Override
	public void onInflict(Status s) {
		if (s.equals(this)) {
			poison = new ParticleEntity(state, inflicted.getSchmuck(), Particle.POISON, 3.0f, 0.0f, true, particleSyncType.TICKSYNC);
		}
	}
	
	@Override
	public void onRemove(Status s) {
		if (s.equals(this) && poison != null) {
			poison.setDespawn(true);
			poison.turnOff();
		}
	}
	
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
