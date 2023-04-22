package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.Pepper;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

public class Peppergrinder extends RangedWeapon {

	private static final int CLIP_SIZE = 60;
	private static final int AMMO_SIZE = 360;
	private static final float SHOOT_CD = 0.06f;
	private static final float RELOAD_TIME = 1.5f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 31.5f;
	private static final int MAX_SPREAD = 24;
	private static final int SPREAD_CHANGE = 8;

	private static final Vector2 PROJECTILE_SIZE = Pepper.PROJECTILE_SIZE;
	private static final float LIFESPAN = Pepper.LIFESPAN;
	private static final float BASE_DAMAGE = Pepper.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_BOILER;
	private static final Sprite EVENT_SPRITE = Sprite.P_BOILER;

	public Peppergrinder(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN);
	}

	private int spread;
	private boolean sweepingUp;
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.PEPPER.initiateSyncedAttackSingle(state, user, startPosition, startVelocity, spread);

		if (sweepingUp) {
			spread += SPREAD_CHANGE;
			if (spread >= MAX_SPREAD) {
				sweepingUp = false;
			}
		} else {
			spread -= SPREAD_CHANGE;
			if (spread <= -MAX_SPREAD) {
				sweepingUp = true;
			}
		}
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
