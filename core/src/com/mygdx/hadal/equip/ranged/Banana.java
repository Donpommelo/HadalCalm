package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.BananaProjectile;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class Banana extends RangedWeapon {

	private static final int CLIP_SIZE = 3;
	private static final int AMMO_SIZE = 27;
	private static final float SHOOT_CD = 0.0f;
	private static final float RELOAD_TIME = 0.6f;
	private static final int RELOAD_AMOUNT = 1;
	private static final float PROJECTILE_SPEED = 15.0f;
	private static final float PROJECTILE_MAX_SPEED = 60.0f;
	private static final float MAX_CHARGE = 0.3f;

	private static final Vector2 PROJECTILE_SIZE = BananaProjectile.PROJECTILE_SIZE;
	private static final float LIFESPAN = BananaProjectile.LIFESPAN;
	private static final float BASE_DAMAGE = BananaProjectile.BASE_DAMAGE;
	private static final float EXPLOSION_DAMAGE = BananaProjectile.EXPLOSION_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_ICEBERG;
	private static final Sprite EVENT_SPRITE = Sprite.P_ICEBERG;

	public Banana(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT,true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN, MAX_CHARGE);
	}

	@Override
	public void mouseClicked(float delta, PlayState state, PlayerBodyData playerData, short faction, Vector2 mousePosition) {
		super.mouseClicked(delta, state, playerData, faction, mousePosition);

		if (reloading || getClipLeft() == 0) {
			return;
		}

		charging = true;

		//while held, build charge until maximum (if not reloading)
		if (chargeCd < getChargeTime()) {
			setChargeCd(chargeCd + delta);
		}
	}

	@Override
	public void execute(PlayState state, PlayerBodyData playerData) {
	}

	@Override
	public void release(PlayState state, PlayerBodyData playerData) {
		super.execute(state, playerData);
		charging = false;
		chargeCd = 0;
	}

	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {

		//velocity scales with charge percentage
		float velocity = chargeCd / getChargeTime() * (PROJECTILE_MAX_SPEED - PROJECTILE_SPEED) + PROJECTILE_SPEED;
		SyncedAttack.BANANA.initiateSyncedAttackSingle(state, user, startPosition, new Vector2(startVelocity).nor().scl(velocity));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf((int) EXPLOSION_DAMAGE),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(MAX_CHARGE)};
	}
}
