package com.mygdx.hadal.equip;

import static com.mygdx.hadal.utils.Constants.PPM;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxAnimated;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxImage;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageExplosionStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxHomingStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnDieExplodeStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStaticStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

/**
 * This util contains several shortcuts for hitbox-spawning effects for weapons or other items.
 * Includes create explosion, missiles, homing missiles, grenades and bees.
 * @author Zachary Tu
 *
 */
public class WeaponUtils {

	private static final float selfDamageReduction = 0.4f;
	
	public static Hitbox createExplosion(PlayState state, float x, float y, final Schmuck user, Equipable tool,
			int explosionRadius, final float explosionDamage, final float explosionKnockback, short filter) {
		
		Hitbox hbox = new HitboxAnimated(state, x, y, explosionRadius, explosionRadius, 0, 0.4f, 1, 0, new Vector2(0, 0),
				filter, true, false, user, "boom") {
			
			@Override
			public void controller(float delta) {
				this.body.setLinearVelocity(0, 0);
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
		
		Hitbox hbox = new HitboxImage(state, x, y, grenadeSize, grenadeSize, gravity, lifespan, dura, restitution, startVelocity,
				filter, false, procEffects, user, "grenade");
		
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
		
		Hitbox hbox = new HitboxImage(state, x, y, rocketWidth, rocketHeight, gravity, lifespan, dura, 0, startVelocity,
				filter, true, procEffects, user, "torpedo");
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactUnitDieStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new HitboxOnDieExplodeStrategy(state, hbox, user.getBodyData(), tool, explosionRadius, explosionDamage, explosionKnockback, (short)0));
		
		new ParticleEntity(state, hbox, AssetList.BUBBLE_TRAIL.toString(), 3.0f, 0.0f, true);
		
		return hbox;
	}
	
	private static final float torpedoBaseDamage = 5.0f;
	private static final float torpedoBaseKnockback = 3.0f;
	private static final float torpedoExplosionDamage = 20.0f;
	private static final float torpedoExplosionKnockback = 16.0f;
	private static final int torpedoExplosionRadius = 100;
	private static final int torpedoWidth = 75;
	private static final int torpedoHeight = 15;
	private static final float torpedoLifespan = 8.0f;
	
	
	public static Hitbox createHomingTorpedo(PlayState state, float x, float y, final Schmuck user, Equipable tool,
			int numTorp, int spread, Vector2 startVelocity, boolean procEffects, short filter) {
		
		for (int i = 0; i < numTorp; i++) {
			
			float newDegrees = (float) (startVelocity.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread)));
			
			Hitbox hbox = new HitboxImage(state, x, y, torpedoWidth, torpedoHeight, 0, torpedoLifespan, 1, 0, startVelocity.setAngle(newDegrees),
					filter, true, procEffects, user, "torpedo");
			
			hbox.addStrategy(new HitboxOnContactUnitDieStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, torpedoBaseDamage, torpedoBaseKnockback, DamageTypes.RANGED));
			hbox.addStrategy(new HitboxOnDieExplodeStrategy(state, hbox, user.getBodyData(), tool, torpedoExplosionRadius, torpedoExplosionDamage, torpedoExplosionKnockback, filter));
			hbox.addStrategy(new HitboxHomingStrategy(state, hbox, user.getBodyData(), filter));
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));

		}
		
		return null;
	}
	
	private static final float beeBaseDamage = 14;
	private static final float beeKnockback = 5.0f;
	private static final int beeWidth = 23;
	private static final int beeHeight = 21;
	private static final float beeLifespan = 4.0f;
	private static final float beeMaxLinSpd = 100;
	private static final float beeMaxLinAcc = 1000;
	private static final float beeMaxAngSpd = 180;
	private static final float beeMaxAngAcc = 90;
	private static final int beeBoundingRad = 500;
	private static final int beeDecelerationRadius = 0;
	private final static float beeHomeRadius = 10;

	public static Hitbox createBees(PlayState state, float x, float y, final Schmuck user, Equipable tool, int numBees, 
			int spread, Vector2 startVelocity, boolean procEffects, short filter) {
		
		for (int i = 0; i < numBees; i++) {
			
			float newDegrees = (float) (startVelocity.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
			
			Hitbox hbox = new HitboxAnimated(state, x, y, beeWidth, beeHeight, 0, beeLifespan, 1, 0, startVelocity.setAngle(newDegrees),
					filter, false, procEffects, user, "bee") {
				
				@Override
				public void render(SpriteBatch batch) {
				
					boolean flip = false;
					
					if (body.getAngle() < 0) {
						flip = true;
					}
					
					batch.setProjectionMatrix(state.sprite.combined);

					batch.draw((TextureRegion) projectileSprite.getKeyFrame(animationTime, true), 
							body.getPosition().x * PPM - width / 2, 
							(flip ? height : 0) + body.getPosition().y * PPM - height / 2, 
							width / 2, 
							(flip ? -1 : 1) * height / 2,
							width, (flip ? -1 : 1) * height, 1, 1, 
							(float) Math.toDegrees(body.getAngle()) - 90);

				}
			};
			
			hbox.addStrategy(new HitboxOnContactUnitDieStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, beeBaseDamage, beeKnockback, DamageTypes.RANGED));	
			hbox.addStrategy(new HitboxHomingStrategy(state, hbox, user.getBodyData(), beeMaxLinSpd, beeMaxLinAcc, 
					beeMaxAngSpd, beeMaxAngAcc, beeBoundingRad, beeDecelerationRadius, beeHomeRadius, filter));
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));

		}
		
		return null;
	}
	
}
