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
import com.mygdx.hadal.schmucks.strategies.HitboxOnHitDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStaticStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

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
		hbox.addStrategy(new HitboxOnHitDieStrategy(state, hbox, user.getBodyData()));
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
		hbox.addStrategy(new HitboxOnContactDieStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new HitboxOnDieExplodeStrategy(state, hbox, user.getBodyData(), tool, explosionRadius, explosionDamage, explosionKnockback, (short)0));
		
		new ParticleEntity(state, hbox, AssetList.BUBBLE_TRAIL.toString(), 3.0f, 0.0f, true);
		
		return hbox;
	}
	
	public static Hitbox createBees(PlayState state, float x, float y, final Schmuck user, Equipable tool,
			final float baseDamage, final float knockback, int projectileWidth, int projectileHeight, float lifespan,
			int numBees, int spread, Vector2 startVelocity, boolean procEffects,
			float maxLinSpd, float maxLinAcc, float maxAngSpd, float maxAngAcc, int boundingRad, int decelerationRadius, float radius, short filter) {
		
		for (int i = 0; i < numBees; i++) {
			
			float newDegrees = (float) (startVelocity.angle() + (ThreadLocalRandom.current().nextInt(-spread, spread + 1)));
			
			Hitbox hbox = new HitboxAnimated(state, x, y, projectileWidth, projectileHeight, 0, lifespan, 1, 0, startVelocity.setAngle(newDegrees),
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
			
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxOnHitDieStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));	
			hbox.addStrategy(new HitboxHomingStrategy(state, hbox, user.getBodyData(), maxLinSpd, maxLinAcc, 
					maxAngSpd, maxAngAcc, boundingRad, decelerationRadius, radius, filter));	
		}
		
		return null;
	}
	
}
