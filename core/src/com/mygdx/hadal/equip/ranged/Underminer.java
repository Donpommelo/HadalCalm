package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.UserDataType;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.utils.Constants;

public class Underminer extends RangedWeapon {

	private static final int clipSize = 1;
	private static final int ammoSize = 22;
	private static final float shootCd = 0.5f;
	private static final float shootDelay = 0.1f;
	private static final float reloadTime = 0.75f;
	private static final int reloadAmount = 0;
	private static final float baseDamage = 40.0f;
	private static final float recoil = 8.5f;
	private static final float knockback = 10.0f;
	private static final float projectileSpeed = 30.0f;
	private static final Vector2 projectileSize = new Vector2(36, 30);
	private static final Vector2 projectileSpriteSize = new Vector2(54, 45);
	private static final float lifespan = 4.0f;
	
	private static final Sprite projSprite = Sprite.DRILL;
	private static final Sprite weaponSprite = Sprite.MT_BLADEGUN;
	private static final Sprite eventSprite = Sprite.P_BLADEGUN;
	
	private static final float drillSpeed = 2.5f;
	private static final float drillDuration = 1.0f;

	private static final float raycastRange = 8.0f;

	private static final Vector2 fragSize = new Vector2(36, 30);
	private static final float fragLifespan = 2.0f;
	private static final float fragDamage = 15.0f;
	private static final float fragKnockback = 25.0f;
	private static final float fragSpeed = 4.0f;
	
	private static final float bombSpeed = 20.0f;
	private static final float bombLifespan = 1.5f;
	private static final int numBombs = 3;
	private static final int spread = 30;
	
	private static final int explosionRadius = 100;
	private static final float explosionDamage = 25.0f;
	private static final float explosionKnockback = 18.0f;
	
	public Underminer(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.FIRE10.playUniversal(state, startPosition, 0.8f, false);

		Hitbox hbox = new Hitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		hbox.setPassability((short) (Constants.BIT_PROJECTILE | Constants.BIT_WALL | Constants.BIT_PLAYER | Constants.BIT_ENEMY | Constants.BIT_SENSOR | Constants.BIT_DROPTHROUGHWALL));
		hbox.setSpriteSize(projectileSpriteSize);
		hbox.setGravity(3.0f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(),  baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			private boolean drilling;
			private boolean activated;
			private float invuln;
			
			private float drillCount;
			private static final int numDrills = 6;

			private final Vector2 angle = new Vector2();
			private final Vector2 raycast = new Vector2(0, raycastRange);
			private final Vector2 entityLocation = new Vector2();
			boolean wallDetected;
			@Override
			public void controller(float delta) {
				
				if (!activated) {
					hbox.setTransform(hbox.getPosition(), MathUtils.atan2(hbox.getLinearVelocity().y , hbox.getLinearVelocity().x));
				}
				
				if (drilling && invuln <= 0) {
					drillCount += delta;
					
					if (drillCount >= drillDuration && !activated) {
						drilling = false;
						activate();
					}
				}
				
				//invulnerability is incremented to prevent hbox from detonating immediately upon hitting a corner
				if (invuln > 0) {
					invuln -= delta;
				}
				
				if (activated) {
					Array<Vector2> drillVelocities = new Array<>();
					for (int i = 0; i < numDrills; i++) {
						float angleOffset = hbox.getAngle() + MathUtils.degRad * i / numDrills * 360;
						entityLocation.set(hbox.getPosition());
						angle.set(entityLocation).add(raycast.setAngleRad(angleOffset));
						wallDetected = false;

						if (entityLocation.x != angle.x || entityLocation.y != angle.y) {
							state.getWorld().rayCast((fixture, point, normal, fraction) -> {
								if (fixture.getFilterData().categoryBits == Constants.BIT_WALL) {
									wallDetected = true;
								}
								return -1.0f;
							}, entityLocation, angle);
						}

						if (wallDetected) {
							drillVelocities.add(new Vector2(0, 1).setAngleRad(angleOffset).scl(fragSpeed));
						}
					}

					Vector2[] positions = new Vector2[drillVelocities.size];
					Vector2[] velocities = new Vector2[drillVelocities.size];
					float[] fragVelocities = new float[drillVelocities.size * numBombs];
					entityLocation.set(hbox.getPixelPosition());
					for (int i = 0; i < drillVelocities.size; i++) {
						positions[i] = entityLocation;
						velocities[i] = drillVelocities.get(i);
						for (int j = 0; j < numBombs; j++) {
							fragVelocities[numBombs * i + j] = MathUtils.random(-spread, spread);
						}
					}
					SyncedAttack.UNDERMINER_DRILL.initiateSyncedAttackMulti(state, user, positions, velocities, fragVelocities);
					hbox.die();
				}
			}
			
			@Override
			public void onHit(HadalData fixB) {
				if (fixB != null) {
					if (fixB.getType().equals(UserDataType.WALL)) {
						
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
				}
			}
		});
	}

	public static Hitbox[] createUndermineDrills(PlayState state, Schmuck user, Vector2[] startPosition, Vector2[] startVelocity, float[] extraFields) {
		Hitbox[] hboxes = new Hitbox[startPosition.length];
		if (startPosition.length != 0) {
			for (int i = 0; i < startPosition.length; i++) {
				final int drillNum = i;
				Hitbox frag = new Hitbox(state, startPosition[i], fragSize, fragLifespan, startVelocity[i], user.getHitboxfilter(),
						true, true, user, projSprite);
				frag.addStrategy(new ControllerDefault(state, frag, user.getBodyData()));
				frag.addStrategy(new AdjustAngle(state, frag, user.getBodyData()));
				frag.addStrategy(new DamageStandard(state, frag, user.getBodyData(),  fragDamage, fragKnockback, DamageTypes.RANGED));
				frag.addStrategy(new HitboxStrategy(state, frag, user.getBodyData()) {

					@Override
					public void onHit(HadalData fixB) {
						if (fixB != null) {
							if (fixB.getType().equals(UserDataType.WALL)) {
								hbox.die();
							}
						}
					}

					@Override
					public void die() {
						for (int i = 0; i < numBombs; i++) {
							if (extraFields.length > drillNum * numBombs + i) {
								Hitbox bomb = new Hitbox(state, hbox.getPixelPosition(), fragSize, bombLifespan,
										new Vector2(startVelocity[drillNum]).setAngleDeg(startVelocity[drillNum].angleDeg() +
												extraFields[drillNum * numBombs + i]).nor().scl(bombSpeed),
										user.getHitboxfilter(), true, true, user, projSprite);
								bomb.setSyncDefault(false);
								bomb.setGravity(3.0f);
								bomb.setDurability(2);

								bomb.addStrategy(new ControllerDefault(state, bomb, user.getBodyData()));
								bomb.addStrategy(new AdjustAngle(state, bomb, user.getBodyData()));
								bomb.addStrategy(new DamageStandard(state, bomb, user.getBodyData(),  fragDamage, fragKnockback, DamageTypes.RANGED));
								bomb.addStrategy(new ContactWallDie(state, bomb, user.getBodyData()).setDelay(0.1f));
								bomb.addStrategy(new DieExplode(state, bomb, user.getBodyData(), explosionRadius, explosionDamage,
										explosionKnockback, user.getHitboxfilter(), false));
								bomb.addStrategy(new DieSound(state, bomb, user.getBodyData(), SoundEffect.EXPLOSION6, 0.25f).setSynced(false));
								bomb.addStrategy(new FlashNearDeath(state, bomb, user.getBodyData(), 1.0f, false));

								if (!state.isServer()) {
									((ClientState) state).addEntity(bomb.getEntityID(), bomb, false, ClientState.ObjectLayer.HBOX);
								}
							}
						}
					}
				});
				hboxes[i] = frag;
			}
		}
		return hboxes;
	}
}
