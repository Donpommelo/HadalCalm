package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.attacks.general.NauticalMineProjectile;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Froginald Frugwump
 */
public class NauticalMine extends ActiveItem {

	private static final float MAX_CHARGE = 8.0f;
	
	private static final float PROJECTILE_SPEED = 15.0f;
	
	public NauticalMine(Player user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.NAUTICAL_MINE.initiateSyncedAttackSingle(state, user.getPlayer(), user.getPlayer().getPixelPosition(),
				new Vector2(weaponVelo).nor().scl(PROJECTILE_SPEED));
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) NauticalMineProjectile.NAUTICAL_MINE_EXPLOSION_DAMAGE),
				String.valueOf((int) NauticalMineProjectile.NAUTICAL_MINE_LIFESPAN)};
	}
}
