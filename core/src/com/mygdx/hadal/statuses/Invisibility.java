package com.mygdx.hadal.statuses;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class Invisibility extends Status {
	
	public Invisibility(PlayState state, float i, BodyData p, BodyData v) {
		super(state, i, false, p, v);
		p.getSchmuck().setShader(Shader.INVISIBLE, i);
		new ParticleEntity(state, inflicted.getSchmuck(), Particle.SMOKE, 0.0f, 0.5f, true, particleSyncType.CREATESYNC);
	}
	
	@Override
	public void onRemove() {
		new ParticleEntity(state, inflicted.getSchmuck(), Particle.SMOKE, 0.0f, 0.5f, true, particleSyncType.CREATESYNC);
		inflicted.getSchmuck().decreaseShaderCount(duration);
	}
	
	@Override
	public void onShoot(Equipable tool) {
		inflicted.removeStatus(this);
	}
	
	@Override
	public void onAirBlast(Equipable tool) {
		inflicted.removeStatus(this);
	}
	
	@Override
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
