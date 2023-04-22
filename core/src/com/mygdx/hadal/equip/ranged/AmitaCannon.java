package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.Amita;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

public class AmitaCannon extends RangedWeapon {

	private static final int CLIP_SIZE = 4;
	private static final int AMMO_SIZE = 32;
	private static final float SHOOT_CD = 0.4f;
	private static final float RELOAD_TIME = 1.6f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 38.0f;

	private static final Vector2 PROJECTILE_SIZE = Amita.PROJECTILE_SIZE;
	private static final float LIFESPAN = Amita.LIFESPAN;
	private static final float BASE_DAMAGE = Amita.BASE_DAMAGE;

	private static final int NUM_ORBITALS = Amita.NUM_ORBITALS;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_STORMCALLER;
	private static final Sprite EVENT_SPRITE = Sprite.P_STORMCALLER;

	public AmitaCannon(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT,
				true, WEAPON_SPRITE, EVENT_SPRITE, LIFESPAN, PROJECTILE_SIZE.x);
	}

	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.AMITA.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf(BASE_DAMAGE),
				String.valueOf(NUM_ORBITALS),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}
