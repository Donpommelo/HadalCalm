package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.BoomerangProjectile;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

public class Boomerang extends RangedWeapon {

	private static final int CLIP_SIZE = 3;
	private static final int AMMO_SIZE = 24;
	private static final float SHOOT_CD = 0.75f;
	private static final float RELOAD_TIME = 0.75f;
	private static final int RELOAD_AMOUNT = 1;
	private static final float PROJECTILE_SPEED = 70.0f;

	private static final Vector2 PROJECTILE_SIZE = BoomerangProjectile.PROJECTILE_SIZE;
	private static final float LIFESPAN = BoomerangProjectile.LIFESPAN;
	private static final float BASE_DAMAGE = BoomerangProjectile.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_BOOMERANG;
	private static final Sprite EVENT_SPRITE = Sprite.P_BOOMERANG;

	public Boomerang(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN);
	}

	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.BOOMERANG.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	@Override
	public float getBotRangeMax() {
		return 24.0f;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}
