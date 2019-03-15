package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.Equipable;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.HitboxSprite;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnDieExplodeStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxMouseStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactUnitDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.utils.HitboxFactory;

public class LaserGuidedRocket extends RangedWeapon {

	private final static String name = "Laser-Guided Rocket";
	private final static int clipSize = 1;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 2.0f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 10.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 0.0f;
	private final static float projectileSpeed = 2.0f;
	private final static int projectileWidth = 160;
	private final static int projectileHeight = 24;
	private final static float lifespan = 9.0f;
	private final static float gravity = 0;
	
	private final static int projDura = 1;
		
	private final static int explosionRadius = 300;
	private final static float explosionDamage = 90.0f;
	private final static float explosionKnockback = 25.0f;
	
	private final static Sprite projSprite = Sprite.TORPEDO;
	private final static Sprite weaponSprite = Sprite.MT_LASERROCKET;
	private final static Sprite eventSprite = Sprite.P_LASERROCKET;
	
	private static final float maxLinSpd = 150;
	private static final float maxLinAcc = 1000;
	private static final float maxAngSpd = 270;
	private static final float maxAngAcc = 180;
	
	private static final int boundingRad = 100;
	private static final int decelerationRadius = 0;
	

	private final static HitboxFactory onShoot = new HitboxFactory() {

		@Override
		public void makeHitbox(final Schmuck user, PlayState state, Equipable tool, Vector2 startVelocity, float x, float y, short filter) {
			
			Hitbox hbox = new HitboxSprite(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
					filter, true, true, user, projSprite);

			hbox.addStrategy(new HitboxOnContactUnitDieStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
			hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), tool, baseDamage, knockback, DamageTypes.RANGED));
			hbox.addStrategy(new HitboxOnDieExplodeStrategy(state, hbox, user.getBodyData(), tool, explosionRadius, explosionDamage, explosionKnockback, (short)0));
			hbox.addStrategy(new HitboxMouseStrategy(state, hbox, user.getBodyData(), maxLinSpd, maxLinAcc, maxAngSpd, maxAngAcc, boundingRad, decelerationRadius));
			hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
			
			new ParticleEntity(state, hbox, Particle.BUBBLE_TRAIL, 3.0f, 0.0f, true, particleSyncType.CREATESYNC);
		}
	};
	
	public LaserGuidedRocket(Schmuck user) {
		super(user, name, clipSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, onShoot, weaponSprite, eventSprite);
	}
}
