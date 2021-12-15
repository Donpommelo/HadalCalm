package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.RangedHitbox;
import com.mygdx.hadal.schmucks.bodies.hitboxes.SyncedAttack;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DieExplode;
import com.mygdx.hadal.strategies.hitbox.DieSound;
import com.mygdx.hadal.strategies.hitbox.FlashNearDeath;
import com.mygdx.hadal.strategies.hitbox.HomingMouse;

public class LaserGuidedRocket extends RangedWeapon {

	private static final int clipSize = 1;
	private static final int ammoSize = 24;
	private static final float shootCd = 0.5f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 1.0f;
	private static final int reloadAmount = 1;
	private static final float baseDamage = 20.0f;
	private static final float recoil = 0.0f;
	private static final float knockback = 0.0f;
	private static final float projectileSpeed = 7.5f;
	private static final Vector2 projectileSize = new Vector2(80, 30);
	private static final float lifespan = 6.0f;

	private static final int explosionRadius = 200;
	private static final float explosionDamage = 40.0f;
	private static final float explosionKnockback = 35.0f;

	private static final float homePower = 250.0f;

	private static final Sprite projSprite = Sprite.MISSILE_C;
	private static final Sprite weaponSprite = Sprite.MT_LASERROCKET;
	private static final Sprite eventSprite = Sprite.P_LASERROCKET;

	public LaserGuidedRocket(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, recoil, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}

	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.LASER_GUIDED_ROCKET.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createLaserGuidedRocket(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.ROLLING_ROCKET.playSourced(state, startPosition, 0.4f);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				true, true, user, projSprite);

		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageTypes.EXPLOSIVE, DamageTypes.RANGED));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback,
				(short) 0, false));
		hbox.addStrategy(new HomingMouse(state, hbox, user.getBodyData(), homePower));
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new FlashNearDeath(state, hbox, user.getBodyData(), 1.0f, false));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.BUBBLE_TRAIL, 0.0f, 1.0f)
			.setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION9, 0.6f).setSynced(false));

		return hbox;
	}
}
