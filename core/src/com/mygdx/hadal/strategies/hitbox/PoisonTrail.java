package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.event.Poison;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy makes a hbox continually create poison tiles as it travels
 * @author Gigawa Gnoheimer
 */
public class PoisonTrail extends HitboxStrategy {
	
	//the amount of damage the poison will deal, how long it will last and its size
	private final float poisonDamage, poisonDuration;
	private final int poisonRadius;
	
	//the hbox filter that determines who can be damaged by the poison
	private final short filter;
	
	//the time interval between creating poison
	private final Vector2 lastPosition = new Vector2();
	private final Vector2 poisonSize = new Vector2();

	//default fields that can be changed using factory method for alternate poison styles
	private Particle particle = Particle.POISON;
	private float lifespan = 1.5f;
	private float interval = 4096f;

	public PoisonTrail(PlayState state, Hitbox proj, BodyData user, Vector2 poisonSize, int poisonRadius, float poisonDamage, float poisonDuration, short filter) {
		super(state, proj, user);
		this.poisonRadius = poisonRadius;
		this.poisonDamage = poisonDamage;
		this.poisonDuration = poisonDuration;
		this.filter = filter;
		
		lastPosition.set(proj.getStartPos());
		this.poisonSize.set(poisonSize);
	}
	
	private final Vector2 entityLocation = new Vector2();
	@Override
	public void controller(float delta) {
		entityLocation.set(hbox.getPixelPosition());
		if (lastPosition.dst2(entityLocation) > poisonRadius * poisonRadius) {
			lastPosition.set(entityLocation);
			Poison poison = new Poison(state, entityLocation, poisonSize, poisonDamage, poisonDuration, creator.getSchmuck(), true,
					filter, DamageSource.SHILLERS_DEATHCAP)
				.setParticle(particle).setParticleLifespan(lifespan).setParticleInterval(interval);

			if (!state.isServer()) {
				((ClientState) state).addEntity(poison.getEntityID(), poison, false, ClientState.ObjectLayer.EFFECT);
			}
		}
	}

	public PoisonTrail setParticle(Particle particle, float lifespan, float interval) {
		this.particle = particle;
		this.lifespan = lifespan;
		this.interval = interval;
		return this;
	}
}
