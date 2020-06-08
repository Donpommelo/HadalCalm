package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy generates projectiles whenever the attached hbox makes contact with a unit
 * @author Zachary Tu
 *
 */
public class ContactUnitParticles extends HitboxStrategy {
	
	private final static float defaultDuration = 1.0f;
	
	//the effect that is to be created.
	private Particle effect;
	
	//how long should the particles last?
	private float duration;
	
	//do we draw the particles at an offset from the hbox? (used for larger hboxes)
	private boolean isOffset = false;
	private boolean drawOnSelf = true;
	private Vector2 offset = new Vector2();
		
	public ContactUnitParticles(PlayState state, Hitbox proj, BodyData user, Particle effect) {
		super(state, proj, user);
		this.effect = effect;
		this.duration = defaultDuration;
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB instanceof BodyData) {
			
			if (drawOnSelf) {
				offset.set(hbox.getPixelPosition());
			} else {
				offset.set(fixB.getEntity().getPixelPosition());
			}
			
			if (isOffset) {
				offset.add(new Vector2(hbox.getLinearVelocity()).nor().scl(hbox.getSize().x / 2));
			}
			
			new ParticleEntity(state, offset, effect, duration, true, particleSyncType.CREATESYNC);
		}
	}
	
	public ContactUnitParticles setOffset(boolean isOffset) {
		this.isOffset = isOffset;
		return this;
	}
	
	public ContactUnitParticles setDuration(float duration) {
		this.duration = duration;
		return this;
	}
	
	public ContactUnitParticles setDrawOnSelf(boolean drawOnSelf) {
		this.drawOnSelf = drawOnSelf;
		return this;
	}
}
