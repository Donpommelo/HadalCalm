package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.Diatom;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

public class DiatomBurst extends RangedWeapon {

	private static final int CLIP_SIZE = 10;
	private static final int AMMO_SIZE = 70;
	private static final float RELOAD_TIME = 1.4f;
	private static final int RELOAD_AMOUNT = 0;

	private static final float RANGE_MIN = 40.0f;
	private static final float RANGE_MAX = 1200.0f;

	private static final float SHOOT_CD_MAX = 0.6f;
	private static final float SHOOT_CD_MIN = 0.15f;

	private static final Vector2 PROJECTILE_SIZE = Diatom.PROJECTILE_SIZE;
	private static final float PROJECTILE_SPEED_MAX = Diatom.PROJECTILE_SPEED_MAX;
	private static final float LIFESPAN_MAX = Diatom.LIFESPAN_MAX;
	private static final float BASE_DAMAGE_MAX = Diatom.BASE_DAMAGE_MAX;
	private static final float BASE_DAMAGE_MIN = Diatom.BASE_DAMAGE_MIN;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_GRENADE;
	private static final Sprite EVENT_SPRITE = Sprite.P_GRENADE;

	public DiatomBurst(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED_MAX, SHOOT_CD_MAX, RELOAD_AMOUNT, true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN_MAX);
	}

	private float reloadCounter;
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {

		float effectiveRange = Math.max(Math.min(this.mouseLocation.dst(startPosition), RANGE_MAX), RANGE_MIN);
		effectiveRange = (effectiveRange - RANGE_MIN) / (RANGE_MAX - RANGE_MIN);
		float cooldown = effectiveRange * (SHOOT_CD_MAX - SHOOT_CD_MIN) + SHOOT_CD_MIN;

		SyncedAttack.DIATOM.initiateSyncedAttackSingle(state, user, startPosition, startVelocity, effectiveRange);

		user.getShootHelper().setShootCdCount(cooldown);
		gainClip(1);
		reloadCounter += cooldown;
		while (reloadCounter >= getUseCd()) {
			reloadCounter -= getUseCd();
			gainClip(-1);
		}
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE_MIN),
				String.valueOf((int) BASE_DAMAGE_MAX),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD_MIN),
				String.valueOf(SHOOT_CD_MAX)};
	}
}
