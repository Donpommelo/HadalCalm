package com.mygdx.hadal.statuses;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Shader;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class Invisibility extends Status {
	
	private final static float fadeTime = 0.5f;
	private float fadeCount;
	
	public Invisibility(PlayState state, float i, BodyData p, BodyData v) {
		super(state, i, false, p, v);
		p.getSchmuck().setShader(Shader.INVISIBLE, i);
		new ParticleEntity(state, inflicted.getSchmuck(), Particle.SMOKE, 1.0f, 3.0f, true, particleSyncType.CREATESYNC);
		
		if (inflicted instanceof PlayerBodyData) {
			((PlayerBodyData) inflicted).getPlayer().setInvisible(true);
		}
		
		fadeCount = fadeTime;
	}
	
	@Override
	public void timePassing(float delta) {
		if (fadeCount >= 0) {
			fadeCount -= delta;
		}
	}
	
	@Override
	public void onInflict() {
		SoundEffect.MAGIC27_EVIL.playUniversal(state, inflicted.getSchmuck().getPixelPosition(), 1.0f, false);
	}
	
	@Override
	public void onRemove() {
		
		new ParticleEntity(state, inflicted.getSchmuck(), Particle.SMOKE, 1.0f, 3.0f, true, particleSyncType.CREATESYNC);
		inflicted.getSchmuck().endShader(Shader.INVISIBLE);
		
		if (inflicted instanceof PlayerBodyData) {
			((PlayerBodyData) inflicted).getPlayer().setInvisible(false);
		}
	}
	
	@Override
	public void onShoot(Equipable tool) {
		if (fadeCount <= 0) {
			inflicted.removeStatus(this);
		}
	}
	
	@Override
	public void whileAttacking(float delta, Equipable tool) {
		if (fadeCount <= 0) {
			if (tool instanceof MeleeWeapon) {
				inflicted.removeStatus(this);
			}
		}
	}
	
	@Override
	public void onDeath(BodyData perp) {
		inflicted.getSchmuck().endShader(Shader.INVISIBLE);
		
		if (inflicted instanceof PlayerBodyData) {
			((PlayerBodyData) inflicted).getPlayer().setInvisible(false);
		}
	}
	
	@Override
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
