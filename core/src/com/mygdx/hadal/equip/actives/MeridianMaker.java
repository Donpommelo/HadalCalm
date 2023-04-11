package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.active.MeridianMakerProjectile;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Hatonio Hadoof
 */
public class MeridianMaker extends ActiveItem {

	private static final float MAX_CHARGE = 8.0f;

	private static final Vector2 PROJECTILE_SIZE = MeridianMakerProjectile.PROJECTILE_SIZE;
	private static final float BASE_DAMAGE = MeridianMakerProjectile.BASE_DAMAGE;
	private static final float LIFESPAN = MeridianMakerProjectile.LIFESPAN;
	private static final float PROJECTILE_SPEED = MeridianMakerProjectile.PROJECTILE_SPEED;

	public MeridianMaker(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.MERIDIAN_MAKER.initiateSyncedAttackSingle(state, user.getPlayer(),
				user.getPlayer().getProjectileOrigin(weaponVelo, PROJECTILE_SIZE.x),
				new Vector2(weaponVelo).nor().scl(PROJECTILE_SPEED));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) BASE_DAMAGE),
				String.valueOf((int) LIFESPAN)};
	}
}
