package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.constants.SyncType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.hitbox.*;

public class TorpedoLauncher extends RangedWeapon {

	private static final int clipSize = 4;
	private static final int ammoSize = 24;
	private static final float shootCd = 0.25f;
	private static final float shootDelay = 0.0f;
	private static final float reloadTime = 0.7f;
	private static final int reloadAmount = 1;
	private static final float baseDamage = 15.0f;
	private static final float recoil = 2.5f;
	private static final float knockback = 0.0f;
	private static final float projectileSpeed = 48.0f;
	private static final Vector2 projectileSize = new Vector2(60, 18);
	private static final float lifespan = 1.5f;
		
	private static final int explosionRadius = 150;
	private static final float explosionDamage = 40.0f;
	private static final float explosionKnockback = 25.0f;

	private static final Sprite projSprite = Sprite.TORPEDO;
	private static final Sprite weaponSprite = Sprite.MT_TORPEDO;
	private static final Sprite eventSprite = Sprite.P_TORPEDO;
	
	public TorpedoLauncher(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}
	
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.TORPEDO.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createTorpedo(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.ROCKET.playSourced(state, startPosition, 0.5f);
		user.recoil(startVelocity, recoil);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				true, true, user, projSprite);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback,
				DamageSource.TORPEDO_LAUNCHER, DamageTag.EXPLOSIVE, DamageTag.RANGED));
		hbox.addStrategy(new DieExplode(state, hbox, user.getBodyData(), explosionRadius, explosionDamage, explosionKnockback,
				(short) 0, false, DamageSource.TORPEDO_LAUNCHER));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.BUBBLE_TRAIL, 0.0f, 1.0f)
				.setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.EXPLOSION1, 0.5f).setSynced(false));
		hbox.addStrategy(new FlashShaderNearDeath(state, hbox, user.getBodyData(), 1.0f, false));

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
