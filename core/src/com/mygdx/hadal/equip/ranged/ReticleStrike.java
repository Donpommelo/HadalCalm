package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.ReticleStrikeProjectile;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

public class ReticleStrike extends RangedWeapon {

	private static final int CLIP_SIZE = 1;
	private static final int AMMO_SIZE = 25;
	private static final float SHOOT_CD = 0.35f;
	private static final float RELOAD_TIME = 0.9f;
	private static final int RELOAD_AMOUNT = 1;
	private static final float PROJECTILE_SPEED = 80.0f;

	private static final Vector2 PROJECTILE_SIZE = ReticleStrikeProjectile.PROJECTILE_SIZE;
	private static final float LIFESPAN = ReticleStrikeProjectile.LIFESPAN;
	private static final float RETICLE_LIFESPAN = ReticleStrikeProjectile.RETICLE_LIFESPAN;
	private static final float EXPLOSION_DAMAGE = ReticleStrikeProjectile.EXPLOSION_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_IRONBALL;
	private static final Sprite EVENT_SPRITE = Sprite.P_IRONBALL;

	public ReticleStrike(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN);
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.RETICLE_STRIKE.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) EXPLOSION_DAMAGE),
				String.valueOf(RETICLE_LIFESPAN),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME)};
	}
}
