package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Shocked;

public class PlusMinus extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 14.0f;
	
	
	private final static float chainDamage = 20.0f;
	private final static int chainRadius = 15;
	private final static int chainAmount = 8;
	
	public PlusMinus(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.THUNDER.playUniversal(state, user.getPlayer().getPixelPosition(), 0.5f, false);
		user.addStatus(new Shocked(state, user, user, chainDamage, chainRadius, chainAmount, user.getSchmuck().getHitboxfilter()));
	}
}
