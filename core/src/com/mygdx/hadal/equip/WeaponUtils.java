package com.mygdx.hadal.equip;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.event.Event;
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
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.TiledObjectUtil;

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
		
		Hitbox hbox = new HitboxSprite(state, x, y, explosionRadius, explosionRadius, 0, 0.4f, 1, 0, new Vector2(0, 0),
				filter, true, false, user, boomSprite) {
			
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
	
	private static final float beeBaseDamage = 2.5f;
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
					
					batch.setProjectionMatrix(state.sprite.combined);

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
	
	/*
	 * NEVER USE THESE IDS AGAIN FOR ANY EVENT ANYWHERE
	 */
	private final static String genericHpDeleterID = "genericHpDeleterID";
	private final static String genericHpChangerID = "genericHpChangerID";
	private final static String genericHpParticleID = "genericHpParticleID";
	
	private final static String genericFuelDeleterID = "genericFuelDeleterID";
	private final static String genericFuelChangerID = "genericFuelChangerID";
	private final static String genericFuelParticleID = "genericFuelParticleID";
	
	private static Event hpChanger, fuelChanger;
	
	public static final int pickupSize = 64;
	
	public static void createPickup(PlayState state, int type, float power, int x, int y) {

		RectangleMapObject pickup = new RectangleMapObject();
		pickup.getRectangle().set(x, y, pickupSize, pickupSize);
		pickup.setName("Sensor");
		pickup.getProperties().put("align", 2);
		pickup.getProperties().put("sync", 3);
		pickup.getProperties().put("synced", true);
		pickup.getProperties().put("scale", 0.25f);
		pickup.getProperties().put("gravity", 1.0f);
		pickup.getProperties().put("player", true);
		pickup.getProperties().put("collision", true);
		pickup.getProperties().put("particle_amb", "EVENT_HOLO");

		if (type == 0) {
			pickup.getProperties().put("triggeringId", genericFuelChangerID);
			pickup.getProperties().put("sprite", "FUEL");
		}

		if (type == 1) {
			pickup.getProperties().put("triggeringId", genericHpChangerID);
			pickup.getProperties().put("sprite", "MEDPAK");
		}
		
		if (hpChanger == null && type == 1) {
			RectangleMapObject changer = new RectangleMapObject();
			changer.setName("Player");
			changer.getProperties().put("hp", power);
			changer.getProperties().put("triggeredId", genericHpChangerID);
			changer.getProperties().put("triggeringId", genericHpDeleterID);
			
			RectangleMapObject deleter = new RectangleMapObject();
			deleter.setName("EventDelete");
			deleter.getProperties().put("triggeredId", genericHpDeleterID);
			deleter.getProperties().put("triggeringId", genericHpParticleID);
			
			RectangleMapObject pickupParticle = new RectangleMapObject();
			pickupParticle.setName("Particle");
			pickupParticle.getProperties().put("duration", 2.0f);
			pickupParticle.getProperties().put("particle", "PICKUP_HEALTH");
			pickupParticle.getProperties().put("triggeredId", genericHpParticleID);
			
			hpChanger = TiledObjectUtil.parseSingleEventWithTriggers(state, changer);
			TiledObjectUtil.parseSingleEventWithTriggers(state, deleter);
			TiledObjectUtil.parseSingleEventWithTriggers(state, pickupParticle);
		}
		
		if (fuelChanger == null && type == 0) {
			RectangleMapObject changer = new RectangleMapObject();
			changer.setName("Player");
			changer.getProperties().put("fuel", power);
			changer.getProperties().put("triggeredId", genericFuelChangerID);
			changer.getProperties().put("triggeringId", genericFuelDeleterID);
			
			RectangleMapObject deleter = new RectangleMapObject();
			deleter.setName("EventDelete");
			deleter.getProperties().put("triggeredId", genericFuelDeleterID);
			deleter.getProperties().put("triggeringId", genericFuelParticleID);
			
			RectangleMapObject pickupParticle = new RectangleMapObject();
			pickupParticle.setName("Particle");
			pickupParticle.getProperties().put("duration", 2.0f);
			pickupParticle.getProperties().put("particle", "PICKUP_ENERGY");
			pickupParticle.getProperties().put("triggeredId", genericFuelParticleID);
			
			fuelChanger = TiledObjectUtil.parseSingleEventWithTriggers(state, changer);
			TiledObjectUtil.parseSingleEventWithTriggers(state, deleter);
			TiledObjectUtil.parseSingleEventWithTriggers(state, pickupParticle);
		}
		
		TiledObjectUtil.parseSingleEventWithTriggers(state, pickup);
	}
}
