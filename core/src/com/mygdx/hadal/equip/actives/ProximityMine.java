package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.battle.DamageSource;
import com.mygdx.hadal.battle.SyncedAttack;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.entities.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Dregnatio Dujandro
 */
public class ProximityMine extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.1f;
	private static final float maxCharge = 12.0f;
	
	private static final float explosionDamage = 100.0f;

	public ProximityMine(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.PROXIMITY_MINE.initiateSyncedAttackSingle(state, user.getPlayer(), user.getPlayer().getPixelPosition(),
				new Vector2(), DamageSource.PROXIMITY_MINE, explosionDamage);
	}
}
