package com.mygdx.hadal.equip.ranged;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.weapon.TeslaActivation;
import com.mygdx.hadal.battle.attacks.weapon.TeslaCoilProjectile;
import com.mygdx.hadal.effects.Sprite;
import com.mygdx.hadal.equip.RangedWeapon;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.states.PlayState;

public class TeslaCoil extends RangedWeapon {

	private static final int CLIP_SIZE = 3;
	private static final int AMMO_SIZE = 27;
	private static final float SHOOT_CD = 0.3f;
	private static final float RELOAD_TIME = 1.8f;
	private static final int RELOAD_AMOUNT = 0;
	private static final float PROJECTILE_SPEED = 100.0f;

	private static final Vector2 PROJECTILE_SIZE = TeslaCoilProjectile.PROJECTILE_SIZE;
	private static final float LIFESPAN = TeslaCoilProjectile.LIFESPAN;
	private static final float PULSE_INTERVAL = TeslaCoilProjectile.PULSE_INTERVAL;
	private static final float PULSE_DAMAGE = TeslaActivation.PULSE_DAMAGE;

	private static final Sprite WEAPON_SPRITE = Sprite.MT_STORMCALLER;
	private static final Sprite EVENT_SPRITE = Sprite.P_STORMCALLER;

	//kep track of all coils laid so far
	private final Vector2 pos1 = new Vector2();

	public TeslaCoil(Player user) {
		super(user, CLIP_SIZE, AMMO_SIZE, RELOAD_TIME, PROJECTILE_SPEED, SHOOT_CD, RELOAD_AMOUNT, true,
				WEAPON_SPRITE, EVENT_SPRITE, PROJECTILE_SIZE.x, LIFESPAN);
	}
	
	@Override
	public void fire(PlayState state, Player user, Vector2 startPosition, Vector2 startVelocity, short filter) {
		pos1.set(user.getMouseHelper().getPixelPosition());
		SyncedAttack.TESLA_COIL.initiateSyncedAttackSingle(state, user, startPosition, startVelocity, pos1.x, pos1.y);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) PULSE_DAMAGE),
				String.valueOf(LIFESPAN),
				String.valueOf((int) PULSE_INTERVAL),
				String.valueOf(CLIP_SIZE),
				String.valueOf(AMMO_SIZE),
				String.valueOf(RELOAD_TIME),
				String.valueOf(SHOOT_CD)};
	}
}
