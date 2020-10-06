package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * Ablaze units receive damage over time.
 * @author Zachary Tu
 */
public class Ablaze extends Status {

	//this is the damage per proc of the unit
	private final float damage;

	//this particle entity follows the player
	private ParticleEntity fire;

	private float procCdCount;

	public Ablaze(PlayState state, float i, BodyData p, BodyData v, float damage) {
		super(state, i, false, p, v);
		this.procCdCount = 0;
		this.damage = damage;
	}
	
	@Override
	public void onRemove() {
		if (fire != null) {
			fire.setDespawn(true);
			fire.turnOff();
		}
	}
	
	@Override
	public void timePassing(float delta) {
		super.timePassing(delta);
		final float procCd = 0.5f;
		if (procCdCount >= procCd) {
			procCdCount -= procCd;
			inflicted.receiveDamage(damage, new Vector2(), inflicter, true, DamageTypes.FIRE);
		}
		procCdCount += delta;
		
		if (fire == null) {
			fire = new ParticleEntity(state, inflicted.getSchmuck(), Particle.FIRE, duration, 0.0f, true, particleSyncType.TICKSYNC);
		}
	}
	
	@Override
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
