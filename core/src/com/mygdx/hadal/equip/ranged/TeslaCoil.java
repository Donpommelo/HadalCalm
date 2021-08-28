package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.UserDataTypes;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.utils.Constants;

import java.util.ArrayList;

public class TeslaCoil extends RangedWeapon {

	private static final int clipSize = 3;
	private static final int ammoSize = 27;
	private static final float shootCd = 0.3f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 1.8f;
	private static final int reloadAmount = 0;
	private static final float recoil = 0.0f;
	private static final float projectileSpeed = 100.0f;
	private static final Vector2 projectileSize = new Vector2(45, 45);
	private static final float lifespan = 4.5f;
	
	private static final Sprite projSprite = Sprite.PYLON;
	private static final Sprite weaponSprite = Sprite.MT_STORMCALLER;
	private static final Sprite eventSprite = Sprite.P_STORMCALLER;

	private static final float radius = 25.0f;
	private static final float pulseInterval = 1.0f;
	private static final float pulseDuration = 0.5f;
	private static final Vector2 pulseSize = new Vector2(75, 75);
	private static final float pulseDamage = 40.0f;
	private static final float pulseKnockback = 20.0f;
	
	//kep track of all coils laid so far
	private final ArrayList<Hitbox> coilsLaid = new ArrayList<>();

	public TeslaCoil(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.LAUNCHER.playUniversal(state, startPosition, 0.25f, false);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		
		final Vector2 endLocation = new Vector2(this.mouseLocation);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			private final Vector2 startLocation = new Vector2();
			private float distance;
			private boolean firstPlanted = false;
			private boolean planted = false;
			private boolean activated = false;
			private float controllerCount;
			
			@Override
			public void create() {
				//keep track of the coil's travel distance
				this.startLocation.set(hbox.getPixelPosition());
				this.distance = startLocation.dst(endLocation) - projectileSize.x;
			}
			
			private final Vector2 entityLocation = new Vector2();
			@Override
			public void controller(float delta) {

				//planted coils stop and activates
				if (firstPlanted) {
					firstPlanted = false;
					planted = true;

					if (hbox.getBody() != null) {
						hbox.setLinearVelocity(0, 0);
						hbox.getBody().setType(BodyType.StaticBody);
					}

					SoundEffect.METAL_IMPACT_1.playUniversal(state, startPosition, 0.5f, false);
				}
				
				//activated coils periodically check world for nearby coils
				if (planted) {
					
					controllerCount += delta;

					while (controllerCount >= pulseInterval) {
						controllerCount -= pulseInterval;
						
						activated = false;
						entityLocation.set(hbox.getPosition());
						hbox.getWorld().QueryAABB(fixture -> {
							if (fixture.getUserData() instanceof HitboxData) {
								if (coilsLaid.contains(((HitboxData) fixture.getUserData()).getHbox())) {
									if (!fixture.getUserData().equals(hbox.getHadalData())) {
										if (((HitboxData) fixture.getUserData()).getHbox().getLinearVelocity().isZero()) {
											coilPairActivated(state, ((HitboxData) fixture.getUserData()).getHbox());
										}
									}
								}
							}
							return true;
						}, entityLocation.x - radius, entityLocation.y - radius, entityLocation.x + radius, entityLocation.y + radius);
					}
					return;
				}
				
				//After reaching the location clicked, the coil is marked as planted
				if (startLocation.dst2(hbox.getPixelPosition()) >= distance * distance) {
					firstPlanted = true;
					controllerCount = pulseInterval;
				}
			}
			
			@Override
			public void onHit(HadalData fixB) {
				
				//activated coils do nothing when hit.
				if (planted) {
					return;
				}
				
				//unactivated coils should stop and plant when they hit a wall
				if (fixB == null) {
					firstPlanted = true;
				} else if (fixB.getType().equals(UserDataTypes.WALL)){
					firstPlanted = true;
				}
			}
			
			@Override
			public void die() {
				//remove dead coils from list
				coilsLaid.remove(hbox);
			}
			
			/**
			 * This activates when a coil performs its periodic check of nearby coils and finds one
			 * @param state: playstate
			 * @param hboxOther: the other coil to connect to
			 */
			public void coilPairActivated(PlayState state, Hitbox hboxOther) {
				
				if (!activated) {
					SoundEffect.ZAP.playUniversal(state, startPosition, 0.4f, false);

					activated = true;
					
					//draw a path of hitboxes between the 2 activated coils that damage enemies that pass through
					Vector2 pulsePosition = new Vector2(hbox.getPixelPosition());
					Vector2 pulsePath = hboxOther.getPixelPosition().sub(hbox.getPixelPosition());
					
					float dist = pulsePath.len();
					for (int i = 0; i < dist - pulseSize.x; i += pulseSize.x) {
						pulsePosition.add(pulsePath.nor().scl(pulseSize));
						
						Hitbox pulse = new RangedHitbox(state, pulsePosition, pulseSize, pulseDuration, new Vector2(), hbox.getFilter(), true, true, user, Sprite.NOTHING);
						pulse.setPassability((short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY));
						pulse.setEffectsHit(false);
						
						pulse.addStrategy(new ControllerDefault(state, pulse, user.getBodyData()));
						pulse.addStrategy(new CreateParticles(state, pulse, user.getBodyData(), Particle.LASER_PULSE, 0.0f, 0.1f).setParticleSize(50));
					}
					
					Hitbox hboxDamage = new RangedHitbox(state, new Vector2(hbox.getPixelPosition()).add(hboxOther.getPixelPosition()).scl(0.5f), 
							new Vector2(hboxOther.getPixelPosition().dst(hbox.getPixelPosition()), pulseSize.y), pulseDuration, new Vector2(), hbox.getFilter(), true, true, user, Sprite.NOTHING) {
						
						private final Vector2 newPosition = new Vector2();
						
						@Override
						public void create() {
							super.create();
							
							//this makes the laser hbox's lifespan unmodifiable
							setLifeSpan(pulseDuration);
							
							newPosition.set(hboxOther.getPixelPosition()).sub(hbox.getPixelPosition());

							//Rotate hitbox to match angle of fire.
							float newAngle = MathUtils.atan2(newPosition.y , newPosition.x);
							setTransform(getPosition().x, getPosition().y, newAngle);
						}
					};
					hboxDamage.setSyncDefault(false);
					hboxDamage.setEffectsVisual(false);
					
					hboxDamage.addStrategy(new ControllerDefault(state, hboxDamage, user.getBodyData()));
					hboxDamage.addStrategy(new DamageStandard(state, hboxDamage, user.getBodyData(), pulseDamage, pulseKnockback,
						DamageTypes.ENERGY, DamageTypes.RANGED).setStaticKnockback(true));
				}
			}
		});
			
		coilsLaid.add(hbox);
	}
}
