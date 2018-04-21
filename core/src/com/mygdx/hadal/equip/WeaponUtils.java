package com.mygdx.hadal.equip;

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
import com.mygdx.hadal.schmucks.strategies.HitboxOnDieExplodeStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnHitDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxStaticStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class WeaponUtils {

	private static final float selfDamageReduction = 0.4f;
	
	public static Hitbox createExplosion(PlayState state, float x, float y, final Schmuck user, 
			int explosionRadius, final float explosionDamage, final float explosionKnockback, short filter) {
		
		Hitbox hbox = new HitboxAnimated(state, x, y, explosionRadius, explosionRadius, 0, 0.4f, 1, 0, new Vector2(0, 0),
				filter, true, user, "boom") {
			
			@Override
			public void controller(float delta) {
				this.body.setLinearVelocity(0, 0);
				super.controller(delta);
			}
		};
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxStaticStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageExplosionStrategy(state, hbox, user.getBodyData(), 
				explosionDamage, explosionKnockback, selfDamageReduction, DamageTypes.EXPLOSIVE, DamageTypes.DEFLECT));
		
		return hbox;
	}
	
	public static Hitbox createGrenade(PlayState state, float x, float y, final Schmuck user, final float baseDamage, final float knockback,
			int grenadeSize, float gravity, float lifespan, float restitution, int dura, Vector2 startVelocity, 
			final int explosionRadius, final float explosionDamage, final float explosionKnockback, short filter) {
		
		Hitbox hbox = new HitboxImage(state, x, y, grenadeSize, grenadeSize, gravity, lifespan, dura, restitution, startVelocity,
				filter, false,  user, "grenade");
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnHitDieStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new HitboxOnDieExplodeStrategy(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback, (short)0));
		
		return hbox;
	}
	
	public static Hitbox createTorpedo(PlayState state, float x, float y, final Schmuck user, final float baseDamage, final float knockback,
			int rocketWidth, int rocketHeight, float gravity, float lifespan, int dura, Vector2 startVelocity, 
			final int explosionRadius, final float explosionDamage, final float explosionKnockback, short filter) {
		
		Hitbox hbox = new HitboxImage(state, x, y, rocketWidth, rocketHeight, gravity, lifespan, dura, 0, startVelocity,
				filter, true, user, "torpedo");
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactDieStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new HitboxOnDieExplodeStrategy(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback, (short)0));
		
		new ParticleEntity(state, hbox, AssetList.BUBBLE_TRAIL.toString(), 3.0f, 0.0f, true);
		
		return hbox;
	}
	
}
