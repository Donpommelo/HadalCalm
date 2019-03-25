package com.mygdx.hadal.statuses;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class Ablaze extends Status {

	private static String name = "Ablaze";
	private static String descr = "Stop Drop and Roll";
	
	private ParticleEntity fire;
	
	private float damage;
	private float procCdCount;
	private float procCd = .5f;
	
	public Ablaze(PlayState state, float i, BodyData p, BodyData v, float damage) {
		super(state, i, name, descr, false, p, v);
		this.damage = damage;
	}
	
	@Override
	public void onRemove(Status s) {
		if (s.equals(this)) {
			fire.setDespawn(true);
			fire.turnOff();
		}
	}
	
	@Override
	public void timePassing(float delta) {
		super.timePassing(delta);
		if (procCdCount >= procCd) {
			procCdCount -= procCd;
			inflicted.receiveDamage(damage, new Vector2(0, 0), inflicter, null, false);
		}
		procCdCount += delta;
		
		if (fire == null) {
			fire = new ParticleEntity(state, inflicted.getSchmuck(), Particle.FIRE, duration, 0.0f, true, particleSyncType.TICKSYNC);
		}
	}
	
	public statusStackType getStackType() {
		return statusStackType.REPLACE;
	}
}
