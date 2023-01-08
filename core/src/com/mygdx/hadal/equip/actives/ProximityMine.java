package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.battle.WeaponUtils;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Dregnatio Dujandro
 */
public class ProximityMine extends ActiveItem {

	private static final float MAX_CHARGE = 12.0f;
	
	private static final float EXPLOSION_DAMAGE = 100.0f;

	public ProximityMine(Schmuck user) {
		super(user, MAX_CHARGE);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.PROXIMITY_MINE.initiateSyncedAttackSingle(state, user.getPlayer(), user.getPlayer().getPixelPosition(),
				new Vector2(), DamageSource.PROXIMITY_MINE, EXPLOSION_DAMAGE);
	}

	@Override
	public String[] getDescFields() {
		return new String[] {
				String.valueOf((int) MAX_CHARGE),
				String.valueOf((int) WeaponUtils.PRIME_TIME),
				String.valueOf((int) EXPLOSION_DAMAGE)};
	}
}
