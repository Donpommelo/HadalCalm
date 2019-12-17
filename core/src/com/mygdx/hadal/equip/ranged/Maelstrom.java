package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.ParticleEntity.particleSyncType;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.strategies.HitboxDamageStandardStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxDefaultStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactChainStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallDieStrategy;
import com.mygdx.hadal.schmucks.strategies.HitboxOnContactWallParticles;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class Maelstrom extends RangedWeapon {

	private final static String name = "Maelstrom";
	private final static int clipSize = 8;
	private final static int ammoSize = 50;
	private final static float shootCd = 0.01f;
	private final static float shootDelay = 0;
	private final static float reloadTime = 1.1f;
	private final static int reloadAmount = 0;

	private final static float recoil = 0.0f;
	private final static float baseDamage = 4.0f;
	private final static float knockback = 8.0f;
	private final static float projectileSpeedStart = 50.0f;
	private final static int projectileWidth = 60;
	private final static int projectileHeight = 60;
	private final static float lifespan = 1.5f;
	private final static float gravity = 0;
	
	private final static int projDura = 5;
	
	private final static Sprite weaponSprite = Sprite.MT_CHAINLIGHTNING;
	private final static Sprite eventSprite = Sprite.P_CHAINLIGHTNING;
	
	public Maelstrom(Schmuck user) {
		super(user, name, clipSize, ammoSize, reloadTime, recoil, projectileSpeedStart, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileWidth);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startVelocity, float x, float y, short filter) {
		Hitbox hbox = new RangedHitbox(state, x, y, projectileWidth, projectileHeight, gravity, lifespan, projDura, 0, startVelocity,
				filter, true, true, user);
		
		hbox.addStrategy(new HitboxDefaultStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactWallParticles(state, hbox, user.getBodyData(), Particle.SPARK_TRAIL));
		hbox.addStrategy(new HitboxOnContactWallDieStrategy(state, hbox, user.getBodyData()));
		hbox.addStrategy(new HitboxOnContactChainStrategy(state, hbox, user.getBodyData(), projDura, filter));
		hbox.addStrategy(new HitboxDamageStandardStrategy(state, hbox, user.getBodyData(), this, baseDamage, knockback, DamageTypes.RANGED));
		
		new ParticleEntity(state, hbox, Particle.LIGHTNING, 3.0f, 0.0f, true, particleSyncType.CREATESYNC);
	}
}
