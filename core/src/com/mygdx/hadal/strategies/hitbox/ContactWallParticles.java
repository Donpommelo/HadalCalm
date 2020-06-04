package com.mygdx.hadal.strategies.hitbox;

import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * This strategy generates projectiles whenever the attached hbox makes contact with a wall
 * @author Zachary Tu
 *
 */
public class ContactWallParticles extends HitboxStrategy {
	
	private final static float defaultDuration = 1.0f;
	
	//the effect that is to be created.
	private Particle effect;
	
	//how long should the particles last?
	private float duration;
	
	//have the particles appeared yet?
	//this is delayed, because at the momen of collision, box2d has not processed the point of contact yet.
	public boolean activated;
	
	public ContactWallParticles(PlayState state, Hitbox proj, BodyData user, Particle effect, float duration) {
		super(state, proj, user);
		this.effect = effect;
		this.duration = duration;
	}
	
	public ContactWallParticles(PlayState state, Hitbox proj, BodyData user, Particle effect) {
		this(state, proj, user, effect, defaultDuration);
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			if (fixB.getType().equals(UserDataTypes.WALL)) {
				new ParticleEntity(state, hbox.getPixelPosition(), effect, duration, true, particleSyncType.CREATESYNC);
			}
		}
	}
}
