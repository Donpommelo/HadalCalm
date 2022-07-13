package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.HadalColor;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.schmucks.entities.ParticleEntity;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy generates projectiles whenever the attached hbox makes contact with a wall
 * @author Brugdalena Bequila
 */
public class ContactWallParticles extends HitboxStrategy {
	
	private static final float DEFAULT_DURATION = 1.0f;
	
	//the effect that is to be created.
	private final Particle effect;
	
	//how long should the particles last?
	private float duration;
	
	//this is the color of the particle. Can be set using the factory method
	private HadalColor color = HadalColor.NOTHING;
	
	//do we draw the particles at an offset from the hbox? (used for larger hboxes)
	private final Vector2 offset = new Vector2();
	private boolean isOffset;

	private SyncType syncType = SyncType.CREATESYNC;

	public ContactWallParticles(PlayState state, Hitbox proj, BodyData user, Particle effect) {
		super(state, proj, user);
		this.effect = effect;
		this.duration = DEFAULT_DURATION;
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			if (UserDataType.WALL.equals(fixB.getType())) {
				offset.set(hbox.getPixelPosition());
				
				if (isOffset) {
					offset.add(new Vector2(hbox.getLinearVelocity()).nor().scl(hbox.getSize().x / 2));
				}
				ParticleEntity particles = new ParticleEntity(state, offset, effect, duration, true, syncType).setColor(color);
				if (!state.isServer()) {
					((ClientState) state).addEntity(particles.getEntityID(), particles, false, ClientState.ObjectLayer.EFFECT);
				}
			}
		}
	}
	
	public ContactWallParticles setOffset(boolean isOffset) {
		this.isOffset = isOffset;
		return this;
	}
	
	public ContactWallParticles setDuration(float duration) {
		this.duration = duration;
		return this;
	}
	
	public ContactWallParticles setParticleColor(HadalColor color) {
		this.color = color;
		return this;
	}

	public ContactWallParticles setSyncType(SyncType syncType) {
		this.syncType = syncType;
		return this;
	}
}
