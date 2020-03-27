package com.mygdx.hadal.statuses;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Stats;

public class Slodged extends Status {

	private ParticleEntity slodge;
	
	private float slow;
	
	public Slodged(PlayState state, float i, float slow, BodyData p, BodyData v) {
		super(state, i, false, p, v);
		this.slow = slow;
	}
	
	@Override
	public void onRemove() {
		if (slodge != null) {
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
		inflicted.setStat(Stats.AIR_SPD, -slow);
		inflicted.setStat(Stats.GROUND_SPD, -slow);
		inflicted.setStat(Stats.JUMP_POW, -slow);
	}
	
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
