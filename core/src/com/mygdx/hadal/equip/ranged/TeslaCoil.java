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
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStrategy;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

import static com.mygdx.hadal.utils.Constants.PPM;

public class TeslaCoil extends RangedWeapon {

	private final static String name = "Tesla Coil";
	private final static int clipSize = 2;
	private final static int ammoSize = 22;
	private final static float shootCd = 0.3f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.8f;
	private final static int reloadAmount = 0;
	private final static float recoil = 0.0f;
	private final static float projectileSpeed = 80.0f;
	private final static int projectileWidth = 75;
	private final static int projectileHeight = 75;
	private final static float lifespan = 4.0f;
	
	private final static Sprite projSprite = Sprite.ORB_YELLOW;
	private final static Sprite weaponSprite = Sprite.MT_DEFAULT;
	private final static Sprite eventSprite = Sprite.P_DEFAULT;

	private final static float radius = 20.0f;
	private final static float pulseInterval = 1.0f;
	private final static float pulseDuration = 0.1f;
	private final static int pulseSize = 40;
	private final static float pulseDamage = 6.0f;
	
	private ArrayList<Hitbox> coilsLaid = new ArrayList<Hitbox>();

	public TeslaCoil(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileWidth);
	}
	
	@Override
	public void fire(PlayState state, final Schmuck user, final Vector2 startVelocity, final float x, final float y, short filter) {
		Hitbox hbox = new HitboxSprite(state, x, y, projectileWidth, projectileHeight, lifespan, startVelocity, filter, true, true, user, projSprite);
		
		final Vector2 endLocation = new Vector2(this.x, this.y);
		final TeslaCoil tool = this;
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		
		hbox.addStrategy(new HitboxStrategy(state, hbox, user.getBodyData()) {
			
			private Vector2 startLocation = new Vector2();
			private float distance;
			private boolean firstPlanted = false;
			private boolean planted = false;
			private boolean activated = false;
			private float controllerCount = pulseInterval;
			
			@Override
			public void create() {
				this.startLocation.set(hbox.getPosition());
				this.distance = startLocation.dst(endLocation);
			}
			
			@Override
			public void controller(float delta) {
				super.controller(delta);
				
				if (firstPlanted) {
					firstPlanted = false;
					planted = true;
					hbox.setLinearVelocity(0, 0);
					hbox.getBody().setType(BodyType.StaticBody);
				}
				
				if (planted) {
					
					controllerCount+=delta;

					if (controllerCount >= pulseInterval) {
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
				
				if (startLocation.dst(hbox.getPosition()) >= distance) {
					firstPlanted = true;
				}
			}
			
			@Override
			public void onHit(HadalData fixB) {
				
				if (planted) {
					return;
				}
				
				if (fixB == null) {
					firstPlanted = true;
				} else if (fixB.getType().equals(UserDataTypes.WALL)){
					firstPlanted = true;
				}
			}
			
			@Override
			public void die() {
				coilsLaid.remove(hbox);
			}
			
			public void coilPairActivated(final PlayState state, final Hitbox hboxOther) {
				
				if (!activated) {
					activated = true;
					
					Vector2 pulsePosition = new Vector2(hbox.getPosition().scl(PPM));
					Vector2 pulsePath = hboxOther.getPosition().sub(hbox.getPosition()).scl(PPM);
					float dist = pulsePath.len();
					for (int i = 0; i < dist - pulseSize; i += pulseSize) {
						pulsePosition.add(pulsePath.nor().scl(pulseSize));
						
						
						Hitbox pulse = new RangedHitbox(state, pulsePosition.x, pulsePosition.y, pulseSize, pulseSize, pulseDuration, new Vector2(), hbox.getFilter(), true, true, user);
						pulse.addStrategy(new HitboxDefaultStrategy(state, pulse, user.getBodyData(), false));
						pulse.addStrategy(new HitboxDamageStandardStrategy(state, pulse, user.getBodyData(), tool, pulseDamage, 0, DamageTypes.RANGED));
						new ParticleEntity(state, pulse, Particle.LASER_PULSE, 0.0f, 0.0f, true, particleSyncType.TICKSYNC);
					}
				}
			}
		});
			
		coilsLaid.add(hbox);
	}
}
