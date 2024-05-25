package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * This status makes units display particles on death. This is used by certain enemies.
 * @author Graberry Ghumbly
 */
public class DeathParticles extends Status {
	
	//this is the particle effect that will be displayed
	private final Particle particle;
	
	//this is the duration of the particle.
	private final float duration;
	
	public DeathParticles(PlayState state, BodyData p, Particle particle, float duration) {
		super(state, p);
		this.particle = particle;
		this.duration = duration;
	}
	
	@Override
	public void onDeath(BodyData perp, DamageSource source, DamageTag... tags) {
		new ParticleEntity(state, new Vector2(inflicted.getSchmuck().getPixelPosition()), particle, duration, true, SyncType.CREATESYNC);
	}
}
