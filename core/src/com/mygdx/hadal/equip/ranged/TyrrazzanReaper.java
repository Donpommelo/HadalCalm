package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Particle;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.SyncType;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.hitbox.*;

public class TyrrazzanReaper extends RangedWeapon {

	private static final int clipSize = 10;
	private static final int ammoSize = 70;
	private static final float shootDelay = 0;
	private static final float reloadTime = 1.4f;
	private static final int reloadAmount = 0;
	private static final float recoil = 4.5f;
	private static final Vector2 projectileSize = new Vector2(45, 15);

	private static final float rangeMin = 40.0f;
	private static final float rangeMax = 1200.0f;

	private static final float sizeMax = 2.5f;
	private static final float sizeMin = 1.0f;

	private static final float shootCdMax = 0.6f;
	private static final float shootCdMin = 0.15f;

	private static final float lifespanMax = 0.8f;
	private static final float lifespanMin = 0.4f;

	private static final float particleSizeMax = 90.0f;
	private static final float particleSizeMin = 60.0f;

	private static final float baseDamageMax = 55.0f;
	private static final float baseDamageMin = 25.0f;

	private static final float knockbackMax = 19.0f;
	private static final float knockbackMin = 6.0f;

	private static final float projectileSpeedMax = 60.0f;
	private static final float projectileSpeedMin = 30.0f;

	private static final float spreadMax = 25.0f;

	private static final Sprite projSprite = Sprite.DIATOM_SHOT_B;
	private static final Sprite weaponSprite = Sprite.MT_GRENADE;
	private static final Sprite eventSprite = Sprite.P_GRENADE;

	public TyrrazzanReaper(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeedMax, shootCdMax, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespanMax);
	}

	private float reloadCounter;
	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {

		float effectiveRange = Math.max(Math.min(this.mouseLocation.dst(startPosition), rangeMax), rangeMin);
		effectiveRange = (effectiveRange - rangeMin) / (rangeMax - rangeMin);
		float cooldown = effectiveRange * (shootCdMax - shootCdMin) + shootCdMin;

		SyncedAttack.TYRAZZAN_REAPER.initiateSyncedAttackSingle(state, user, startPosition, startVelocity, effectiveRange);

		user.setShootCdCount(cooldown);
		gainClip(1);
		reloadCounter += cooldown;
		while (reloadCounter >= getUseCd()) {
			reloadCounter -= getUseCd();
			gainClip(-1);
		}
	}

	public static Hitbox createTyrrazzanReaper(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, float[] extraFields) {
		SoundEffect.MAGIC3_BURST.playSourced(state, startPosition, 0.5f, 0.75f);
		user.recoil(startVelocity, recoil);

		float effectiveRange = 0.0f;
		if (extraFields.length > 0) {
			effectiveRange = extraFields[0];
		}
		float size = effectiveRange * (sizeMax - sizeMin) + sizeMin;
		float velocity = effectiveRange * (projectileSpeedMax - projectileSpeedMin) + projectileSpeedMin;
		float damage = effectiveRange * (baseDamageMax - baseDamageMin) + baseDamageMin;
		float knockback = effectiveRange * (knockbackMax - knockbackMin) + knockbackMin;
		float lifespan = effectiveRange * (lifespanMax - lifespanMin) + lifespanMin;
		float particleSize = effectiveRange * (particleSizeMax - particleSizeMin) + particleSizeMin;
		int spread = (int) ((1 - effectiveRange) * spreadMax);

		Hitbox hbox = new RangedHitbox(state, startPosition, new Vector2(projectileSize).scl(size), lifespan,
				startVelocity.nor().scl(velocity), user.getHitboxfilter(), true, true, user, projSprite);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.DIATOM_TRAIL, 0.0f, 1.0f).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new DieParticles(state, hbox, user.getBodyData(), Particle.DIATOM_IMPACT_SMALL)
				.setParticleSize(particleSize).setSyncType(SyncType.NOSYNC));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), damage, knockback, DamageSource.TYRRAZZAN_REAPER,
				DamageTag.BULLET, DamageTag.RANGED));
		hbox.addStrategy(new ContactWallSound(state, hbox, user.getBodyData(), SoundEffect.BULLET_DIRT_HIT, 0.5f).setSynced(false));
		hbox.addStrategy(new Spread(state, hbox, user.getBodyData(), spread));

		return hbox;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) baseDamageMin),
				String.valueOf((int) baseDamageMax),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime),
				String.valueOf(shootCdMin),
				String.valueOf(shootCdMax)};
	}
}
