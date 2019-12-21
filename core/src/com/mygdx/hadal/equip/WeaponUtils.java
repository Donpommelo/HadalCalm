package com.mygdx.hadal.equip;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.Event.eventSyncTypes;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.utility.Sensor;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageExplosionStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxHomingStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnDieExplodeStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitLoseDuraStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStaticStrategy;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

/**
 * This util contains several shortcuts for hitbox-spawning effects for weapons or other items.
 * Includes create explosion, missiles, homing missiles, grenades and bees.
 * @author Zachary Tu
 *
 */
public class WeaponUtils {

	private static final float selfDamageReduction = 0.75f;
	private final static Sprite boomSprite = Sprite.BOOM;
	private final static Sprite grenadeSprite = Sprite.GRENADE;
	private final static Sprite torpedoSprite = Sprite.TORPEDO;
	private final static Sprite beeSprite = Sprite.BEE;

	public static Hitbox createExplosion(PlayState state, float x, float y, final Schmuck user, Equipable tool,
			int explosionRadius, final float explosionDamage, final float explosionKnockback, short filter) {
		
		Hitbox hbox = new HitboxSprite(state, x, y, explosionRadius, explosionRadius, 0, 0.4f, 1, 0, new Vector2(0, 0), filter, true, false, user, boomSprite) {
			
			@Override
			public void controller(float delta) {
				setLinearVelocity(0, 0);
				super.controller(delta);
			}
		};
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxStaticStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageExplosionStrategy(state, hbox, user.getBodyData(), tool,
				explosionDamage, explosionKnockback, selfDamageReduction, DamageTypes.EXPLOSIVE, DamageTypes.DEFLECT));
		
		return hbox;
	}
	
	public static Hitbox createGrenade(PlayState state, float x, float y, final Schmuck user, Equipable tool,
			final float baseDamage, final float knockback, int grenadeSize, float gravity, float lifespan, float restitution,
			int dura, Vector2 startVelocity, boolean procEffects, 
			final int explosionRadius, final float explosionDamage, final float explosionKnockback, short filter) {
		
		Hitbox hbox = new HitboxSprite(state, x, y, grenadeSize, grenadeSize, gravity, lifespan, dura, restitution, startVelocity,
				filter, false, procEffects, user, grenadeSprite);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactUnitDieStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new HitboxOnDieExplodeStrategy(state, hbox, user.getBodyData(), tool, explosionRadius, explosionDamage, explosionKnockback, (short)0));
		
		return hbox;
	}
	
	public static Hitbox createTorpedo(PlayState state, float x, float y, final Schmuck user, Equipable tool,
			final float baseDamage, final float knockback, int rocketWidth, int rocketHeight, float gravity, float lifespan,
			int dura, Vector2 startVelocity, boolean procEffects,
			final int explosionRadius, final float explosionDamage, final float explosionKnockback, short filter) {
		
		Hitbox hbox = new HitboxSprite(state, x, y, rocketWidth, rocketHeight, gravity, lifespan, dura, 0, startVelocity,
				filter, true, procEffects, user, torpedoSprite);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactUnitDieStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new HitboxOnDieExplodeStrategy(state, hbox, user.getBodyData(), tool, explosionRadius, explosionDamage, explosionKnockback, (short)0));
		
		new ParticleEntity(state, hbox, Particle.BUBBLE_TRAIL, 3.0f, 0.0f, true, particleSyncType.CREATESYNC);
		
		return hbox;
	}
	
	private static final float torpedoBaseDamage = 3.0f;
	private static final float torpedoBaseKnockback = 3.0f;
	private static final float torpedoExplosionDamage = 7.5f;
	private static final float torpedoExplosionKnockback = 16.0f;
	private static final int torpedoExplosionRadius = 150;
	private static final int torpedoWidth = 75;
	private static final int torpedoHeight = 15;
	private static final float torpedoLifespan = 8.0f;
	
	public static Hitbox createHomingTorpedo(PlayState state, float x, float y, final Schmuck user, Equipable tool,
			int numTorp, int spread, Vector2 startVelocity, boolean procEffects, short filter) {
		
		for (int i = 0; i < numTorp; i++) {
			
			float newDegrees = (float) (startVelocity.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread)));
			
			Hitbox hbox = new HitboxSprite(state, x, y, torpedoWidth, torpedoHeight, 0, torpedoLifespan, 1, 0, startVelocity.setAngle(newDegrees),
					filter, true, procEffects, user, torpedoSprite);
			
			hbox.addStrategy(new HitboxOnContactUnitDieStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, torpedoBaseDamage, torpedoBaseKnockback, DamageTypes.RANGED));
			hbox.addStrategy(new HitboxOnDieExplodeStrategy(state, hbox, user.getBodyData(), tool, torpedoExplosionRadius, torpedoExplosionDamage, torpedoExplosionKnockback, filter));
			hbox.addStrategy(new HitboxHomingStrategy(state, hbox, user.getBodyData(), filter));
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		}
		
		return null;
	}
	
	private static final float beeBaseDamage = 3.5f;
	private static final float beeKnockback = 7.5f;
	private static final int beeWidth = 23;
	private static final int beeHeight = 21;
	private static final int beeDurability = 3;
	private static final float beeLifespan = 4.0f;
	private static final float beeMaxLinSpd = 100;
	private static final float beeMaxLinAcc = 1000;
	private static final float beeMaxAngSpd = 1080;
	private static final float beeMaxAngAcc = 1080;
	private static final int beeBoundingRad = 100;
	private static final int beeDecelerationRadius = 0;
	private final static float beeHomeRadius = 1000;

	public static Hitbox createBees(PlayState state, float x, float y, final Schmuck user, Equipable tool, int numBees, 
			int spread, Vector2 startVelocity, boolean procEffects, short filter) {
		
		for (int i = 0; i < numBees; i++) {
			
			float newDegrees = (float) (startVelocity.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
			
			Hitbox hbox = new HitboxSprite(state, x, y, beeWidth, beeHeight, 0, beeLifespan, beeDurability, 0, startVelocity.setAngle(newDegrees),
					filter, false, procEffects, user, beeSprite) {
				
				@Override
				public void render(SpriteBatch batch) {
				
					boolean flip = false;
					
					if (getOrientation() < 0) {
						flip = true;
					}
					
					batch.draw((TextureRegion) projectileSprite.getKeyFrame(animationTime, true), 
							getPosition().x * PPM - width / 2, 
							(flip ? height : 0) + getPosition().y * PPM - height / 2, 
							width / 2, 
							(flip ? -1 : 1) * height / 2,
							width, (flip ? -1 : 1) * height, 1, 1, 
							(float) Math.toDegrees(getOrientation()) - 90);

				}
			};
			
			hbox.addStrategy(new HitboxOnContactUnitLoseDuraStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, beeBaseDamage, beeKnockback, DamageTypes.RANGED));	
			hbox.addStrategy(new HitboxHomingStrategy(state, hbox, user.getBodyData(), beeMaxLinSpd, beeMaxLinAcc, 
					beeMaxAngSpd, beeMaxAngAcc, beeBoundingRad, beeDecelerationRadius, beeHomeRadius, filter));
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		}
		
		return null;
	}
	
	private static final int spiritSize = 25;
	public static void releaseVengefulSpirits(PlayState state, float spiritLifespan, float spiritDamage, float spiritKnockback, Vector2 pos, BodyData creator, short filter) {		
		Hitbox hbox = new Hitbox(state, (int)pos.x, (int)pos.y, (int)spiritSize, (int)spiritSize, 0, spiritLifespan, 1, 0, 
				new Vector2(), filter, true, true, creator.getSchmuck());
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, creator));
		hbox.addStrategy(new HitboxOnContactUnitDieStrategy(state, hbox, creator));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, creator, null, spiritDamage, spiritKnockback));
		hbox.addStrategy(new HitboxHomingStrategy(state, hbox, creator, filter));
		new ParticleEntity(state, hbox, Particle.SHADOW_PATH, spiritLifespan, 0.0f, true, particleSyncType.CREATESYNC);
	}
	
	public static final int pickupSize = 64;
	public static void createPickup(PlayState state, final pickupTypes type, final float power, int x, int y) {

		Event pickup = new Sensor(state, pickupSize, pickupSize, x, y, true, false, false, false, 1.0f, true) {
			
			@Override
			public void create() {
				
				this.eventData = new EventData(this) {
					
					@Override
					public void onTouch(HadalData fixB) {
						super.onTouch(fixB);
						
						if (isAlive() && fixB instanceof PlayerBodyData) {
							
							
							PlayerBodyData player = ((PlayerBodyData)fixB);
							switch(type) {
							case AMMO:
								player.getCurrentTool().gainAmmo(power);
								new ParticleEntity(state, player.getSchmuck(), Particle.PICKUP_ENERGY, 0.0f, 2.0f, true, particleSyncType.TICKSYNC);
								event.queueDeletion();
								break;
							case FUEL:
								if (player.getCurrentFuel() < player.getMaxFuel()) {
									player.fuelGain(power);
									new ParticleEntity(state, player.getSchmuck(), Particle.PICKUP_ENERGY, 0.0f, 2.0f, true, particleSyncType.TICKSYNC);
									event.queueDeletion();
								}
								break;
							case HEALTH:
								if (player.getCurrentHp() < player.getMaxHp()) {
									player.regainHp(power, player, true, DamageTypes.MEDPAK);
									new ParticleEntity(state, player.getSchmuck(), Particle.PICKUP_HEALTH, 0.0f, 2.0f, true, particleSyncType.TICKSYNC);
									event.queueDeletion();
								}
								break;
							default:
								break;
							}
						}
					}
				};
				this.body = BodyBuilder.createBox(world, startX, startY, width, height, gravity, 0, 0, false, false, Constants.BIT_SENSOR, 
						(short)Constants.BIT_PLAYER, (short) 0, true, eventData);
				
				body.createFixture(FixtureBuilder.createFixtureDef(width - 2, height - 2, 
						new Vector2(1 / 4 / PPM,  1 / 4 / PPM), false, 0, 0, 0.0f, 1.0f,
					Constants.BIT_SENSOR, Constants.BIT_WALL, (short) 0));
			}
		};
		
		new ParticleEntity(state, pickup, Particle.EVENT_HOLO, 0.0f, 0.0f, true, particleSyncType.TICKSYNC);
		pickup.setScaleAlign("CENTER_BOTTOM");
		pickup.setSyncType(eventSyncTypes.ILLUSION);
		pickup.setSynced(true);
		pickup.setScale(0.25f);
		
		switch(type) {
		case AMMO:
			pickup.setEventSprite(Sprite.FUEL);
			break;
		case FUEL:
			pickup.setEventSprite(Sprite.FUEL);
			break;
		case HEALTH:
			pickup.setEventSprite(Sprite.MEDPAK);
			break;
		default:
			break;
		}
	}
	
	public enum pickupTypes {
		HEALTH,
		FUEL,
		AMMO
	}
}
