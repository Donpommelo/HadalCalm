package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.StickyBomb;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.entities.hitboxes.Hitbox;
import com.mygdx.hadal.states.PlayState;

public class StickyBombLauncher extends RangedWeapon {

	private static final int CLIP_SIZE = 6;
	private static final int AMMO_SIZE = 36;
	private static final float SHOOT_CD = 0.35f;
	private static final float RELOAD_TIME = 1.25f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 40.0f;

	private static final Vector2 PROJECTILE_SIZE = StickyBomb.PROJECTILE_SIZE;
	private static final float LIFESPAN = StickyBomb.LIFESPAN;
	private static final float EXPLOSION_DAMAGE = StickyBomb.EXPLOSION_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_STICKYBOMB;
	private static final Sprite EVENT_SPRITE = Sprite.P_STICKYBOMB;

	public StickyBombLauncher(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, false,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN);
	}

	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.STICKY_BOMB.initiateSyncedAttackSingle(state, user, startPosition, startVelocity);
	}

	@Override
	public void processEffects(PlayState state, float delta, Vector2 playerPosition) {
		if (reloading) {
			//upon reload, detonate all laid bombs
			for (Hitbox bomb : user.getSpecialWeaponHelper().getStickyBombs()) {
				if (bomb.isAlive()) {
					bomb.die();
				}
			}
			user.getSpecialWeaponHelper().getStickyBombs().clear();
		}
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) EXPLOSION_DAMAGE),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}