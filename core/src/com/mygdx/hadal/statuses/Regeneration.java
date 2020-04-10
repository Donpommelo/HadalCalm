package com.mygdx.hadal.statuses;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.SoundEntity.soundSyncType;
import com.mygdx.hadal.schmucks.bodies.SoundEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.utils.Stats;

public class Regeneration extends Status {
	
	private ParticleEntity regenParticles;
	private SoundEntity regenSound;
	
	private float heal;
	
	public Regeneration(PlayState state, float i, BodyData p, BodyData v, float heal) {
		super(state, i, false, p, v);
		this.heal = heal;
	}
	
	@Override
	public void onInflict() {
		regenParticles = new ParticleEntity(state, inflicted.getSchmuck(), Particle.REGEN, duration, 0.0f, true, particleSyncType.CREATESYNC);
		regenSound =  new SoundEntity(state, inflicted.getSchmuck(), SoundEffect.MAGIC21_HEAL, 0.25f, true, true, soundSyncType.TICKSYNC);
	}
	
	@Override
	public void onRemove() {
		if (regenParticles != null) {
			regenParticles.setDespawn(true);
			regenParticles.turnOff();
		}
		
		if (regenSound != null) {
			regenSound.terminate();
		}
	}

	@Override
	public void statChanges() {
		inflicted.setStat(Stats.HP_REGEN, inflicted.getStat(Stats.HP_REGEN) + heal);
	}
}
