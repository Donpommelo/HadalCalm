package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.IronBall;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

public class IronBallLauncher extends RangedWeapon {

	private static final int CLIP_SIZE = 1;
	private static final int AMMO_SIZE = 23;
	private static final float SHOOT_CD = 0.15f;
	private static final float RELOAD_TIME = 0.75f;
	private static final int RELOAD_AMOUNT = 1;
	private static final float PROJECTILE_SPEED = 60.0f;

	private static final Vector2 PROJECTILE_SIZE = IronBall.PROJECTILE_SIZE;
	private static final float LIFESPAN = IronBall.LIFESPAN;
	private static final float BASE_DAMAGE = IronBall.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_IRONBALL;
	private static final Sprite EVENT_SPRITE = Sprite.P_IRONBALL;
	
	public IronBallLauncher(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN);
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.IRON_BALL.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME)};
	}
}
