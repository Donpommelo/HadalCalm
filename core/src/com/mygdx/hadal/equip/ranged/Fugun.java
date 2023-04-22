package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.Fugu;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

public class Fugun extends RangedWeapon {

	private static final int CLIP_SIZE = 2;
	private static final int AMMO_SIZE = 14;
	private static final float SHOOT_CD = 0.2f;
	private static final float RELOAD_TIME = 1.1f;
	private static final int RELOAD_AMOUNT = 1;
	private static final float PROJECTILE_SPEED = 45.0f;

	private static final Vector2 PROJECTILE_SIZE = Fugu.PROJECTILE_SIZE;
	private static final float LIFESPAN = Fugu.LIFESPAN;
	private static final float BASE_DAMAGE = Fugu.BASE_DAMAGE;

	private static final float POISON_DAMAGE = Fugu.POISON_DAMAGE;
	private static final float POISON_DURATION = Fugu.POISON_RADIUS;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_IRONBALL;
	private static final Sprite EVENT_SPRITE = Sprite.P_IRONBALL;

	public Fugun(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN);
	}

	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.FUGU.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf((int) (POISON_DAMAGE * 60)),
				String.valueOf((int) POISON_DURATION),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}