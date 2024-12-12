package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.BouncingBladeProjectile;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

public class BouncingBlade extends RangedWeapon {

	private static final int CLIP_SIZE = 5;
	private static final int AMMO_SIZE = 30;
	private static final float SHOOT_CD = 0.7f;
	private static final float RELOAD_TIME = 1.4f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 59.0f;

	private static final Vector2 PROJECTILE_SIZE = BouncingBladeProjectile.PROJECTILE_SIZE;
	private static final float LIFESPAN = BouncingBladeProjectile.LIFESPAN;
	private static final int DURABILITY = BouncingBladeProjectile.DURABILITY;
	private static final float BASE_DAMAGE = BouncingBladeProjectile.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_BLADEGUN;
	private static final Sprite EVENT_SPRITE = Sprite.P_BLADEGUN;

	public BouncingBlade(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, WEAPON_SPRITE, EVENT_SPRITE,
				PROJECTILE_SIZE.x, LIFESPAN);
	}

	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.BOUNCING_BLADE.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(DURABILITY),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}
