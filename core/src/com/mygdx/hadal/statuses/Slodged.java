package com.mygdx.hadal.statuses;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class Slodged extends Status {

	private static String name = "Slodged";
	private static String descr = "Slowed";
	
	private ParticleEntity slodge;
	
	private float slow;
	
	public Slodged(PlayState state, float i, float slow, BodyData p, BodyData v) {
		super(state, i, name, descr, false, p, v);
		this.slow = slow;
	}
	
	@Override
	public void onRemove(Status s) {
		if (s.equals(this) && slodge != null) {
			slodge.setDespawn(true);
			slodge.turnOff();
		}
	}
	
	@Override
	public void timePassing(float delta) {
		super.timePassing(delta);
		
		if (slodge == null) {
			slodge = new ParticleEntity(state, inflicted.getSchmuck(), Particle.STUN, duration, 0.0f, true, particleSyncType.TICKSYNC);
		}
	}
	
	@Override
	public void statChanges(){
		inflicted.setBonusAirSpeed(-slow);
		inflicted.setBonusGroundSpeed(-slow);
	}
	
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
