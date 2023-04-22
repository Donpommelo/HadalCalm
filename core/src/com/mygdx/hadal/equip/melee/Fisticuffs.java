package com.mygdx.hadal.equip.melee;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.Fist;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.MeleeWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

public class Fisticuffs extends MeleeWeapon {

	private static final float SWING_CD = 0.07f;
	private static final float PROJECTILE_SPEED = 24.0f;

	private static final Vector2 PROJECTILE_SIZE = Fist.PROJECTILE_SIZE;
	private static final float LIFESPAN = Fist.LIFESPAN;
	private static final float BASE_DAMAGE = Fist.BASE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_DEFAULT;
	private static final Sprite EVENT_SPRITE = Sprite.P_DEFAULT;

	public Fisticuffs(Player user) {
		super(user, SWING_CD, WEAPON_SPRITE, EVENT_SPRITE);
	}
	
	private final Vector2 startVelo = new Vector2();
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		SyncedAttack.FIST.initiateSyncedAttackSingle(state, user, user.getProjectileOrigin(weaponVelo, PROJECTILE_SIZE.x),
				startVelo.set(startVelocity).nor().scl(PROJECTILE_SPEED));
	}

	@Override
	public float getBotRangeMax() { return PROJECTILE_SPEED * LIFESPAN; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(SWING_CD)};
	}
}
