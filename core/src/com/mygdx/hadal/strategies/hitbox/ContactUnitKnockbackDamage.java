package com.mygdx.hadal.strategies.hitbox;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;

/**
 * @author Zachary Tu
 *
 */
public class ContactUnitKnockbackDamage extends HitboxStrategy {
	
	private final static float lifespan = 1.0f;
	private final static float speedThreshold = 30.0f;
	private static final float procCd = 0.03f;
	private static final float maxDamage = 150.0f;
	
	public ContactUnitKnockbackDamage(PlayState state, Hitbox proj, BodyData user) {
		super(state, proj, user);
	}
	
	@Override
	public void onHit(HadalData fixB) {
		if (fixB != null) {
			if (fixB.getType().equals(UserDataTypes.BODY)) {
				final BodyData vic = (BodyData) fixB;
				
				Vector2 hitboxSize = new Vector2();
				hitboxSize.set(vic.getSchmuck().getSize()).add(5, 5);
				
				Hitbox hbox = new Hitbox(state, new Vector2(), hitboxSize, lifespan, new Vector2(), creator.getSchmuck().getHitboxfilter(),  true, true, creator.getSchmuck(), Sprite.NOTHING);
				hbox.makeUnreflectable();
				
				hbox.addStrategy(new ControllerDefault(state, hbox, creator));
				hbox.addStrategy(new FixedToEntity(state, hbox, vic, new Vector2(), new Vector2(), true));
				hbox.addStrategy(new HitboxStrategy(state, hbox, creator) {
					
					private float procCdCount;
					private float lastVelo;
					
					@Override
					public void controller(float delta) {
						if (procCdCount < procCd) {
							procCdCount += delta;
						}
						lastVelo = Math.min(vic.getSchmuck().getBody().getLinearVelocity().len2(), maxDamage);
					}
					
					@Override
					public void onHit(HadalData fixB) {
						if (procCdCount > procCd) {
							if (fixB != null) {
								if (fixB.getType().equals(UserDataTypes.WALL)) {
									if (lastVelo > speedThreshold) {
										vic.receiveDamage(lastVelo, new Vector2(), creator, true, DamageTypes.WHACKING);
										new ParticleEntity(state, hbox.getPixelPosition(), Particle.EXPLOSION, 1.0f, true, particleSyncType.CREATESYNC);
										hbox.die();
									}
								}
								if (fixB.getType().equals(UserDataTypes.BODY)) {
									if (lastVelo > speedThreshold) {
										vic.receiveDamage(lastVelo, new Vector2(), creator, true, DamageTypes.WHACKING);
										fixB.receiveDamage(lastVelo, new Vector2(), creator, true, DamageTypes.WHACKING);
										new ParticleEntity(state, hbox.getPixelPosition(), Particle.EXPLOSION, 1.0f, true, particleSyncType.CREATESYNC);
										hbox.die();
									}
								}
							}
						}
					}
				});
			}
		}
	}
}
