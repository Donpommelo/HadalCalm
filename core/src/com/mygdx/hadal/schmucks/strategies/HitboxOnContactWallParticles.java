package com.mygdx.hadal.schmucks.strategies;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;

/**
 * This strategy generates projectiles whenever the attached hbox makes contact with a wall
 * @author Zachary Tu
 *
 */
public class HitboxOnContactWallParticles extends HitboxStrategy {
	
	private final static float defaultDuration = 0.1f;
	
	//the effect that is to be created.
	private Particle effect;
	
	//how long should the particles last?
	private float duration;
	
	public HitboxOnContactWallParticles(PlayState state, Hitbox proj, BodyData user, Particle effect, float duration) {
		super(state, proj, user);
		this.effect = effect;
		this.duration = duration;
	}
	
	public HitboxOnContactWallParticles(PlayState state, Hitbox proj, BodyData user, Particle effect) {
		this(state, proj, user, effect, defaultDuration);
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB == null) {
			new ParticleEntity(state, new Vector2(hbox.getPixelPosition()),	effect, duration, true, particleSyncType.CREATESYNC);
		} else if (fixB.getType().equals(UserDataTypes.WALL)){
			new ParticleEntity(state, new Vector2(hbox.getPixelPosition()),	effect, duration, true, particleSyncType.CREATESYNC);
		}
	}
}
