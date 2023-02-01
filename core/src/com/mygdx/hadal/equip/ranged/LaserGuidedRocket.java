package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.hitbox.AdjustAngle;
import com.mygdx.hadal.strategies.hitbox.ContactUnitDie;
import com.mygdx.hadal.strategies.hitbox.ContactWallDie;
import com.mygdx.hadal.strategies.hitbox.ControllerDefault;
import com.mygdx.hadal.strategies.hitbox.CreateParticles;
import com.mygdx.hadal.strategies.hitbox.DamageStandard;
import com.mygdx.hadal.strategies.hitbox.DieExplode;
import com.mygdx.hadal.strategies.hitbox.DieSound;
import com.mygdx.hadal.strategies.hitbox.FlashShaderNearDeath;
import com.mygdx.hadal.strategies.hitbox.HomingMouse;

public class LaserGuidedRocket extends RangedWeapon {

	private static final int clipSize = 1;
	private static final int ammoSize = 24;
	private static final float shootCd = 0.5f;
	private static final float reloadTime = 1.0f;
	private static final int reloadAmount = 1;
	private static final float baseDamage = 20.0f;
	private static final float recoil = 8.0f;
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

	public LaserGuidedRocket(Player user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}

	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.LASER_GUIDED_ROCKET.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createLaserGuidedRocket(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.ROLLING_ROCKET.playSourced(state, startPosition, 0.4f);
		user.recoil(startVelocity, recoil);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxFilter(),
				true, true, user, projSprite);

		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback,
				DamageSource.LASER_GUIDED_ROCKET, DamageTag.EXPLOSIVE, DamageTag.RANGED));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback,
				(short) 0, false, DamageSource.LASER_GUIDED_ROCKET));
		hbox.addStrategy(new HomingMouse(state, hbox, user.getBodyData(), homePower));
		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new FlashShaderNearDeath(state, hbox, user.getBodyData(), 1.0f));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.BUBBLE_TRAIL, 0.0f, 1.0f)
			.setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION9, 0.6f).setSynced(false));

		return hbox;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) baseDamage),
				String.valueOf((int) explosionDamage),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime),
				String.valueOf(shootCd)};
	}
}
