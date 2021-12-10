package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.equip.WeaponUtils;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;

/**
 * @author Dregnatio Dujandro
 */
public class ProximityMine extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.1f;
	private static final float maxCharge = 12.0f;
	
	private static final float mineLifespan = 18.0f;

	private static final float projectileSpeed = 60.0f;
	
	private static final int explosionRadius = 250;
	private static final float explosionDamage = 100.0f;
	private static final float explosionKnockback = 50.0f;
	
	public ProximityMine(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		WeaponUtils.createProximityMine(state, user.getPlayer().getPixelPosition(), user.getPlayer(), projectileSpeed,
			mineLifespan, explosionDamage, explosionKnockback, explosionRadius);
	}
}
