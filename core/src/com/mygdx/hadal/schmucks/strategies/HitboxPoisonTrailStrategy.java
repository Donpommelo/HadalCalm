package com.mygdx.hadal.schmucks.strategies;

import static com.mygdx.hadal.utils.Constants.PPM;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

public class HitboxPoisonTrailStrategy extends HitboxStrategy {
	
	private float poisonDamage, poisonDuration;
	private int poisonRadius;
	private short filter;
	
	private final static float poisonCd = 0.1f;
	private float poisonCdCount;
	
	public HitboxPoisonTrailStrategy(PlayState state, Hitbox proj, BodyData user, int poisonRadius, float poisonDamage, 
			float poisonDuration, short filter) {
		super(state, proj, user);
		this.poisonRadius = poisonRadius;
		this.poisonDamage = poisonDamage;
		this.poisonDuration = poisonDuration;
		this.filter = filter;
		
		this.poisonCdCount = 0;
		
		new ParticleEntity(state, hbox, Particle.POISON, 1.5f, 0, true, particleSyncType.CREATESYNC);
	}
	
	@Override
	public void controller(float delta) {
		
		if (poisonCdCount <= 0) {
			poisonCdCount = poisonCd;
			
			new Poison(state, poisonRadius, poisonRadius,
					(int)(this.hbox.getPosition().x * PPM), 
					(int)(this.hbox.getPosition().y * PPM), poisonDamage, poisonDuration, creator.getSchmuck(), false, filter);
		}
		poisonCdCount -= delta;
		
	}
}
