package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.StutterLaser;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.FiringWeapon;

public class StutterGun extends RangedWeapon {

	private static final int CLIP_SIZE = 5;
	private static final int AMMO_SIZE = 35;
	private static final float SHOOT_CD = 0.6f;
	private static final float RELOAD_TIME = 1.0f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 45.0f;
	private static final float PROC_CD = 0.09f;
	private static final float FIRE_DURATION = 0.5f;
	private static final int SHOT_NUMBER = 5;

	private static final Vector2 PROJECTILE_SIZE = StutterLaser.PROJECTILE_SIZE;
	private static final float LIFESPAN = StutterLaser.LIFESPAN;
	private static final float BASE_DAMAGE = StutterLaser.BASE_DAMAGE;

	private static final Sprite weaponSprite = Sprite.MT_LASERRIFLE;
	private static final Sprite eventSprite = Sprite.P_LASERRIFLE;
	
	public StutterGun(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, true,
				weaponSprite, eventSprite, PROJECTILE_SIZE.x, LIFESPAN);
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.STUTTER_LASER.initiateSyncedAttackSingle(state, user, startPosition, startVelocity.nor().scl(PROJECTILE_SPEED));
	}

	@Override
	public void execute(PlayState state, PlayerBodyData playerData) {
		if (processClip()) {
			playerData.addStatus(new FiringWeapon(state, FIRE_DURATION, playerData, playerData, PROJECTILE_SIZE.x, PROC_CD, SHOT_NUMBER, this));
		}
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf((int) (FIRE_DURATION / PROC_CD)),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}
