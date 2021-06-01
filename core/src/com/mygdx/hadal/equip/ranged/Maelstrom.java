package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;

public class Maelstrom extends RangedWeapon {

	private static final int clipSize = 3;
	private static final int ammoSize = 18;
	private static final float shootCd = 0.75f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 1.5f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 12.0f;
	private static final float recoil = 6.0f;
	private static final float knockback = -8.0f;
	private static final float projectileSpeed = 20.0f;
	private static final Vector2 projectileSize = new Vector2(20, 20);
	private static final float lifespan = 1.8f;
	
	private static final float explosionInterval = 0.06f;
	private static final float explosionDuration = 0.1f;
	private static final int explosionMaxSize = 250;
	private static final float explosionGrowth = 8.0f;
	
	private static final Sprite projSprite = Sprite.HURRICANE;
	private static final Sprite weaponSprite = Sprite.MT_STORMCALLER;
	private static final Sprite eventSprite = Sprite.P_STORMCALLER;
	
	public Maelstrom(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}

	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.WIND2.playUniversal(state, startPosition, 0.8f, false);

		Hitbox storm = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, false, true, user, Sprite.NOTHING);
		storm.setEffectsHit(false);
		storm.setEffectsVisual(false);
		storm.setRestitution(0.5f);
		
		storm.addStrategy(new ControllerDefault(state, storm, user.getBodyData()));
		storm.addStrategy(new CreateParticles(state, storm, user.getBodyData(), Particle.STORM, 0.0f, 1.0f));
		storm.addStrategy(new CreateSound(state, storm, user.getBodyData(), SoundEffect.WIND3, 0.6f, true));
		storm.addStrategy(new HitboxStrategy(state, storm, user.getBodyData()) {
			
			private float controllerCount = 0;
			private final Vector2 explosionSize = new Vector2(projectileSize);
			
			@Override
			public void create() {
				
				//Set hurricane to have constant angular velocity for visual effect.
				hbox.setAngularVelocity(5);
			}
			
			@Override
			public void controller(float delta) {
				
				controllerCount += delta;

				//This hbox periodically spawns hboxes on top of itself.
				while (controllerCount >= explosionInterval) {
					controllerCount -= explosionInterval;
					
					Hitbox pulse = new Hitbox(state, hbox.getPixelPosition(), explosionSize, explosionDuration, new Vector2(), storm.getFilter(), true, true, user, projSprite);
					pulse.setSyncDefault(false);
					pulse.setSyncInstant(true);
					pulse.setEffectsMovement(false);

					pulse.addStrategy(new ControllerDefault(state, pulse, user.getBodyData()));
					pulse.addStrategy(new DamageStandard(state, pulse, user.getBodyData(), baseDamage, knockback, DamageTypes.EXPLOSIVE, DamageTypes.RANGED).setStaticKnockback(true));
					pulse.addStrategy(new HitboxStrategy(state, pulse, user.getBodyData()) {
						
						@Override
						public void create() {
							hbox.setAngle(storm.getAngle());
							hbox.setAngularVelocity(5);
						}
					});
					
					//spawned hboxes get larger as hbox moves
					if (explosionSize.x <= explosionMaxSize) {
						explosionSize.add(explosionGrowth, explosionGrowth);
					}
				}
			}
		});
	}
}
