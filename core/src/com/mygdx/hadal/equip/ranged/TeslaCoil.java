package com.mygdx.hadal.equip.ranged;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
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
import com.mygdx.hadal.strategies.hitbox.DamageStatic;

public class TeslaCoil extends RangedWeapon {

	private final static int clipSize = 2;
	private final static int ammoSize = 22;
	private final static float shootCd = 0.3f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.8f;
	private final static int reloadAmount = 0;
	private final static float recoil = 0.0f;
	private final static float projectileSpeed = 80.0f;
	private final static Vector2 projectileSize = new Vector2(50, 50);
	private final static float lifespan = 6.0f;
	
	private final static Sprite projSprite = Sprite.ORB_YELLOW;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;

	private final static float radius = 20.0f;
	private final static float pulseInterval = 0.5f;
	private final static float pulseDuration = 0.1f;
	private final static Vector2 pulseSize = new Vector2(75, 75);
	private final static float pulseDamage = 10.0f;
	private final static float pulseKnockback = 20.0f;
	
	//kep track of all coils laid so far
	private ArrayList<Hitbox> coilsLaid = new ArrayList<Hitbox>();

	public TeslaCoil(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);
		
		final Vector2 endLocation = new Vector2(this.mouseLocation);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			private Vector2 startLocation = new Vector2();
			private float distance;
			private boolean firstPlanted = false;
			private boolean planted = false;
			private boolean activated = false;
			private float controllerCount = pulseInterval;
			
			@Override
			public void create() {
				
				//keep track of the coil's travel distance
				this.startLocation.set(hbox.getPixelPosition());
				this.distance = startLocation.dst(endLocation);
			}
			
			@Override
			public void controller(float delta) {
				super.controller(delta);
				
				//planted coils stop and activates
				if (firstPlanted) {
					firstPlanted = false;
					planted = true;
					hbox.setLinearVelocity(0, 0);
					hbox.getBody().setType(BodyType.StaticBody);
				}
				
				//activated coils periodically check world for nearby coils
				if (planted) {
					
					controllerCount += delta;

					while (controllerCount >= pulseInterval) {
						controllerCount -= pulseInterval;
						
						activated = false;
						hbox.getWorld().QueryAABB(new QueryCallback() {

							@Override
							public boolean reportFixture(Fixture fixture) {
								if (fixture.getUserData() instanceof HitboxData) {
									if (coilsLaid.contains(((HitboxData) fixture.getUserData()).getHbox())) {
										if (!fixture.getUserData().equals(hbox.getHadalData())) {
											coilPairActivated(state, ((HitboxData) fixture.getUserData()).getHbox());
										}
									}
								}
								return true;
							}
						},
						hbox.getPosition().x - radius, hbox.getPosition().y - radius, 
						hbox.getPosition().x + radius, hbox.getPosition().y + radius);
					}
					return;
				}
				
				//After reaching the location clicked, the coil is makred as planted
				if (startLocation.dst(hbox.getPixelPosition()) >= distance) {
					firstPlanted = true;
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
			public void coilPairActivated(final PlayState state, final Hitbox hboxOther) {
				
				if (!activated) {
					activated = true;
					
					//draw a path of hitboxes between the 2 activated coils that damage enemies that pass through
					Vector2 pulsePosition = new Vector2(hbox.getPixelPosition());
					Vector2 pulsePath = hboxOther.getPixelPosition().sub(hbox.getPixelPosition());
					float dist = pulsePath.len();
					for (int i = 0; i < dist - pulseSize.x; i += pulseSize.x) {
						pulsePosition.add(pulsePath.nor().scl(pulseSize));
						
						
						Hitbox pulse = new RangedHitbox(state, pulsePosition, pulseSize, pulseDuration, new Vector2(), hbox.getFilter(), true, true, user, Sprite.NOTHING);
						pulse.addStrategy(new ControllerDefault(state, pulse, user.getBodyData()));
						pulse.addStrategy(new DamageStatic(state, pulse, user.getBodyData(), pulseDamage, pulseKnockback, DamageTypes.ELECTRICITY, DamageTypes.RANGED));
						pulse.addStrategy(new CreateParticles(state, pulse, user.getBodyData(), Particle.LASER_PULSE, 0.0f, 0.0f));
					}
				}
			}
		});
			
		coilsLaid.add(hbox);
	}
}
