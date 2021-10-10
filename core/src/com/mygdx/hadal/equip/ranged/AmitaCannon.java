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

public class AmitaCannon extends RangedWeapon {

	private static final int clipSize = 4;
	private static final int ammoSize = 32;
	private static final float shootCd = 0.4f;
	private static final float shootDelay = 0.1f;
	private static final float reloadTime = 1.6f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 8.8f;
	private static final float recoil = 6.0f;
	private static final float knockback = 13.0f;
	private static final float projectileSpeed = 38.0f;
	private static final Vector2 projectileSize = new Vector2(48, 48);
	private static final float lifespan = 1.0f;
	
	private static final int numOrbitals = 8;
	private static final float orbitalRange = 0.8f;
	private static final float orbitalSpeed = 720.0f;
	private static final Vector2 orbitalSize = new Vector2(24, 24);
	private static final float activatedSpeed = 40.0f;

	private static final Sprite projSprite = Sprite.ORB_ORANGE;
	private static final Sprite weaponSprite = Sprite.MT_STORMCALLER;
	private static final Sprite eventSprite = Sprite.P_STORMCALLER;
	
	public AmitaCannon(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount,
				true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.ELECTRIC_CHAIN.playUniversal(state, startPosition, 0.4f, false);

		//we create an invisible hitbox that moves in a straight line.
		Hitbox center = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, Sprite.NOTHING);
		center.setSyncDefault(false);
		center.setEffectsHit(false);
		center.setEffectsVisual(false);
		
		center.addStrategy(new ControllerDefault(state, center, user.getBodyData()));
		center.addStrategy(new ContactWallDie(state, center, user.getBodyData()));
		center.addStrategy(new DieSound(state, center, user.getBodyData(), SoundEffect.MAGIC3_BURST, 0.5f));
		center.addStrategy(new HitboxStrategy(state, center, user.getBodyData()) {
			
			private final Vector2 angle = new Vector2(0, orbitalRange);
			@Override
			public void create() {
				for (int i = 0; i < numOrbitals; i++) {
					angle.setAngleDeg(angle.angleDeg() + 360.0f / numOrbitals);
					
					//we create several orbiting projectiles that circle the invisible center
					//when the center hits a wall, the orbitals move outwards
					Hitbox orbital = new RangedHitbox(state, startPosition, orbitalSize, lifespan, startVelocity, filter, true, true, user, projSprite);
					orbital.setSyncDefault(false);
					orbital.setSyncInstant(true);
					orbital.setEffectsMovement(false);
					
					orbital.addStrategy(new ControllerDefault(state, orbital, user.getBodyData()));
					orbital.addStrategy(new DamageStandard(state, orbital, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED).setRepeatable(true));
					orbital.addStrategy(new ContactWallDie(state, orbital, user.getBodyData()));
					orbital.addStrategy(new DieParticles(state, orbital, user.getBodyData(), Particle.ORB_IMPACT));
					orbital.addStrategy(new HitboxStrategy(state, orbital, user.getBodyData()) {
						
						private final Vector2 centerPos = new Vector2();
						private final Vector2 offset = new Vector2();
						private float currentAngle = angle.angleDeg();
						private boolean activated = false;
						private float controllerCount;
						private static final float pushInterval = 1 / 60f;
						@Override
						public void controller(float delta) {
							controllerCount += delta;
							while (controllerCount >= pushInterval) {
								controllerCount -= pushInterval;

								if (center.getBody() != null && center.isAlive()) {
									currentAngle += orbitalSpeed * delta;

									centerPos.set(center.getPosition());
									offset.set(0, orbitalRange).setAngleDeg(currentAngle);
									orbital.setTransform(centerPos.add(offset), orbital.getAngle());
								} else if (!activated) {
									activated = true;
									hbox.setLinearVelocity(new Vector2(0, activatedSpeed).setAngleDeg(currentAngle));
									hbox.setLifeSpan(lifespan);
								}
							}
						}
					});
				}
			}
		});
	}
}
