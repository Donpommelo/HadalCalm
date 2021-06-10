package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Shocked;

/**
 * @author Borpwood Bluwood
 */
public class PlusMinus extends ActiveItem {

	private static final float usecd = 0.0f;
	private static final float usedelay = 0.0f;
	private static final float maxCharge = 12.0f;
	
	
	private static final float chainDamage = 18.0f;
	private static final int chainRadius = 15;
	private static final int chainAmount = 8;
	
	public PlusMinus(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.THUNDER.playUniversal(state, user.getPlayer().getPixelPosition(), 0.5f, false);
		user.addStatus(new Shocked(state, user, user, chainDamage, chainRadius, chainAmount, user.getSchmuck().getHitboxfilter()));
	}
}
