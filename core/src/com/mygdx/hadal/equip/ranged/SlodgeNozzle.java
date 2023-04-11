package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.Slodge;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.FiringWeapon;

public class SlodgeNozzle extends RangedWeapon {

	private static final int CLIP_SIZE = 1;
	private static final int AMMO_SIZE = 25;
	private static final float SHOOT_CD = 0.25f;
	private static final float RELOAD_TIME = 1.2f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED_MAX = 35.0f;
	private static final float PROC_CD = 0.075f;
	private static final float FIRE_DURATION = 1.5f;

	private static final Vector2 PROJECTILE_SIZE = Slodge.PROJECTILE_SIZE;
	private static final float LIFESPAN = Slodge.LIFESPAN_MAX;
	private static final float BASE_DAMAGE = Slodge.BASE_DAMAGE;
	private static final int SHOT_NUMBER = Slodge.SHOT_NUMBER;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_SLODGEGUN;
	private static final Sprite EVENT_SPRITE = Sprite.P_SLODGEGUN;
	
	public SlodgeNozzle(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED_MAX, SHOOT_CD, RELOAD_AMOUNT, true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN);
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {

		int shotNum = user.getSpecialWeaponHelper().getSprayWeaponShotNumber();
		SyncedAttack.SLODGE.initiateSyncedAttackSingle(state, user, startPosition, startVelocity, shotNum);
	}

	@Override
	public void execute(PlayState state, PlayerBodyData playerData) {
		if (processClip()) {
			playerData.addStatus(new FiringWeapon(state, FIRE_DURATION, playerData, playerData, PROJECTILE_SIZE.x,
					PROC_CD, SHOT_NUMBER, this));
		}
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(FIRE_DURATION),
				String.valueOf(PROC_CD),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}
