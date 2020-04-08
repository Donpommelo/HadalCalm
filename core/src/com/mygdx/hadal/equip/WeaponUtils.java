package com.mygdx.hadal.equip;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
import com.mygdx.hadal.event.Scrap;
import com.mygdx.hadal.event.Event.eventSyncTypes;
import com.mygdx.hadal.event.userdata.EventData;
import com.mygdx.hadal.event.utility.Sensor;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.userdata.BodyData;
import com.mygdx.hadal.schmucks.userdata.HadalData;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitDie;
import com.mygdx.hadal.strategies.hitbox.ContactUnitLoseDurability;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.CreateSound;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DamageStandardRepeatable;
import com.mygdx.hadal.strategies.hitbox.DieExplode;
import com.mygdx.hadal.strategies.hitbox.DieSound;
import com.mygdx.hadal.strategies.hitbox.DropThroughPassability;
import com.mygdx.hadal.strategies.hitbox.ExplosionDefault;
import com.mygdx.hadal.strategies.hitbox.HomingUnit;
import com.mygdx.hadal.strategies.hitbox.Spread;
import com.mygdx.hadal.strategies.hitbox.Static;
import com.mygdx.hadal.utils.Constants;
import com.mygdx.hadal.utils.Stats;
import com.mygdx.hadal.utils.b2d.BodyBuilder;
import com.mygdx.hadal.utils.b2d.FixtureBuilder;

/**
 * This util contains several shortcuts for hitbox-spawning effects for weapons or other items.
 * Includes create explosion, missiles, homing missiles, grenades and bees.
 * @author Zachary Tu
 *
 */
public class WeaponUtils {

	private static final float selfDamageReduction = 0.5f;
	private final static Sprite boomSprite = Sprite.BOOM;
	private final static Sprite grenadeSprite = Sprite.GRENADE;
	private final static Sprite torpedoSprite = Sprite.TORPEDO;
	private final static Sprite beeSprite = Sprite.BEE;

	public static Hitbox createExplosion(PlayState state, Vector2 startPos, float size, final Schmuck user, final float explosionDamage, final float explosionKnockback, short filter) {
		
		float newSize = size * (1 + user.getBodyData().getStat(Stats.EXPLOSION_SIZE));
		
		Hitbox hbox = new Hitbox(state, startPos, new Vector2(newSize, newSize), 0.4f, new Vector2(0, 0), filter, true, false, user, boomSprite);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new Static(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ExplosionDefault(state, hbox, user.getBodyData(), explosionDamage, explosionKnockback, selfDamageReduction, DamageTypes.EXPLOSIVE));
		
		return hbox;
	}
	
	public static Hitbox createGrenade(PlayState state, Vector2 startPos, Vector2 size, final Schmuck user, final float baseDamage, final float knockback, float lifespan, 
			Vector2 startVelocity, boolean procEffects, final int explosionRadius, final float explosionDamage, final float explosionKnockback, short filter) {
		
		Hitbox hbox = new RangedHitbox(state, startPos, size, lifespan, startVelocity, filter, false, procEffects, user, grenadeSprite);
		hbox.setGravity(2.5f);
		hbox.setRestitution(0.5f);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DropThroughPassability(state, hbox, user.getBodyData()));	
		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.EXPLOSIVE, DamageTypes.RANGED));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback, (short)0));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.BOMB, 0.4f));
		
		return hbox;
	}
	
	public static Hitbox createTorpedo(PlayState state, Vector2 startPos, Vector2 size, final Schmuck user, final float baseDamage, final float knockback, float lifespan,
			Vector2 startVelocity, boolean procEffects,	final int explosionRadius, final float explosionDamage, final float explosionKnockback, short filter) {
		
		Hitbox hbox = new RangedHitbox(state, startPos, size, lifespan, startVelocity, filter, true, procEffects, user, torpedoSprite);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.EXPLOSIVE, DamageTypes.RANGED));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback, (short)0));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.BUBBLE_TRAIL, 0.0f, 3.0f));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION1, 0.8f));
		
		return hbox;
	}
	
	private static final float torpedoBaseDamage = 3.0f;
	private static final float torpedoBaseKnockback = 3.0f;
	private static final float torpedoExplosionKnockback = 16.0f;
	private static final int torpedoExplosionRadius = 150;
	private static final int torpedoWidth = 50;
	private static final int torpedoHeight = 10;
	private static final float torpedoLifespan = 8.0f;
	private static final int torpedoSpread = 30;
	private static final float torpedoHoming = 100;
	
	public static Hitbox createHomingTorpedo(PlayState state, Vector2 startPos, final Schmuck user, float damage, int numTorp, int spread, Vector2 startVelocity, boolean procEffects, short filter) {
		
		for (int i = 0; i < numTorp; i++) {
			
			float newDegrees = (float) (startVelocity.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread)));
			
			Hitbox hbox = new RangedHitbox(state, startPos, new Vector2(torpedoWidth, torpedoHeight), torpedoLifespan, startVelocity.setAngle(newDegrees), filter, true, procEffects, user, torpedoSprite);

			hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
			hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
			hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
			hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
			hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), torpedoBaseDamage, torpedoBaseKnockback, DamageTypes.EXPLOSIVE, DamageTypes.RANGED));
			hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), torpedoExplosionRadius, damage, torpedoExplosionKnockback, filter));
			hbox.addStrategy(new HomingUnit(state, hbox, user.getBodyData(), torpedoHoming, filter));
			hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), torpedoSpread));
			hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION6, 0.5f));
		}
		
		return null;
	}
	
	private static final float beeBaseDamage = 5.0f;
	private static final float beeKnockback = 8.0f;
	private static final int beeWidth = 13;
	private static final int beeHeight = 12;
	private static final int beeDurability = 5;
	private static final float beeLifespan = 5.0f;
	private final static int beeSpread = 60;
	private final static float beeHoming = 110;
	
	public static Hitbox createBees(PlayState state, Vector2 startPos, final Schmuck user, int numBees, Vector2 startVelocity, boolean procEffects, short filter) {

		for (int i = 0; i < numBees; i++) {
			
			Hitbox hbox = new RangedHitbox(state, startPos, new Vector2(beeWidth, beeHeight), beeLifespan, startVelocity, filter, false, procEffects, user, beeSprite);
			
			hbox.setDurability(beeDurability);
			hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
			hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
			hbox.addStrategy(new DamageStandardRepeatable(state, hbox, user.getBodyData(), beeBaseDamage, beeKnockback, DamageTypes.BEES, DamageTypes.RANGED));	
			hbox.addStrategy(new HomingUnit(state, hbox, user.getBodyData(), beeHoming, filter));
			hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), beeSpread));
			hbox.addStrategy(new CreateSound(state, hbox, user.getBodyData(), SoundEffect.BEE_BUZZ, 0.25f));
		}
		
		return null;
	}
	
	private static final int spiritSize = 25;
	private static final float spiritHoming = 80;
	public static void releaseVengefulSpirits(PlayState state, Vector2 startPos, float spiritLifespan, float spiritDamage, float spiritKnockback, BodyData creator, short filter) {		
		
		
		Hitbox hbox = new RangedHitbox(state, startPos, new Vector2(spiritSize, spiritSize), spiritLifespan, new Vector2(), filter, true, true, creator.getSchmuck(), Sprite.NOTHING);
		
		hbox.addStrategy(new ControllerDefault(state, hbox, creator));
		hbox.addStrategy(new ContactUnitDie(state, hbox, creator));
		hbox.addStrategy(new DamageStandard(state, hbox, creator, spiritDamage, spiritKnockback, DamageTypes.MAGIC, DamageTypes.RANGED));
		hbox.addStrategy(new HomingUnit(state, hbox, creator, spiritHoming, filter));
		hbox.addStrategy(new CreateParticles(state, hbox, creator, Particle.SHADOW_PATH, 0.0f, 3.0f));
	}
	
	public static final int pickupSize = 64;
	public static void createPickup(PlayState state, Vector2 startPos, final pickupTypes type, final float power) {

		Event pickup = new Sensor(state, startPos, new Vector2(pickupSize, pickupSize), true, false, false, false, 1.0f, true) {
			
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
								new ParticleEntity(state, player.getSchmuck(), Particle.PICKUP_ENERGY, 0.0f, 5.0f, true, particleSyncType.CREATESYNC);
								event.queueDeletion();
								break;
							case FUEL:
								if (player.getCurrentFuel() < player.getStat(Stats.MAX_FUEL)) {
									player.fuelGain(power);
									new ParticleEntity(state, player.getSchmuck(), Particle.PICKUP_ENERGY, 0.0f, 5.0f, true, particleSyncType.CREATESYNC);
									event.queueDeletion();
								}
								break;
							case HEALTH:
								if (player.getCurrentHp() < player.getStat(Stats.MAX_HP)) {
									player.regainHp(power, player, true, DamageTypes.MEDPAK);
									new ParticleEntity(state, player.getSchmuck(), Particle.PICKUP_HEALTH, 0.0f, 5.0f, true, particleSyncType.CREATESYNC);
									event.queueDeletion();
								}
								break;
							default:
								break;
							}
						}
					}
				};
				this.body = BodyBuilder.createBox(world, startPos, size, gravity, 0, 0, false, false, Constants.BIT_SENSOR, (short)Constants.BIT_PLAYER, (short) 0, true, eventData);
				
				body.createFixture(FixtureBuilder.createFixtureDef(new Vector2(), size, false, 0, 0, 0.0f, 1.0f, Constants.BIT_SENSOR, Constants.BIT_WALL, (short) 0));
			}
		};
		
		new ParticleEntity(state, pickup, Particle.EVENT_HOLO, 0.0f, 0.0f, true, particleSyncType.CREATESYNC);
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
	
	public static void spawnScrap(PlayState state, int amount, Vector2 startPos, boolean statCheck) {
		
		int modifiedAmount = 0;
		
		if (statCheck) {
			if (state.getPlayer().getPlayerData().getStat(Stats.EXTRA_SCRAP) * amount < 1.0f && state.getPlayer().getPlayerData().getStat(Stats.EXTRA_SCRAP) > 0) {
				modifiedAmount = amount + 1;
			} else {
				modifiedAmount = (int) (amount * (1 + state.getPlayer().getPlayerData().getStat(Stats.EXTRA_SCRAP)));
			}
		} else {
			modifiedAmount = amount;
		}
		
		for (int i = 0; i < modifiedAmount; i++) {
			new Scrap(state, startPos);
		}
	}
	
	public enum pickupTypes {
		HEALTH,
		FUEL,
		AMMO
	}
}
