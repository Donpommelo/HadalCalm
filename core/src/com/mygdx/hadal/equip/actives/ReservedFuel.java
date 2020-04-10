package com.mygdx.hadal.equip.actives;

import com.mygdx.hadal.audio.SoundEffect;
import com.mygdx.hadal.equip.ActiveItem;
import com.mygdx.hadal.schmucks.bodies.Schmuck;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.statuses.StatChangeStatus;
import com.mygdx.hadal.utils.Stats;

public class ReservedFuel extends ActiveItem {

	private final static float usecd = 0.0f;
	private final static float usedelay = 0.0f;
	private final static float maxCharge = 20.0f;
	
	private final static float duration = 5.0f;
	private final static float power = 18.0f;
	
	public ReservedFuel(Schmuck user) {
		super(user, usecd, usedelay, maxCharge, chargeStyle.byTime);
	}
	
	@Override
	public void useItem(PlayState state, PlayerBodyData user) {
		SoundEffect.MAGIC2_FUEL.playUniversal(state, user.getPlayer().getPixelPosition(), 0.5f, false);

		user.addStatus(new StatChangeStatus(state, duration, Stats.FUEL_REGEN, power, user, user));
	}
	
	@Override
	public float getUseDuration() { return duration; }
}
