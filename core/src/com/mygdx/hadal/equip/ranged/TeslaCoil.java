package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.constants.UserDataType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.HitboxData;
import com.mygdx.hadal.states.ClientState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.HitboxStrategy;
import com.mygdx.hadal.strategies.hitbox.*;
import com.mygdx.hadal.constants.Constants;

import static com.mygdx.hadal.constants.Constants.PPM;

public class TeslaCoil extends RangedWeapon {

	private static final int clipSize = 3;
	private static final int ammoSize = 27;
	private static final float shootCd = 0.3f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 1.8f;
	private static final int reloadAmount = 0;
	private static final float projectileSpeed = 100.0f;
	private static final Vector2 projectileSize = new Vector2(45, 45);
	private static final float lifespan = 4.5f;
	private static final float flashLifespan = 1.0f;

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
	private final Array<Hitbox> coilsLaid = new Array<>();

	public TeslaCoil(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SoundEffect.LAUNCHER.playUniversal(state, startPosition, 0.25f, false);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true,
				true, user, projSprite);
		
		final Vector2 endLocation = new Vector2(this.mouseLocation);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), flashLifespan));
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
								if (coilsLaid.contains(((HitboxData) fixture.getUserData()).getHbox(), false)) {
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
				} else if (UserDataType.WALL.equals(fixB.getType())){
					firstPlanted = true;
				}
			}
			
			@Override
			public void die() {
				//remove dead coils from list
				coilsLaid.removeValue(hbox, false);
			}
			
			/**
			 * This activates when a coil performs its periodic check of nearby coils and finds one
			 * @param state: playstate
			 * @param hboxOther: the other coil to connect to
			 */
			public void coilPairActivated(PlayState state, Hitbox hboxOther) {
				if (!activated) {
					activated = true;
					Vector2 otherPosition = new Vector2(hboxOther.getPixelPosition());
					SyncedAttack.TESLA_ACTIVATION.initiateSyncedAttackSingle(state, user, hbox.getPixelPosition(),
							startVelocity, otherPosition.x, otherPosition.y);
				}
			}
		});
			
		coilsLaid.add(hbox);
	}

	public static Hitbox createTeslaActivation(PlayState state, Schmuck user, Vector2 startPosition, float[] extraFields) {
		SoundEffect.ZAP.playSourced(state, startPosition, 0.4f);

		//draw a path of hitboxes between the 2 activated coils that damage enemies that pass through
		Vector2 pulsePosition = new Vector2(startPosition);
		Vector2 otherPosition = new Vector2();
		Vector2 pulsePath = new Vector2();
		if (extraFields.length >= 2) {
			otherPosition.set(extraFields[0], extraFields[1]);
			pulsePath.set(otherPosition).sub(pulsePosition);
		}

		float dist = pulsePath.len();
		for (int i = 0; i < dist - pulseSize.x; i += pulseSize.x) {
			pulsePosition.add(pulsePath.nor().scl(pulseSize));

			Hitbox pulse = new RangedHitbox(state, pulsePosition, pulseSize, pulseDuration, new Vector2(), user.getHitboxfilter(),
					true, true, user, Sprite.NOTHING);
			pulse.setPassability((short) (Constants.BIT_PLAYER | Constants.BIT_ENEMY));
			pulse.setSyncDefault(false);
			pulse.setEffectsHit(false);

			pulse.addStrategy(new ControllerDefault(state, pulse, user.getBodyData()));
			pulse.addStrategy(new CreateParticles(state, pulse, user.getBodyData(), Particle.LASER_PULSE, 0.0f, 0.1f)
					.setParticleSize(50).setSyncType(SyncType.NOSYNC));

			if (!state.isServer()) {
				((ClientState) state).addEntity(pulse.getEntityID(), pulse, false, ClientState.ObjectLayer.HBOX);
			}
		}

		Hitbox hboxDamage = new RangedHitbox(state, startPosition, new Vector2(otherPosition.dst(startPosition), pulseSize.y),
				pulseDuration, new Vector2(), user.getHitboxfilter(), true, true, user, Sprite.NOTHING) {

			private final Vector2 newPosition = new Vector2();
			@Override
			public void create() {
				super.create();

				//this makes the laser hbox's lifespan unmodifiable
				setLifeSpan(pulseDuration);

				//Rotate hitbox to match angle of fire.
				newPosition.set(otherPosition).sub(startPosition);
				float newAngle = MathUtils.atan2(newPosition.y , newPosition.x);

				newPosition.set(startPosition).add(otherPosition).scl(0.5f);
				setTransform(newPosition.x / PPM, newPosition.y / PPM, newAngle);
			}
		};
		hboxDamage.setEffectsVisual(false);

		hboxDamage.addStrategy(new ControllerDefault(state, hboxDamage, user.getBodyData()));
		hboxDamage.addStrategy(new DamageStandard(state, hboxDamage, user.getBodyData(), pulseDamage, pulseKnockback,
				DamageSource.TESLA_COIL, DamageTag.ENERGY, DamageTag.RANGED).setStaticKnockback(true));
		hboxDamage.addStrategy(new Static(state, hboxDamage, user.getBodyData()));

		return hboxDamage;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) pulseDamage),
				String.valueOf(lifespan),
				String.valueOf((int) pulseInterval),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime),
				String.valueOf(shootCd)};
	}
}
