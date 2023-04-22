package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.CR4P;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

public class CR4PCannon extends RangedWeapon {

	private static final int CLIP_SIZE = 2;
	private static final int AMMO_SIZE = 22;
	private static final float SHOOT_CD = 0.15f;
	private static final float RELOAD_TIME = 1.2f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 35.0f;
	private static final int NUM_PROJ = 9;

	private static final Vector2 PROJECTILE_SIZE = CR4P.PROJECTILE_SIZE;
	private static final float LIFESPAN = CR4P.LIFESPAN;
	private static final float BASE_DAMAGE = CR4P.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_SHOTGUN;
	private static final Sprite EVENT_SPRITE = Sprite.P_SHOTGUN;
	
	public CR4PCannon(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN);
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		Vector2[] positions = new Vector2[NUM_PROJ];
		Vector2[] velocities = new Vector2[NUM_PROJ];
		for (int i = 0; i < NUM_PROJ; i++) {
			positions[i] = startPosition;
			velocities[i] = startVelocity;
		}
		SyncedAttack.CR4P.initiateSyncedAttackMulti(state, user, startVelocity, positions, velocities);
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
