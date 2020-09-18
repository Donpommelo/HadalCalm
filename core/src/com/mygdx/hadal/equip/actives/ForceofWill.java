package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.Invulnerability;

public class ForceofWill extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 16.0f;
	
	private final static float duration = 3.0f;
	
	public ForceofWill(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.MAGIC18_BUFF.playUniversal(state, user.getPlayer().getPixelPosition(), 0.5f, false);
		user.addStatus(new Invulnerability(state, duration, user, user));
	}
	
	@Override
	public float getUseDuration() { return duration; }
}
