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
import com.mygdx.hadal.schmucks.strategies.DamageStandard;
import com.mygdx.hadal.schmucks.strategies.ControllerDefault;
import com.mygdx.hadal.schmucks.strategies.DieExplode;
import com.mygdx.hadal.schmucks.strategies.HomingMouse;
import com.mygdx.hadal.schmucks.strategies.ContactUnitDie;
import com.mygdx.hadal.schmucks.strategies.ContactWallDie;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;

public class LaserGuidedRocket extends RangedWeapon {

	private final static int clipSize = 1;
	private final static int ammoSize = 12;
	private final static float shootCd = 0.0f;
	private final static float shootDelay = 0.0f;
	private final static float reloadTime = 1.5f;
	private final static int reloadAmount = 1;
	private final static float baseDamage = 40.0f;
	private final static float recoil = 0.0f;
	private final static float knockback = 0.0f;
	private final static float projectileSpeed = 5.0f;
	private final static Vector2 projectileSize = new Vector2(70, 10);
	private final static float lifespan = 7.0f;
		
	private final static int explosionRadius = 150;
	private final static float explosionDamage = 50.0f;
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
	
	public LaserGuidedRocket(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true, weaponSprite, eventSprite, projectileSize.x);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, filter, true, true, user, projSprite);

		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.RANGED));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback, (short)0));
		hbox.addStrategy(new HomingMouse(state, hbox, user.getBodyData(), maxLinSpd, maxLinAcc, maxAngSpd, maxAngAcc, boundingRad, decelerationRadius));
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		
		new ParticleEntity(state, hbox, Particle.BUBBLE_TRAIL, 3.0f, 0.0f, true, particleSyncType.TICKSYNC);
	}
}
