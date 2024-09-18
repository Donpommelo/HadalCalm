package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayStateClient;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy generates projectiles whenever the attached hbox makes contact with a unit
 * @author Flechnold Forseradish
 */
public class ContactUnitParticles extends HitboxStrategy {
	
	private static final float DEFAULT_DURATION = 1.0f;
	
	//the effect that is to be created.
	private final Particle effect;
	
	//how long should the particles last?
	private float duration;
	
	//this is the color of the particle. change using the factory method
	private HadalColor color = HadalColor.NOTHING;

	//do we draw the particles at an offset from the hbox? (used for larger hboxes)
	private boolean isOffset;
	private final Vector2 offset = new Vector2();

	private SyncType syncType = SyncType.CREATESYNC;

	//do we draw the particle on the hbox? If not, we draw it on the entity it hits instead. used for even longer hboxes like the laser rifle.
	private boolean drawOnSelf = true;
	
	public ContactUnitParticles(PlayState state, Hitbox proj, BodyData user, Particle effect) {
		super(state, proj, user);
		this.effect = effect;
		this.duration = DEFAULT_DURATION;
	}
	
	@Override
	public void onHit(HadalData fixB, Body body) {
		if (fixB instanceof BodyData) {
			
			if (drawOnSelf) {
				offset.set(hbox.getPixelPosition());
			} else {
				offset.set(fixB.getEntity().getPixelPosition());
			}
			if (isOffset) {
				offset.add(new Vector2(hbox.getLinearVelocity()).nor().scl(hbox.getSize().x / 2));
			}
			ParticleEntity particles = new ParticleEntity(state, offset, effect, duration, true, syncType).setColor(color);
			if (!state.isServer()) {
				((PlayStateClient) state).addEntity(particles.getEntityID(), particles, false, PlayStateClient.ObjectLayer.EFFECT);
			}
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
	
	public ContactUnitParticles setParticleColor(HadalColor color) {
		this.color = color;
		return this;
	}

	public ContactUnitParticles setSyncType(SyncType syncType) {
		this.syncType = syncType;
		return this;
	}
}
