package com.mygdx.hadal.schmucks.strategies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * This strategy makes a hbox continually create poison tiles as it travels
 * @author Zachary Tu
 *
 */
public class HitboxPoisonTrailStrategy extends HitboxStrategy {
	
	//the amount of damage the poison will deal, how long it will last and its size
	private float poisonDamage, poisonDuration;
	private int poisonRadius;
	
	//the hbox filter that determines who can be damaged by the poison
	private short filter;
	
	//the time interval between creating posion
	private final static float poisonCd = 0.1f;
	private float poisonCdCount;
	
	public HitboxPoisonTrailStrategy(PlayState state, Hitbox proj, BodyData user, int poisonRadius, float poisonDamage, float poisonDuration, short filter) {
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
			new Poison(state, this.hbox.getPixelPosition(), new Vector2(poisonRadius, poisonRadius), poisonDamage, poisonDuration, creator.getSchmuck(), false, filter);
		}
		poisonCdCount -= delta;
	}
}
