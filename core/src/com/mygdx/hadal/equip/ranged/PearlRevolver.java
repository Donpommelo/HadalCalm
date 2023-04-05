package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.Pearl;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class PearlRevolver extends RangedWeapon {

	private static final int CLIP_SIZE = 6;
	private static final int AMMO_SIZE = 48;
	private static final float SHOOT_CD = 0.3f;
	private static final float RELOAD_TIME = 0.6f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 55.0f;

	private static final Vector2 PROJECTILE_SIZE = Pearl.PROJECTILE_SIZE;
	private static final float LIFESPAN = Pearl.LIFESPAN;
	private static final float BASE_DAMAGE = Pearl.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_GRENADE;
	private static final Sprite EVENT_SPRITE = Sprite.P_GRENADE;

	public PearlRevolver(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN);
	}

	@Override
	public void release(PlayState state, PlayerBodyData playerData) {
		super.release(state, playerData);

		//Rapidly clicking this weapon incurs no cooldown between shots
		playerData.getPlayer().getShootHelper().setShootCdCount(0);
	}

	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.PEARL.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
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
