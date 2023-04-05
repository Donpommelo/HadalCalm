package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.VajraProjectile;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

public class Vajra extends RangedWeapon {

	private static final int CLIP_SIZE = 3;
	private static final int AMMO_SIZE = 21;
	private static final float SHOOT_CD = 0.4f;
	private static final float RELOAD_TIME = 1.1f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED_START = 45.0f;

	private static final Vector2 PROJECTILE_SIZE = VajraProjectile.PROJECTILE_SIZE;
	private static final float LIFESPAN = VajraProjectile.LIFESPAN;
	private static final float BASE_DAMAGE = VajraProjectile.BASE_DAMAGE;

	private static final float CHAIN_DAMAGE = VajraProjectile.CHAIN_DAMAGE;
	private static final int CHAIN_AMOUNT = VajraProjectile.CHAIN_AMOUNT;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_CHAINLIGHTNING;
	private static final Sprite EVENT_SPRITE = Sprite.P_CHAINLIGHTNING;

	public Vajra(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED_START, SHOOT_CD, RELOAD_AMOUNT, true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN);
	}

	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.VAJRA.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf((int) CHAIN_DAMAGE),
				String.valueOf(CHAIN_AMOUNT),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}
