package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.Bee;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

public class BeeGun extends RangedWeapon {

	private static final int CLIP_SIZE = 20;
	private static final int AMMO_SIZE = 96;
	private static final float SHOOT_CD = 0.4f;
	private static final float RELOAD_TIME = 1.9f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED_START = 20.0f;

	private static final Vector2 PROJECTILE_SIZE = Bee.PROJECTILE_SIZE;
	private static final float LIFESPAN = Bee.LIFESPAN;
	private static final float BEE_BASE_DAMAGE = Bee.BEE_BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_BEEGUN;
	private static final Sprite EVENT_SPRITE = Sprite.P_BEEGUN;

	public BeeGun(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED_START, SHOOT_CD, RELOAD_AMOUNT,true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN);
	}

	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.BEE.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BEE_BASE_DAMAGE),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}
