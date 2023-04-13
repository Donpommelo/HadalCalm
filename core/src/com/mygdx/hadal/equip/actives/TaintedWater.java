package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.active.TaintedWaterProjectile;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Lassafras Lickette
 */
public class TaintedWater extends ActiveItem {

	private static final float MAX_CHARGE = 10.0f;

	private static final Vector2 PROJECTILE_SIZE = TaintedWaterProjectile.PROJECTILE_SIZE;
	private static final float POISON_DAMAGE = TaintedWaterProjectile.POISON_DAMAGE;
	private static final float POISON_DURATION = TaintedWaterProjectile.POISON_DURATION;
	private static final float PROJECTILE_SPEED = TaintedWaterProjectile.PROJECTILE_SPEED;

	public TaintedWater(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.TAINTED_WATER.initiateSyncedAttackSingle(state, user.getPlayer(),
				user.getPlayer().getProjectileOrigin(weaponVelo, PROJECTILE_SIZE.x),
				new Vector2(weaponVelo).nor().scl(PROJECTILE_SPEED));
	}
	
	@Override
	public float getUseDuration() { return POISON_DURATION; }

	@Override
	public float getBotRangeMin() { return 11.0f; }

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) POISON_DURATION),
				String.valueOf((int) (POISON_DAMAGE * 60))};
	}
}
