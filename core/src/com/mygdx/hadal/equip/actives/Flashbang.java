package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.active.FlashbangProjectile;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

public class Flashbang extends ActiveItem {

	private static final float MAX_CHARGE = 9.0f;
	private static final float PROJECTILE_SPEED = 30.0f;

	private static final float BASE_DAMAGE = FlashbangProjectile.BASE_DAMAGE;
	private static final Vector2 PROJECTILE_SIZE = FlashbangProjectile.PROJECTILE_SIZE;
	private static final float BLIND_DURATION = FlashbangProjectile.BLIND_DURATION;

	public Flashbang(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.FLASHBANG.initiateSyncedAttackSingle(state, user.getPlayer(), user.getPlayer().getProjectileOrigin(weaponVelo, PROJECTILE_SIZE.x),
				new Vector2(weaponVelo).nor().scl(PROJECTILE_SPEED));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf(BLIND_DURATION)};
	}
}
