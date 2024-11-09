package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntArray;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.RecombinantShot;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

public class RecombinantShotrifle extends RangedWeapon {

	private static final int CLIP_SIZE = 1;
	private static final int AMMO_SIZE = 25;
	private static final float SHOOT_CD = 0.65f;
	private static final float RELOAD_TIME = 1.5f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 90.0f;
	private static final int NUM_PROJ = 5;
	private static final int PROJ_SPREAD = 8;

	private static final Vector2 PROJECTILE_SIZE = RecombinantShot.PROJECTILE_SIZE;
	private static final float LIFESPAN = RecombinantShot.LIFESPAN;
	private static final float BASE_DAMAGE = RecombinantShot.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_SHOTGUN;
	private static final Sprite EVENT_SPRITE = Sprite.P_SHOTGUN;

	private static final int[] PROJ_ARRAY = {0, 1, 2, 3, 4};
	private static final IntArray PROJ = new IntArray(PROJ_ARRAY);

	public RecombinantShotrifle(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, WEAPON_SPRITE, EVENT_SPRITE,
				PROJECTILE_SIZE.x, LIFESPAN);
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		Vector2[] positions = new Vector2[NUM_PROJ];
		Vector2[] velocities = new Vector2[NUM_PROJ];
		float[] extraFields = new float[NUM_PROJ];
		PROJ.shuffle();

		for (int i = 0; i < NUM_PROJ; i++) {
			positions[i] = startPosition;
			velocities[i] = setShotVelocity(i, startVelocity);
			extraFields[i] = PROJ.get(i);
		}
		SyncedAttack.RECOMBINANT_SHOT.initiateSyncedAttackMulti(state, user, startVelocity, positions, velocities, extraFields);
	}

	private Vector2 setShotVelocity(int projectile, Vector2 startVelocity) {
		Vector2 noteVelo = new Vector2();
		float angle = startVelocity.angleDeg() - (PROJ_SPREAD * 3) + PROJ_SPREAD * projectile;
		noteVelo.set(startVelocity).setAngleDeg(angle);
		return noteVelo;
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(NUM_PROJ),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}
