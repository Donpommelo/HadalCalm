package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.Puffball;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

public class Puffballer extends RangedWeapon {

	private static final int CLIP_SIZE = 1;
	private static final int AMMO_SIZE = 25;
	private static final float SHOOT_CD = 0.8f;
	private static final float RELOAD_TIME = 1.75f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 50.0f;
	private static final float FRAG_SPEED = 6.0f;
	private static final float FRAG_VELO_SPREAD = 1.2f;

	private static final Vector2 PROJECTILE_SIZE = Puffball.PROJECTILE_SIZE;
	private static final float LIFESPAN = Puffball.LIFESPAN;
	private static final float BASE_DAMAGE = Puffball.BASE_DAMAGE;

	private static final float SPORE_FRAG_LIFESPAN = Puffball.SPORE_FRAG_LIFESPAN;
	private static final float SPORE_FRAG_DAMAGE = Puffball.SPORE_FRAG_DAMAGE;
	private static final int SPORE_FRAG_NUMBER = Puffball.SPORE_FRAG_NUMBER;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_TORPEDO;
	private static final Sprite EVENT_SPRITE = Sprite.P_TORPEDO;

	private final Vector2 pos1 = new Vector2();

	public Puffballer(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN);
	}

	private final Vector2 newVelocity = new Vector2();
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		pos1.set(user.getMouseHelper().getPixelPosition());
		float[] fragAngles = new float[SPORE_FRAG_NUMBER * 2 + 2];
		fragAngles[0] = pos1.x;
		fragAngles[1] = pos1.y;

		for (int i = 0; i < SPORE_FRAG_NUMBER; i++) {
			newVelocity.setToRandomDirection().scl(FRAG_SPEED).scl(MathUtils.random() * FRAG_VELO_SPREAD + 1 - FRAG_VELO_SPREAD / 2);
			fragAngles[2 * i + 2] = newVelocity.x;
			fragAngles[2 * i + 3] = newVelocity.y;
		}

		SyncedAttack.PUFFBALL.initiateSyncedAttackSingle(state, user, startPosition, startVelocity, fragAngles);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf((int) SPORE_FRAG_DAMAGE),
				String.valueOf(SPORE_FRAG_NUMBER),
				String.valueOf((int) SPORE_FRAG_LIFESPAN),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}
