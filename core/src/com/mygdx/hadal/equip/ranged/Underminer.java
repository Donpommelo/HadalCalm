package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactWallLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DieExplode;
import com.mygdx.hadal.strategies.hitbox.DieSound;
import com.mygdx.hadal.strategies.hitbox.FlashNearDeath;
import com.mygdx.hadal.strategies.hitbox.Spread;
import com.mygdx.hadal.utils.Constants;

public class Underminer extends RangedWeapon {

	private final static int clipSize = 1;
	private final static int ammoSize = 22;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.1f;
	private final static float reloadTime = 0.5f;
	private final static int reloadAmount = 0;
	private final static float baseDamage = 40.0f;
	private final static float recoil = 8.5f;
	private final static float knockback = 10.0f;
	private final static float projectileSpeed = 30.0f;
	private final static Vector2 projectileSize = new Vector2(54, 45);
	private final static float lifespan = 4.0f;
	
	private final static Sprite projSprite = Sprite.DRILL;
	private final static Sprite weaponSprite = Sprite.MT_BLADEGUN;
	private final static Sprite eventSprite = Sprite.P_BLADEGUN;
	
	private final static float drillSpeed = 3.0f;
	private final static float drillDuration = 1.0f;
	private final static float activatedLifespan = 0.8f;
	private final static float activatedSpinSpeed = 8.0f;

	private final static float raycastRange = 8.0f;

	private final static Vector2 fragSize = new Vector2(36, 30);
	private final static float fragLifespan = 2.0f;
	private final static float fragDamage = 15.0f;
	private final static float fragKnockback = 25.0f;
	private final static float fragSpeed = 4.0f;
	
	private final static float bombSpeed = 20.0f;
	private final static float bombLifespan = 1.5f;
	private final static int numBombs = 4;
	private final static int spread = 30;
	
	private final static int explosionRadius = 100;
	private final static float explosionDamage = 25.0f;
	private final static float explosionKnockback = 18.0f;
	
	public Underminer(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.FIRE10.playUniversal(state, startPosition, 0.8f, false);

		Hitbox hbox = new Hitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		hbox.setPassability((short) (short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_SENSOR | Constants.BIT_DROPTHROUGHWALL));

		hbox.setGravity(3.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(),  baseDamage, knockback, DamageTypes.RANGED));

		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			private boolean drilling = false;
			private boolean activated = false;
			private float invuln = 0.0f;
			
			private float drillCount = 0;
			private final static float procCd = 0.2f;
			private float procCdCount = procCd;
			
			private Vector2 angle = new Vector2();
			private Vector2 raycast = new Vector2(0, raycastRange);
			boolean wallDetected;

			@Override
			public void controller(float delta) {
				
				if (!activated) {
					hbox.setTransform(hbox.getPosition(), (float) (Math.atan2(hbox.getLinearVelocity().y , hbox.getLinearVelocity().x)));
				}
				
				if (drilling && invuln <= 0) {
					drillCount += delta;
					
					if (drillCount >= drillDuration && !activated) {
						drilling = false;
						activate();
					}
				}
				
				//invuln is incremented to prevent hbox from detonating immediately upon hitting a corner
				if (invuln > 0) {
					invuln -= delta;
				}
				
				if (activated) {
					if (procCdCount >= procCd) {
						procCdCount -= procCd;

						angle.set(hbox.getPosition()).add(raycast.setAngleRad(hbox.getAngle()));
						wallDetected = false;
						state.getWorld().rayCast(new RayCastCallback() {

							@Override
							public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
								
								if (fixture.getFilterData().categoryBits == (short) Constants.BIT_WALL) {
									wallDetected = true;
								}
								return -1.0f;
							}
							
						}, hbox.getPosition(), angle);
						
						if (wallDetected) {
							Hitbox frag = new Hitbox(state, hbox.getPixelPosition(), fragSize, fragLifespan, new Vector2(0, 1).setAngleRad(hbox.getAngle()).scl(fragSpeed), filter, true, true, user, projSprite);
							frag.addStrategy(new ControllerDefault(state, frag, user.getBodyData()));
							frag.addStrategy(new AdjustAngle(state, frag, user.getBodyData()));
							frag.addStrategy(new DamageStandard(state, frag, user.getBodyData(),  fragDamage, fragKnockback, DamageTypes.RANGED));
							frag.addStrategy(new HitboxStrategy(state, frag, user.getBodyData()) {
								
								@Override
								public void onHit(HadalData fixB) {
									if (fixB != null) {
										if (fixB.getType().equals(UserDataTypes.WALL)) {
											
											for (int i = 0; i < numBombs; i++) {
												Hitbox bomb = new Hitbox(state, hbox.getPixelPosition(), fragSize, bombLifespan, new Vector2(0, 1).setAngleRad(hbox.getAngle()).scl(bombSpeed), 
														filter, true, true, user, projSprite);
												bomb.setGravity(3.0f);
												bomb.setDurability(2);
												
												bomb.addStrategy(new ControllerDefault(state, bomb, user.getBodyData()));
												bomb.addStrategy(new AdjustAngle(state, bomb, user.getBodyData()));
												bomb.addStrategy(new DamageStandard(state, bomb, user.getBodyData(),  fragDamage, fragKnockback, DamageTypes.RANGED));
												bomb.addStrategy(new ContactWallLoseDurability(state, bomb, user.getBodyData()));
												bomb.addStrategy(new DieExplode(state, bomb, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback, (short) 0));
												bomb.addStrategy(new DieSound(state, bomb, user.getBodyData(), SoundEffect.EXPLOSION6, 0.25f));
												bomb.addStrategy(new FlashNearDeath(state, bomb, user.getBodyData(), 1.0f));
												bomb.addStrategy(new Spread(state, bomb, user.getBodyData(), spread));
											}
										}
										hbox.die();
									}
								}
							});
						}
					}
					procCdCount += delta;
				}
			}
			
			@Override
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					
					if (fixB.getType().equals(UserDataTypes.WALL)) {
						
						//upon hitting a wall, hbox activates and begins drilling in a straight line
						if (!drilling) {
							drilling = true;
							hbox.setLinearVelocity(hbox.getLinearVelocity().nor().scl(drillSpeed));
							hbox.setGravityScale(0);
							invuln = 0.1f;
						} else {
							//if already activated (i.e drilling to other side of wall), hbox explodes
							if (invuln <= 0) {
								drilling = false;
								activate();
							}
						}
					}
				}
			}
			
			private void activate() {
				if (!activated) {
					activated = true;
					hbox.setLinearVelocity(0, 0);
					hbox.setAngularVelocity(activatedSpinSpeed);
					hbox.setLifeSpan(activatedLifespan);
				}
			}
		});
	}
}
