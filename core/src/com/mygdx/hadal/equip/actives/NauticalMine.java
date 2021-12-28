package com.mygdx.hadal.equip.actives;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.bodies.hitboxes.SyncedAttack;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Froginald Frugwump
 */
public class NauticalMine extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.1f;
	private static final float maxCharge = 8.0f;
	
	private static final float projectileSpeed = 15.0f;
	
	public NauticalMine(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SyncedAttack.NAUTICAL_MINE.initiateSyncedAttackSingle(state, user.getPlayer(), user.getPlayer().getPixelPosition(),
				new Vector2(weaponVelo).nor().scl(projectileSpeed));
	}
}
