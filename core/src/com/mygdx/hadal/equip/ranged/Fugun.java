package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.schmucks.entities.hitboxes.RangedHitbox;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.battle.DamageTag;
import com.mygdx.hadal.strategies.hitbox.*;

public class Fugun extends RangedWeapon {

	private static final int clipSize = 2;
	private static final int ammoSize = 14;
	private static final float shootCd = 0.2f;
	private static final float reloadTime = 1.1f;
	private static final int reloadAmount = 1;
	private static final float baseDamage = 35.0f;
	private static final float recoil = 7.5f;
	private static final float knockback = 12.5f;
	private static final float projectileSpeed = 45.0f;
	private static final Vector2 projectileSize = new Vector2(36, 36);
	private static final float lifespan = 1.2f;

	private static final int poisonRadius = 250;
	private static final float poisonDamage = 0.75f;
	private static final float poisonDuration = 4.0f;

	private static final Sprite projSprite = Sprite.FUGU;
	private static final Sprite weaponSprite = Sprite.MT_IRONBALL;
	private static final Sprite eventSprite = Sprite.P_IRONBALL;

	public Fugun(Schmuck user) {
		super(user, clipSize, ammoSize, reloadTime, projectileSpeed, shootCd, reloadAmount, true,
				weaponSprite, eventSprite, projectileSize.x, lifespan);
	}

	@Override
	public void fire(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.FUGU.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	public static Hitbox createFugu(PlayState state, Schmuck user, Vector2 startPosition, Vector2 startVelocity) {
		SoundEffect.LAUNCHER4.playSourced(state, startPosition, 0.25f);
		user.recoil(startVelocity, recoil);

		Hitbox hbox = new RangedHitbox(state, startPosition, projectileSize, lifespan, startVelocity, user.getHitboxFilter(),
				true, true, user, projSprite);

		hbox.addStrategy(new ControllerDefault(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactUnitDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new ContactWallDie(state, hbox, user.getBodyData()));
		hbox.addStrategy(new DamageStandard(state, hbox, user.getBodyData(), baseDamage, knockback, DamageSource.FUGUN,
				DamageTag.POISON, DamageTag.RANGED));
		hbox.addStrategy(new DieRagdoll(state, hbox, user.getBodyData(), false));
		hbox.addStrategy(new DieSound(state, hbox, user.getBodyData(), SoundEffect.DEFLATE, 0.25f).setSynced(false));

		if (state.isServer()) {
			hbox.addStrategy(new DiePoison(state, hbox, user.getBodyData(), poisonRadius, poisonDamage, poisonDuration,
					(short) 0, DamageSource.FUGUN));
		}

		return hbox;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) baseDamage),
				String.valueOf((int) (poisonDamage * 60)),
				String.valueOf((int) poisonDuration),
				String.valueOf(clipSize),
				String.valueOf(ammoSize),
				String.valueOf(reloadTime),
				String.valueOf(shootCd)};
	}
}