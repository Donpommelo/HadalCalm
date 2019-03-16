package com.mygdx.hadal.statuses;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class Ablaze extends Status {

	private static String name = "Ablaze";
	private static String descr = "Stop Drop and Roll";
	
	private ParticleEntity fire;
	
	public Ablaze(PlayState state, float i, BodyData p, BodyData v) {
		super(state, i, name, descr, false, true, p, v);
	}
	
	@Override
	public void onInflict(Status s) {
		if (s.equals(this)) {
			fire = new ParticleEntity(state, inflicted.getSchmuck(), Particle.FIRE, 3.0f, 0.0f, true, particleSyncType.TICKSYNC);
		}
	}
	
	@Override
	public void onRemove(Status s) {
		if (s.equals(this) && fire != null) {
			fire.setDespawn(true);
			fire.turnOff();
		}
	}
	
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
