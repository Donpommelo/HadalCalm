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

public class Vajra extends RangedWeapon {

	private static final int clipSize = 3;
	private static final int ammoSize = 21;
	private static final float shootCd = 0.4f;
	private static final float shootDelay = 0;
	private static final float reloadTime = 1.1f;
	private static final int reloadAmount = 0;

	private static final float recoil = 4.0f;
	private static final float baseDamage = 44.0f;
	private static final float knockback = 5.0f;
	private static final float projectileSpeedStart = 45.0f;
	private static final Vector2 projectileSize = new Vector2(70, 24);
	private static final float lifespan = 1.0f;

	private static final float chainDamage = 20.0f;
	private static final int chainRadius = 20;
	private static final int chainAmount = 5;

	private static final Sprite weaponSprite = Sprite.MT_CHAINLIGHTNING;
	private static final Sprite eventSprite = Sprite.P_CHAINLIGHTNING;

	public Vajra(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeedStart, shootCd, shootDelay, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}

	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.VAJRA.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createVajra(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.THUNDER.playSourced(state, startPosition, 0.5f);
		user.recoil(startVelocity, recoil);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxfilter(),
				true, true, user, Sprite.LIGHTNING);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new AdjustAngle(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitLoseDurability(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitShock(state, hbox, user.getBodyData(), chainDamage, chainRadius, chainAmount,
				user.getHitboxfilter(), DamageSource.VAJRA));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageSource.VAJRA,
				DamageTag.LIGHTNING, DamageTag.RANGED));
		hbox.addStrategy(new CreateParticles(state, hbox, user.getBodyData(), Particle.LIGHTNING_CHARGE, 0.0f, 1.0f).setSyncType(SyncType.NOSYNC));

		return hbox;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) baseDamage),
				String.valueOf((int) chainDamage),
				String.valueOf(chainAmount),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime),
				String.valueOf(shootCd)};
	}
}
